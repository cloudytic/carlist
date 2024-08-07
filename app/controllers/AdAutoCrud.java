package controllers;

import authority.SecuredAdmin;
import com.fasterxml.jackson.databind.JsonNode;
import models.Auto;
import models.AutoFeature;
import models.AutoPicture;
import org.apache.commons.io.FileUtils;
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
import scala.Int;
import search.Searcher;
import services.DB;
import services.S3Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Security.Authenticated(SecuredAdmin.class)
public class AdAutoCrud extends Controller {
    private final FormFactory formFactory;
    private final Form<Auto> autoForm;

    @Inject
    public AdAutoCrud(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.autoForm = formFactory.form(Auto.class);
    }
    
    public Result index(Http.Request request) {

        DB.Filter filter = DB.where();

        filter.field("notVisible", false);

        Map<String, Object> ret = new HashMap<>();

        ret.put("fetched", false);

        return search(request, filter, ret);
    }

    public Result fetched(Http.Request request) {
        DB.Filter filter = DB.where();

        filter.field("notVisible",  true);
        Map<String, Object> ret = new HashMap<>();

        ret.put("fetched", true);

        return search(request, filter, ret);
    }

    private static Result search(Http.Request request, DB.Filter filter, Map<String, Object> ret) {
        Param param = Util.param(50, request);
        param.setSort("id");
        param.setOrder("desc");

        Optional<String> searchOp = request.queryString("search");
        if(searchOp.isPresent()) {
            String search = searchOp.get().trim();
            if(Util.isNotEmpty(search)) {
                filter.brS().field("caption").like(search)
                        .or().field("locality.name").like(search)
                        .or().field("locality.parent.name").like(search)
                        .or().field("category.name").like(search)
                        .or().field("model.name").like(search)
                        .or().field("model.parent.name").like(search)
                        .or().field("colorExterior.name").like(search)
                        .or().field("colorInterior.name").like(search)
                        .or().field("condition.name").like(search)
                        .or().field("transmission.name").like(search)
                        .or().field("fuelType.name").like(search)
                        .or().field("bodyType.name").like(search)
                        .or().field("contact.name").like(search)
                        .or().field("contact.phone").like(search)
                        .brE();
            }
        }

        Optional<String> hasAccount = request.queryString("account");
        if(hasAccount.isPresent()) {
            String accountId = hasAccount.get().trim();
            if(Util.isNumeric(accountId)) {
                filter.field("account.id").eq(Long.parseLong(accountId));
            }
        }


        Optional<String> hasPhoneOp = request.queryString("hasPhone");
        if(hasPhoneOp.isPresent()) {
            String hasPhone = hasPhoneOp.get().trim();
            if("true".equals(hasPhone)) {
                filter.field("contact.phone").notNull();
            } else if("false".equals(hasPhone)) {
                filter.field("contact.phone").isNull();
            }
        }

        Optional<String> isAccountOp = request.queryString("isAccount");
        if(isAccountOp.isPresent()) {
            String isAccount = isAccountOp.get().trim();
            if("true".equals(isAccount)) {
                filter.field("account").notNull();
            } else if("false".equals(isAccount)) {
                filter.field("account").isNull();
            }
        }

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

        Optional<String> stateOp = request.queryString("state");
        if(stateOp.isPresent()) {
            String id = stateOp.get().trim();
            if(Util.isNumeric(id)) {
                filter.field("locality.parent.id").eq(Long.parseLong(id));
            }
        }

        Optional<String> makeOp = request.queryString("make");
        if(makeOp.isPresent()) {
            String id = makeOp.get().trim();
            if(Util.isNumeric(id)) {
                filter.field("model.parent.id").eq(Long.parseLong(id));
            }
        }

        List<String> filters = Arrays.asList(
                "bodyType",
                "transmission",
                "fuelType",
                "colorExterior",
                "colorInterior",
                "condition",
                "model",
                "locality");

        request.queryString().forEach((key,v) -> {
            String value = v[0];
            if(filters.contains(key) && Util.isNumeric(value)) {
                filter.field(key+".id").eq(Long.parseLong(value));
            }
        });


        List<Auto> list = DB.find(Auto.class, filter, param);
        Long total = DB.count(Auto.class, filter);

        ret.put("cars", list);
        ret.put("total", total);
        ret.put("param", param);

        return ok(views.html.admin.autos.render(ret, request));
    }

    public Result single(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        List<Auto> list = Arrays.asList(auto);
        Map<String, Object> ret = new HashMap<>();
        ret.put("cars", list);
        ret.put("total", 1L);
        ret.put("param", new Param(0, 1));
        return ok(views.html.admin.autos.render(ret, request));
    }

