package controllers;

import models.Account;
import models.PasswordReset;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Passwords;
import services.DB;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class PasswordResetController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public PasswordResetController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result forgotten(Http.Request request) {
        return ok(views.html.main.password_forgotten.render(request));
    }

    public Result submitForgotten(Http.Request request) {
        DynamicForm form = formFactory.form().bindFromRequest(request);
        String email = form.get("email").trim();
        String info = "";
        if (Util.isValidEmail(email)) {
            Account account = DB.findOne(Account.class, DB.where().field("email", email));
            if(account != null) {
                String guid = Util.generateUniqueId(100);

                PasswordReset reset = new PasswordReset(guid);
                reset.setAccount(account);
                DB.save(reset);

                //Send password reset email
                System.out.println("prompting sending here");
                //MessageService.resetPassword(account, guid);

                info = "Mail sent, please check your email for further instructions";
            } else {
                info = "We could not find a record with this email address";
            }
        } else {
            info = "Please enter your valid email address";

        }
        return redirect(routes.PasswordResetController.forgotten())
                .flashing("info", info);
    }

    public Result reset(String guid, Http.Request request) {
        PasswordReset reset = DB.findOne(PasswordReset.class, DB.where().field("guid", guid));
        Calendar thirtyMinAgo = Calendar.getInstance();
        thirtyMinAgo.add(Calendar.MINUTE, -30);
        String info = "";
        if(reset != null) {
            if(reset.getCreated().before(thirtyMinAgo.getTime())) {
                DB.delete(PasswordReset.class, reset.getId());
                info = "Please enter your email address again as the link has expired";
            } else {
                Account account = reset.getAccount();
                if (account != null) {
                    Form<Passwords> doForm = this.formFactory.form(Passwords.class);
                    return ok(views.html.main.password_reset.render(doForm, guid, request));
                }
            }
        } else {
            info = "Please enter your email address again as this record could not be found";
        }
        return redirect(routes.PasswordResetController.forgotten())
                .flashing("info", info);
    }

    public Result submitReset(String guid, Http.Request request) {
        PasswordReset reset = DB.findOne(PasswordReset.class, DB.where().field("guid", guid));
        Form<Passwords> filledForm = this.formFactory.form(Passwords.class).bindFromRequest(request);

        String password = filledForm.field("password").value().orElse("");
        if (Util.isEmpty(password)) {
            filledForm = filledForm.withError("password", "Enter a password");
        } else{
            if (!Util.isValidPassword(password)) {
                filledForm = filledForm.withError("password", "Your password is not strong enough");
            } else {
                String confirmPass = filledForm.field("confirmPass").value().orElse("");
                if (!password.equals(confirmPass)) {
                    filledForm = filledForm.withError("confirmPass", "Passwords don't match");
                }
            }
        }

        if(filledForm.hasErrors()) {
            return badRequest(views.html.main.password_reset.render(filledForm, guid, request));
        } else {
            if(reset != null) {
                Account account = DB.findOne(Account.class, reset.getAccount().getId());

                Optional<String> pOption = filledForm.field("password").value();

                if(account != null && pOption.isPresent()) {
                    String newPassword = pOption.get();

                    account.setHashedPassword(newPassword);
                    AdAcctCrud.saveAccount(account);

                    List<PasswordReset> resets = DB.find(PasswordReset.class, DB.where().field("account", account));
                    resets.forEach(r -> DB.delete(PasswordReset.class, r.getId()));

                    String info = "Password has been changed successfully, login with the new password to proceed";
                    return redirect(routes.LoginController.login()).flashing("info", info);
                }
            }
        }
        String info = "Something went wrong! Please enter your email address again";
        return redirect(routes.PasswordResetController.forgotten()).flashing("info", info);
    }
}
