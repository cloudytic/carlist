package controllers;

import authority.SecuredApi;
import lombok.extern.slf4j.Slf4j;
import models.Account;
import models.Image;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.DB;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

@Slf4j
@Security.Authenticated(SecuredApi.class)
public class ApiAcProfileController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public ApiAcProfileController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result getAccount(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();
        Account account = SecuredApi.getAccount(request);
        map.put("account", account);
        return ok(Json.toJson(map));
    }

    public Result editAccount(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();

        Account account = SecuredApi.getAccount(request);

        Form<Account> filledForm = formFactory.form(Account.class).bindFromRequest(request);

        filledForm = AcProfileController.validateAccount(filledForm, account);

        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            Account form = filledForm.get();

            account.setBusinessName(form.getBusinessName());
            account.setFirstName(form.getFirstName());
            account.setLastName(form.getLastName());
            account.setEmail(form.getEmail());
            account.setPhone(form.getPhone());
            account.setWhatsapp(form.getWhatsapp());
            account.setLocality(form.getLocality());
            account.setAbout(form.getAbout());

            System.out.println("Saved Api Account: " + account);

            AdAcctCrud.saveAccount(account);

            map.put("account", account);
        }

        return ok(Json.toJson(map));
    }

    public Result changePassword(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();

        Account account = SecuredApi.getAccount(request);

        Form<Account> filledForm = formFactory.form(Account.class).bindFromRequest(request);

        //validate password
        if (!filledForm.field("password").value().orElse("").trim().isEmpty()) {
            if (!filledForm.field("password").value().orElse("")
                    .equals(filledForm.field("confirmPass").value().orElse(""))) {
                filledForm = filledForm.withError("confirmPass", "Passwords don't match");
            }
        } else {
            filledForm = filledForm.withError("password", "Please enter a password");
        }


        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            String password = filledForm.field("password").value().orElse("").trim();
            account.setHashedPassword(password);

            AdAcctCrud.saveAccount(account);

            map.put("account", account);
        }

        return ok(Json.toJson(map));
    }

    public Result updateLogo(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();
        Account account = SecuredApi.getAccount(request);

        List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
        for (Http.MultipartFormData.FilePart filePart : files) {
            try {
                if(Util.isNotEmpty(filePart.getFilename())) {

                    Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                    File original = file.path().toFile();

                    Image logo = AcProfileController.logo(original, filePart.getFilename());
                    DB.save(logo);
                    account.logo = logo;

                    AdAcctCrud.saveAccount(account);

                    map.put("account", account);

                    return ok(Json.toJson(map));
                }
            } catch (Exception e) {
                log.info("Logo: {}", e.getMessage());
            }
        }

        map.put("error", "No file uploaded");
        return ok(Json.toJson(map));
    }


}
