package controllers;

import authority.Secured;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.Auto;
import models.AutoFeature;
import models.AutoPicture;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DB;
import services.S3Service;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

@Security.Authenticated(Secured.class)
public class AcAutoController extends Controller {
    private final FormFactory formFactory;
    private final Form<Auto> autoForm;

    @Inject
    public AcAutoController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.autoForm = formFactory.form(Auto.class);
    }

    public Result index(Http.Request request) {
        return listings(request);
    }
    
    public static Result listings(Http.Request request) {

        Param param = Util.param(20, request);
        param.setSort("id");
        param.setOrder("desc");

        DB.Filter filter = DB.where();
        filter.field("account", Auth.getAccount(request));

        populateFilter(request, filter);

        List<Auto> list = DB.find(Auto.class, filter, param);
        Long total = DB.count(Auto.class, filter);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);

        map.put("param", param);

        return ok(views.html.dash.autos.render(map, request));
    }

    public static DB.Filter populateFilter(Http.Request request, DB.Filter filter) {
        Optional<String> trashedOp = request.queryString("trashed");
        if(trashedOp.isPresent()) {
            String trashed = trashedOp.get().trim();
            if("true".equals(trashed)) {
                filter.field("trashed").eq(true);
            } else if("all".equals(trashed)){
                //take all
            } else {
                filter.field("trashed").eq(false);
            }
        } else {
            filter.field("trashed").eq(false);
        }

        Optional<String> publishedOp = request.queryString("published");
        if(publishedOp.isPresent()) {
            String published = publishedOp.get().trim();
            if("true".equals(published)) {
                filter.field("published").eq(true);
            } else if("false".equals(published)){
                filter.field("published").eq(false);
            }
        }

        Optional<String> yearOp = request.queryString("year");
        if(yearOp.isPresent()) {
            String year = yearOp.get().trim();
            if(Util.isNumeric(year)) {
                filter.field("year").eq(Integer.parseInt(year));
            }
        }

        Optional<String> stateOp = request.queryString("stateId");
        if(stateOp.isPresent()) {
            String id = stateOp.get().trim();
            if(Util.isNumeric(id)) {
                filter.field("locality.parent.id").eq(Long.parseLong(id));
            }
        }

        Optional<String> makeOp = request.queryString("makeId");
        if(makeOp.isPresent()) {
            String id = makeOp.get().trim();
            if(Util.isNumeric(id)) {
                filter.field("model.parent.id").eq(Long.parseLong(id));
            }
        }

        List<String> filters = Arrays.asList(
                "bodyTypeId",
                "transmissionId",
                "fuelTypeId",
                "colorExteriorId",
                "colorInteriorId",
                "conditionId",
                "modelId",
                "localityId");

        request.queryString().forEach((key,v) -> {
            String value = v[0];
            if(filters.contains(key) && Util.isNumeric(value)) {
                filter.field(key.replace("Id", ".id")).eq(Long.parseLong(value));
            }
        });

        return filter;
    }

    public Result single(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        List<Auto> list = Arrays.asList(auto);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", 1L);
        map.put("param", new Param(0,1));
        return ok(views.html.dash.autos.render(map, request));
    }

    public Result add(Http.Request request) {
        Account account = Auth.getAccount(request);
        if(Util.isEmpty(account.businessName) || Util.isEmpty(account.phone)) {
            return redirect(routes.AcProfileController.edit())
                    .flashing("flashed", "Please complete your profile first.");
        }

        if(!account.active) {
            return redirect(routes.AcDashboardController.index())
                    .flashing("flashed", "Your account is inactive. Please contact admin");
        }

        Auto auto = new Auto();
        auto.uuid = Util.generateUniqueId(50);
        Form<Auto> formData = autoForm.fill(auto);
        return ok(views.html.dash.post.render(formData, auto, request));
    }

    public Result create(Http.Request request) {
        Form<Auto> filledForm = autoForm.bindFromRequest(request);

        Auto auto = new Auto();
        auto.uuid = filledForm.rawData().get("uuid");

        filledForm = AdAutoCrud.validateAd(filledForm);

        if (filledForm.hasErrors()) {
            return badRequest(views.html.dash.post.render(filledForm, auto, request));
        } else {
            Auto form = filledForm.get();
            auto.uuid = form.uuid;
            AdAutoCrud.fill(form, auto);
            auto.published = true;

            auto.setAccount(Auth.getAccount(request));

            try {
                AdAutoCrud.redactPhone(auto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            AdAutoCrud.save(auto);

            return redirect(routes.AcAutoController.getPictures(auto.getId()));
        }
    }

    public Result edit(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        for (AutoFeature feature: auto.features) {
            auto.feat.put(feature.id.toString(), feature.id.toString());
        }

        if(Util.isNotEmpty(auto.description)) {
            auto.description = auto.description.replace("<br/>", "\n");
        }

        Form<Auto> filledForm = autoForm.fill(auto);

        return ok(views.html.dash.post.render(filledForm, auto, request));
    }

    public Result update(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        Form<Auto> filledForm = autoForm.bindFromRequest(request);
        filledForm = AdAutoCrud.validateAd(filledForm);
        if (filledForm.hasErrors()) {
            return badRequest(views.html.dash.post.render(filledForm, auto, request));
        } else {
            Auto form = filledForm.get();
            AdAutoCrud.fill(form, auto);

            AdAutoCrud.save(auto);

            String submit = filledForm.rawData().get("submit");
            if ("toPic".equals(submit)) {
                return redirect(routes.AcAutoController.getPictures(auto.id));
            }

            return redirect(routes.AcAutoController.single(auto.getId()));
        }
    }

    public Result publish(Long id, Http.Request request) {
        Account account = Auth.getAccount(request);
        if(!account.active) {
            return redirect(routes.AcDashboardController.index())
                    .flashing("flashed", "Your account is inactive. Please contact admin");
        }

        Auto auto = DB.findOne(Auto.class, id);
        auto.published = !auto.published;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result trash(Long id, Http.Request request) {
        Account account = Auth.getAccount(request);
        if(!account.active) {
            return redirect(routes.AcDashboardController.index())
                    .flashing("flashed", "Your account is inactive. Please contact admin");
        }

        Auto auto = DB.findOne(Auto.class, id);
        auto.trashed = !auto.trashed;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result delete(Long id) {
        try {
            DB.delete(Auto.class, id);
        } catch (Exception e) {e.printStackTrace();}
        return ok("success");
    }

    public Result getPictures(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        return ok(views.html.dash.postPictures.render(auto, request));
    }

    public Result postPictures(Long id, Http.Request request) {
        DynamicForm form = this.formFactory.form().bindFromRequest(request);
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        List<AutoPicture> photos = auto.getPictures();
        for (AutoPicture picture : photos) {
            String index = form.get("index" + picture.id);
            picture.sort = Integer.parseInt(index);

            DB.save(picture);
        }

        auto.refreshPictures();
        AdAutoCrud.save(auto);

        return redirect(routes.AcAutoController.single(auto.getId()));

    }

    public Result deletePicture(Http.Request request) {
        DynamicForm form = this.formFactory.form().bindFromRequest(request);
        String name = form.get("name");
        try {
            AutoPicture picture = DB.findOne(AutoPicture.class, DB.where().field("name", name));
            if (picture != null) {
                DB.delete(AutoPicture.class, picture.id);

                Auto auto = DB.findOne(Auto.class, DB.where().field("uuid", picture.uuid));
                auto.refreshPictures();
                AdAutoCrud.save(auto);

                return ok("success");
            }

        } catch (Exception e) {}
        return ok("error");
    }

    public Result uploadPicture(Http.Request request) {

        DynamicForm form = formFactory.form().bindFromRequest(request);
        String uuid = form.get("uuid");

        Integer index = Integer.valueOf(form.get("index"));

        Map<String, Object> ret = new HashMap<>();

        try {

            List<String> images = new ArrayList<>();

            List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
            for (Http.MultipartFormData.FilePart<Object> filePart : files) {

                String originalName = filePart.getFilename();
                if (Util.isNotEmpty(originalName)) {
                    String newName = Util.generateUniqueId(50) + "." + S3Service.getFileExtension(originalName);

                    Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                    File original = file.path().toFile();

                    int width = -1;
                    try {
                        width = S3Service.storeOrigin(original, newName);
                        S3Service.storePicture(original, newName, width, true);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    if(width != -1) {
                        AutoPicture picture = new AutoPicture();
                        picture.name = newName;
                        picture.sort = index;
                        picture.uuid = uuid;

                        DB.save(picture);

                        images.add(views.html.dash.postPictureImage.render(picture, index).toString());

                        index++;
                    }
                }
            }

            images.add(views.html.dash.postPictureImage.render(null, index).toString());

            ret.put("images", images);

            JsonNode json = Json.toJson(ret);
            return ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ok("error");
        }
    }

    public Result rotate(String name, Double angle) {

        AutoPicture oldPicture = DB.findOne(AutoPicture.class, DB.where().field("name", name));

        if(oldPicture != null) {

            String newName = Util.generateUniqueId(5) + "-" + name;

            S3Service.rotateImage(oldPicture.getUrl("large"), "large", newName, angle);
            S3Service.rotateImage(oldPicture.getUrl("medium"), "medium", newName, angle);

            String uuid = oldPicture.uuid;

            AutoPicture newPicture = new AutoPicture(newName);
            newPicture.uuid = uuid;
            newPicture.sort = oldPicture.sort;
            newPicture.hide = oldPicture.hide;

            DB.delete(AutoPicture.class, oldPicture.getId());

            DB.save(newPicture);

            return ok("success");

        }

        return ok("failed");

    }
}