    public Result add(Http.Request request) {
        Auto auto = new Auto();
        auto.uuid = Util.generateUniqueId(50);
        Form<Auto> formData = autoForm.fill(auto);
        return ok(views.html.admin.autoPost.render(formData, auto, request));
    }

    public Result create(Http.Request request) {
        Form<Auto> filledForm = autoForm.bindFromRequest(request);

        Auto auto = new Auto();
        auto.uuid = filledForm.rawData().get("uuid");

        filledForm = validateAd(filledForm);

        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.autoPost.render(filledForm, auto, request));
        } else {
            Auto form = filledForm.get();
            auto.uuid = form.uuid;
            fill(form, auto);
            auto.published = true;

            auto.setAccount(Auth.getAccount(request));

            try {
                redactPhone(auto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            save(auto);

            String submit = filledForm.rawData().get("submit");
            if ("toPic".equals(submit)) {
                return redirect(routes.AdAutoCrud.getPictures(auto.id));
            }
            return redirect(routes.AdAutoCrud.single(auto.getId()));
        }
    }

    public Result edit(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        for (AutoFeature feature: auto.features) {
            auto.feat.put(feature.id.toString(), feature.id.toString());
        }
        Form<Auto> filledForm = autoForm.fill(auto);
        return ok(views.html.admin.autoPost.render(filledForm, auto, request));
    }

    public Result update(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        Form<Auto> filledForm = autoForm.bindFromRequest(request);
        filledForm = validateAd(filledForm);
        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.autoPost.render(filledForm, auto, request));
        } else {
            Auto form = filledForm.get();
            fill(form, auto);

            save(auto);

            String submit = filledForm.rawData().get("submit");
            if ("toPic".equals(submit)) {
                return redirect(routes.AdAutoCrud.getPictures(auto.id));
            }

            return redirect(routes.AdAutoCrud.single(auto.getId()));
        }
    }

    public Result delete(Long id) {
        try {
            DB.delete(Auto.class, id);
        } catch (Exception e) {e.printStackTrace();}
        return ok("success");
    }

    public static void save(Auto auto) {
        DB.save(auto);
        Searcher.indexAuto(auto);
    }
    public Result getPictures(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        return ok(views.html.admin.autoPictures.render(auto, request));
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

        return redirect(routes.AdAutoCrud.single(auto.getId()));

    }

    //delete all pictures
    public Result deleteAllPictures(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);

        if(!AdAutoCrud.canModify(auto, request)) {
            return forbidden();
        }

        List<AutoPicture> photos = auto.getPictures();
        for (AutoPicture picture : photos) {
            DB.delete(AutoPicture.class, picture.id);
        }

        auto.refreshPictures();
        AdAutoCrud.save(auto);

        return ok("success");
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

        System.out.println(uuid.toString());

        Integer index = Integer.valueOf(form.get("index"));

        Map<String, Object> ret = new HashMap<>();

        try {

            List<String> images = new ArrayList<>();

            List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
            for (Http.MultipartFormData.FilePart<Object> filePart : files) {

                String originalName = filePart.getFilename();
                if (Util.isNotEmpty(originalName)) {
                    String extension = S3Service.getFileExtension(originalName);
                    String newName = Util.generateUniqueId(50) + "." + extension;

                    Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                    File original = file.path().toFile();


                    int width = -1;
                    try {
                        boolean process = !"webp".equals(extension);
                        if(process) {
                            width = S3Service.storeOrigin(original, newName);
                            S3Service.storePicture(original, newName, width, true);
                        } else {
                            FileUtils.copyFile(original, new File(S3Service.TMPMAINDIR + newName));
                            S3Service.storePicture(original, newName, -1, false);
                            width = 0;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    if(width != -1) {
                        AutoPicture picture = new AutoPicture();
                        picture.name = newName;
                        picture.sort = index;
                        picture.uuid = uuid;

                        DB.save(picture);

                        images.add(views.html.admin.autoPictureImage.render(picture, index).toString());

                        index++;
                    }
                }
            }

            images.add(views.html.admin.autoPictureImage.render(null, index).toString());

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

    public static Form<Auto> validateAd(Form<Auto> filledForm) {
        Map<String, String> data = filledForm.rawData();

        String price = data.get("price");
        if (Util.isEmpty(price)) {
            filledForm = filledForm.withError("price", "This field is required");
        } else {
            price = price.replace(",", "").replaceAll("\\s", "");
            if (!Util.isNumeric(price)) {
                filledForm = filledForm.withError("price", "Price field must be a number e.g 500,000");
            } else if (Integer.parseInt(price) <= 100000) {
                filledForm = filledForm.withError("price", "Please enter a valid price");
            }
        }

        if (!filledForm.hasErrors()) {}

        return filledForm;
    }

    public static void fill(Auto form, Auto auto) {

        auto.setCategory(form.getCategory());

        auto.setCaption(form.getCaption());

        String description = form.getDescription();
        if(Util.isNotEmpty(description)) {
            description = description.replace("\n", "<br/>");
            auto.setDescription(description);
        }


        auto.setPrice(form.getPrice());

        auto.setModel(form.getModel());
        auto.setYear(form.getYear());
        auto.setColorExterior(form.getColorExterior());
        auto.setColorInterior(form.getColorInterior());
        auto.setCondition(form.getCondition());
        auto.setTransmission(form.getTransmission());
        //auto.setEngineType(form.getEngineType());
        auto.setEngineSize(form.getEngineSize());

        auto.setDrive(form.getDrive());

        auto.setMileage(form.getMileage());
        auto.setVin(form.getVin());

        auto.setRegistered(form.isRegistered());
        auto.setFuelType(form.getFuelType());
        auto.setBodyType(form.getBodyType());
        auto.setDriveTrain(form.getDriveTrain());

        auto.setLocality(form.getLocality());

        fillChecks(form, auto);
    }

    public static void fillChecks(Auto form, Auto main) {
        Map<String, String> data = form.feat;
        Set<AutoFeature> features = new HashSet<>();
        if(data != null && !data.isEmpty()) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                AutoFeature feature = DB.findOne(AutoFeature.class, entry.getValue());
                if (feature != null) {
                    features.add(feature);
                }
            }
        } else if(Util.isNotEmpty(form.featureIds)) {
            String[] array = form.featureIds.split(",");
            List<String> list = Arrays.asList(array);
            Set<String> ids = new HashSet<>(list);
            for (String id : ids) {
                AutoFeature feature = DB.findOne(AutoFeature.class, id.trim());
                if (feature != null) {
                    features.add(feature);
                }
            }
        }
        main.features = features;

    }

    public static boolean redactPhone(Auto auto) {

        String text = auto.description;

        boolean seen = false;

        String regex = "[0-9\\+ ]{10,22}";
        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()) {
            String match = m.group();
            match = match.trim();
            if(Util.isNotEmpty(match)) {
                if(match.replaceAll("\\D", "").length() > 5) {
                    auto.description = auto.description.replace(match, "[redacted]");
                    seen = true;
                    //System.out.println("phone: " + match);
                }
            }
        }

        m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(text);
        while (m.find()) {
            String match = m.group();
            auto.description = auto.description.replace(match, "[redacted]");
            seen = true;
            //System.out.println("email: "+ match);
        }

        return seen;
    }

    public static boolean canModify(Auto auto, Http.Request request) {
        if(Auth.isAccount(request)) {
            /*Account account = Auth.getAccount(request);
            if(auto.dealer == null || !auto.dealer.id.equals(dealer.id)){
                return false;
            }*/
        }
        return true;
    }


    public Result trash(Long id) {
        Auto auto = DB.findOne(Auto.class, id);
        auto.trashed = !auto.trashed;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result publish(Long id) {
        Auto auto = DB.findOne(Auto.class, id);
        auto.published = !auto.published;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public Result visible(Long id) {
        Auto auto = DB.findOne(Auto.class, id);
        auto.notVisible = !auto.notVisible;
        AdAutoCrud.save(auto);
        return ok("success");
    }

    public static void indexAutos() {
        try {
            for(int i=0; i < Integer.MAX_VALUE; i++) {
                Param param = Param.get(i, 50, "created", "desc");

                List<Auto> autos = DB.find(Auto.class, DB.where(), param);

                if(autos.isEmpty()) {
                    break;
                } else {
                    for(final Auto auto: autos) {
                        try {
                            Searcher.indexAuto(auto);
                            System.out.println("Indexed " + auto.getRef());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public Result boost(Long id) {
        Auto auto = DB.findOne(Auto.class, id);
        try {
            auto.boostedOn = new Date();;
            AdAutoCrud.save(auto);
        } catch (Exception e) {
            e.printStackTrace();
            return ok("error");
        }

        return ok("success");
    }

    public Result downloadPictures(Long id, Http.Request request) {
        Auto auto = DB.findOne(Auto.class, id);
        List<AutoPicture> pictures = auto.getPictures();
        //reverse order
        Collections.reverse(pictures);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            for (AutoPicture picture : pictures) {
                String link = StaticAssets.getImg(picture.getUrl("large"));
                URL url = new URL(link);
                InputStream is = url.openStream();
                ZipEntry zipEntry = new ZipEntry(picture.name);
                zos.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                is.close();
                zos.closeEntry();
            }
            zos.close();
            baos.close();

            String downloadName = auto.getRef() + ".zip";

            return ok(baos.toByteArray()).as("application/zip")
                    .withHeader("Content-Disposition", "attachment; filename=\""+downloadName+"\"");
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error creating zip file");
        }
    }
}
