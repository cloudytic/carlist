package controllers;

import authority.SecuredApi;
import models.Account;
import models.Auto;
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

@Security.Authenticated(SecuredApi.class)
public class ApiAcAutoController extends Controller {
    private final FormFactory formFactory;
    private final Form<Auto> autoForm;

    @Inject
    public ApiAcAutoController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.autoForm = formFactory.form(Auto.class);
    }

    public Result index(Http.Request request) {
        System.out.println("ApiAutoController: " + request.uri());

        Account account = SecuredApi.getAccount(request);

        Param param = Util.param(20, request);
        param.setSort("id");
        param.setOrder("desc");

        DB.Filter filter = DB.where();
        filter.field("account", account);

        AcAutoController.populateFilter(request, filter);

        List<Auto> list = DB.find(Auto.class, filter, param);
        Long total = DB.count(Auto.class, filter);

        Map<String, Object> map = new HashMap<>();

        map.put("list", list);
        map.put("total", total);

        map.put("param", param);

        return ok(Json.toJson(map));
    }

    public Result single(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        if(auto == null) {
            return notFound();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("auto", auto);
        return ok(Json.toJson(map));
    }

    public Result create(Http.Request request) {
        Map<String, Object> map = new HashMap<>();

        Account account = SecuredApi.getAccount(request);

        Form<Auto> filledForm = autoForm.bindFromRequest(request);

        Auto auto = new Auto();
        auto.uuid = Util.generateUniqueId(50);

        filledForm = AdAutoCrud.validateAd(filledForm);

        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            Auto form = filledForm.get();
            AdAutoCrud.fill(form, auto);
            auto.published = true;

            auto.setAccount(account);

            try {
                AdAutoCrud.redactPhone(auto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                AdAutoCrud.save(auto);
                map.put("id", auto.id);
            } catch (Exception e) {
                map.put("error", e.getMessage());
            }

        }
        return ok(Json.toJson(map));
    }

    public Result update(Long id, Http.Request request) {
        Map<String, Object> map = new HashMap<>();

        Auto auto = DB.findOne(Auto.class, id);

        Form<Auto> filledForm = autoForm.bindFromRequest(request);
        filledForm = AdAutoCrud.validateAd(filledForm);
        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.key() + ": "+ error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            Auto form = filledForm.get();
            AdAutoCrud.fill(form, auto);

            AdAutoCrud.save(auto);

            map.put("id", auto.id);
        }

        return ok(Json.toJson(map));
    }

    public Result publish(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        auto.published = !auto.published;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result trash(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        auto.trashed = !auto.trashed;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result uploadPicture(Http.Request request) {
        DynamicForm form = formFactory.form().bindFromRequest(request);
        String uuid = form.get("uuid");

        Auto auto = DB.findOne(Auto.class, DB.where().field("uuid", uuid));
        if(auto == null) {
            return notFound();
        }

        int index = Integer.parseInt(form.get("index"));

        try {

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
                        index++;
                    }
                }
            }

            auto.refreshPictures();

            Map<String, Object> map = new HashMap<>();

            map.put("pictures", auto.getPictures());

            return ok(Json.toJson(map));

        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }
    }

    public Result makePicturePrimary(Long id, Http.Request request) {
        AutoPicture picture = DB.findOne(AutoPicture.class, id);
        if(picture == null) {
            return notFound();
        }

        Auto auto = DB.findOne(Auto.class, DB.where().field("uuid", picture.uuid));
        if(auto == null) {
            return notFound();
        }

        int lowestIndex = auto.getPictures().stream().mapToInt(p -> p.sort).min().orElse(0);
        picture.sort = lowestIndex - 1;
        DB.save(picture);

        auto.refreshPictures();

        Map<String, Object> map = new HashMap<>();
        map.put("pictures", auto.getPictures());

        return ok(Json.toJson(map));
    }

    public Result deletePicture(Long id, Http.Request request) {
        try {
            DB.delete(AutoPicture.class, id);
            return ok("success");
        } catch (Exception e) {
            return internalServerError();
        }

    }
}
