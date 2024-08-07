package controllers;

import authority.SecuredApi;
import lombok.extern.slf4j.Slf4j;
import models.Account;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Login;
import services.DB;
import services.MessageService;
import services.PasswordService;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ApiAuthController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public ApiAuthController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result login(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();

        Form<Login> filledForm = formFactory.form(Login.class).bindFromRequest(request);
        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            Login login = filledForm.get();
            String email = login.email.trim();
            String password = login.password.trim();

            DB.Filter filter = DB.where();
            filter.field("email", email);
            Account account = DB.findOne(Account.class,filter);

            if(account != null && PasswordService.checkPassword(password, account.getHashedPassword())) {
                if (!account.isActive()) {
                    map.put("error", "Account is not active, please contact the administrator.");
                }

                String accessToken = SecuredApi.getAccessToken(account);

                map.put("accessToken", accessToken);
                map.put("account", account);

            } else {
                map.put("error", "Incorrect Email/Password");
            }
        }

        return ok(Json.toJson(map));
    }

    public Result register(Http.Request request) {
        Map<String, Object> map = new LinkedHashMap<>();

        Form<Account> filledForm = formFactory.form(Account.class).bindFromRequest(request);

        filledForm = RegisterController.validate(filledForm);

        if (filledForm.hasErrors()) {
            StringBuilder err = new StringBuilder();
            filledForm.errors().forEach(error -> {
                err.append(error.message()).append("\n");
            });
            map.put("error", err.toString());
        } else {
            Account account = new Account();

            String password = filledForm.field("password").value().orElse("").trim();

            Account form = filledForm.get();

            String phone = AdAcctCrud.cleanNumber(form.getPhone());

            account.setActive(true);

            account.setBusinessName(form.getBusinessName());
            account.setPhone(phone);
            account.setEmail(form.getEmail());

            account.setHashedPassword(password);

            account.username = AdAcctCrud.generateUsername(account.getBusinessName(), 0);

            AdAcctCrud.saveAccount(account);


            //TODO: OTP Verification

            //Send Verify Email Message
            MessageService.welcome(account);

            String accessToken = SecuredApi.getAccessToken(account);

            map.put("accessToken", accessToken);
            map.put("account", account);
        }

        return ok(Json.toJson(map));
    }
}
