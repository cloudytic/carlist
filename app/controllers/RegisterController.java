package controllers;

import models.Account;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.DB;
import services.MessageService;
import utilities.HashUtil;

import javax.inject.Inject;

public class RegisterController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public RegisterController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index(Http.Request request) {
        Account account = new Account();

        Form<Account> filledForm = this.formFactory.form(Account.class).fill(account);

        return ok(views.html.main.register.render(filledForm, account, request));
    }

    public Result submit(Http.Request request) {

        Form<Account> filledForm = this.formFactory.form(Account.class).bindFromRequest(request);


        filledForm = validate(filledForm);

        Account account = new Account();

        if (filledForm.hasErrors()) {
            return badRequest(views.html.main.register.render(filledForm, account, request));
        } else {
            String password = filledForm.field("password").value().orElse("").trim();

            Account form = filledForm.get();

            String phone = AdAcctCrud.cleanNumber(form.getPhone());

            account.setActive(true);

            account.setBusinessName(form.getBusinessName());
            //account.setFirstName(form.getFirstName());
            //account.setLastName(form.getLastName());
            account.setPhone(phone);
            account.setEmail(form.getEmail());

            account.setHashedPassword(password);

            account.username = AdAcctCrud.generateUsername(account.getBusinessName(), 0);

            AdAcctCrud.saveAccount(account);


            //TODO: OTP Verification

            //Send Verify Email Message
            MessageService.welcome(account);

            return redirect(routes.AcProfileController.edit())
                    .addingToSession(request, Auth.ACCOUNT, account.getId().toString());
        }
    }

    public static Form<Account> validate(Form<Account> filledForm) {
        if (filledForm.field("businessName").value().orElse("").trim().isEmpty()) {
            filledForm = filledForm.withError("businessName", "Please enter your Business Name or Name");
        }

        String phone = filledForm.field("phone").value().orElse("").trim();
        if (phone.isEmpty()) {
            filledForm = filledForm.withError("phone", "Please enter your phone number");
        }

        if (filledForm.field("email").value().orElse("").trim().isEmpty()) {
            filledForm = filledForm.withError("email", "Please enter your email address");
        }

        String password = filledForm.field("password").value().orElse("").trim();
        if (Util.isEmpty(password)) {
            filledForm = filledForm.withError("password", "Enter a password");
        } else {
            if (!Util.isValidPassword(password)) {
                filledForm = filledForm.withError("password", "Your password is not strong enough");
            } else {
                String confirmPass = filledForm.field("confirmPass").value().orElse("");
                if (!password.equals(confirmPass)) {
                    filledForm = filledForm.withError("confirmPass", "Passwords don't match");
                }
            }
        }

        String email = filledForm.field("email").value().orElse("").toLowerCase().trim();
        if(Util.isNotEmpty(email)) {
            email = email.trim().toLowerCase();
            DB.Filter filter = DB.where()
                    .field("email", email);
            Account acct = DB.findOne(Account.class, filter);
            if (acct != null) {
                filledForm = filledForm.withError("email", "This email is already registered");
            }
        }

        if(Util.isNotEmpty(phone)) {
            phone = AdAcctCrud.cleanNumber(phone);
            DB.Filter filter = DB.where()
                    .field("phone", phone);
            Account acct = DB.findOne(Account.class, filter);
            if (acct != null) {
                filledForm = filledForm.withError("phone", "This phone number is already registered");
            }
        }

        return filledForm;
    }

    public boolean verifyOTP(Account account) {
        return false;
    }

    public Result verifyEmail(String guid, Http.Request request) {
        String email = HashUtil.decodeBase64(guid);
        Account account = DB.findOne(Account.class, DB.where().field("email", email.trim()));

        if(account != null) {
            account.setEmailVerified(true);
            AdAcctCrud.saveAccount(account);

            //Send welcome email
            MessageService.welcome(account);

            return redirect(routes.AcDashboardController.index())
                    .flashing("info", "You have successfully verified your email address");
        }

        return notFound();
    }

    public Result sendVerifyEmail(Long id, Http.Request request) {
        Account account = DB.findOne(Account.class, id);
        if(account != null) {
            MessageService.verifyEmail(account);
            return ok("success");
        }
        return ok("error");
    }
}
