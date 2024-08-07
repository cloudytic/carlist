package controllers;

import authority.Secured;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import models.Account;
import models.Image;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.DB;
import services.S3Service;

import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Security.Authenticated(Secured.class)
public class AcProfileController extends Controller {

    private final FormFactory formFactory;

    @Inject
    public AcProfileController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result edit(Http.Request request) {
        Account account = Auth.getAccount(request);

        if(!account.active) {
            return redirect(routes.AcDashboardController.index())
                    .flashing("flashed", "Your account is inactive. Please contact admin");
        }


        account.dial = "234";
        account.about = Util.convert2Text(account.about);

        Form<Account> formData = formFactory.form(Account.class).fill(account);

        return ok(views.html.dash.profile_form.render(formData, account, request));
    }

    public Result update(Http.Request request) {
        Account account = Auth.getAccount(request);
        Form<Account> formData = formFactory.form(Account.class).bindFromRequest(request);

        formData = validateAccount(formData, account);

        if (formData.hasErrors()) {
            return badRequest(views.html.dash.profile_form.render(formData, account, request));
        }

        fill(account, formData);

        List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
        for (Http.MultipartFormData.FilePart filePart : files) {
            try {
                if(Util.isNotEmpty(filePart.getFilename())) {
                    Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                    File original = file.path().toFile();

                    Image logo = logo(original, filePart.getFilename());
                    DB.save(logo);
                    account.logo = logo;

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Logo: " + e.getMessage());
            }
        }

        AdAcctCrud.saveAccount(account);
        
        return redirect(routes.AcDashboardController.index());
    }

    public Result updateLogo(Http.Request request) {
        Account account = Auth.getAccount(request);
        List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
        for (Http.MultipartFormData.FilePart filePart : files) {
            try {
                if(Util.isNotEmpty(filePart.getFilename())) {

                    Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                    File original = file.path().toFile();
                    
                    Image logo = logo(original, filePart.getFilename());
                    DB.save(logo);
                    account.logo = logo;

                    AdAcctCrud.saveAccount(account);

                    break;
                }
            } catch (Exception e) {
                log.info("Logo: " + e.getMessage());
            }
        }

        return redirect(routes.AcDashboardController.index());
    }

    public static Form<Account> validateAccount(Form<Account> formData, Account account) {

        if (formData.field("businessName").value().orElse("").trim().isEmpty()) {
            formData = formData.withError("businessName", "Please enter your business name");
        }

        String dial = formData.field("dial").value().orElse("").trim();
        if(Util.isEmpty(dial)) {
            formData = formData.withError("dial", "Please select country code");
        }

        String phone = formData.field("phone").value().orElse("").trim();
        if(!phone.matches("[0-9+]+")) {
            formData = formData.withError("phone", "Enter only a valid number (digits only)");
        } else {
            if(!phone.equals(account.phone)) {
                log.info("Checking phone existence: " + phone);
                phone = cleanNumber(phone, dial);
                Account ag = DB.findOne(Account.class, DB.where().field("phone", phone));
                if (ag != null && !ag.id.equals(account.id)) {
                    formData = formData.withError("phone", "This phone number is already registered with another account");
                    log.warn("Profile: Phone registered: " + phone + " / dealerId:" + account.id);
                }
            }
        }

        String whatsapp = formData.field("whatsapp").value().orElse("").trim();
        if(!whatsapp.matches("[0-9+]+")) {
            formData = formData.withError("whatsapp", "Enter only a valid whatsapp number (digits only)");
        } else {
            if(!whatsapp.equals(account.whatsapp)) {
                log.info("Checking whatsapp existence: " + whatsapp);
                whatsapp = cleanNumber(whatsapp, dial);
                Account ag = DB.findOne(Account.class, DB.where().field("whatsapp", whatsapp));
                if (ag != null && !ag.id.equals(account.id)) {
                    formData = formData.withError("whatsapp", "This whatsapp number is already registered with another account");
                    log.info("Profile: Whatsapp registered: " + whatsapp + " / dealerId:" + account.id);
                }
            }
        }

        if (!formData.field("password").value().orElse("").trim().isEmpty()) {
            if (!formData.field("password").value().orElse("")
                    .equals(formData.field("confirmPass").value().orElse(""))) {
                formData = formData.withError("confirmPass", "Passwords don't match");
            }
        }

        return formData;
    }

    public static void fill(Account main, Form<Account> formData) {
        Account form = formData.get();

        main.businessName = form.businessName;
        main.firstName = form.firstName;
        main.lastName = form.lastName;
        main.email = form.email;
        main.dial = form.dial;
        main.phone = cleanNumber(form.phone, form.dial);
        main.whatsapp = cleanNumber(form.whatsapp, form.dial);

        main.website = form.website;

        main.locality = form.locality;

        String password = formData.field("password").value().orElse("").trim();
        if(Util.isNotEmpty(password)) {
            main.setHashedPassword(password);
        }

        if(Util.isNotEmpty(form.about)) {
            String about = form.about;
            main.about = about
                    .replace("\n", "<br/>")
                    .replace("<a", "<span")
                    .replace("</a", "</span")
                    .replace("<A", "<span")
                    .replace("</A", "</span");
        }


        main.facebook = Util.reduceString(form.facebook, 255);
        main.instagram = Util.reduceString(form.instagram, 255);
        main.linkedin = Util.reduceString(form.linkedin, 255);
        main.twitter = Util.reduceString(form.twitter, 255);

    }

    public static String getFileExtension(String fileName) {
        int mid = fileName.lastIndexOf(".");
        String ext = fileName.substring(mid + 1);
        return ext.toLowerCase();
    }

    public static Image logo(File file, String original) throws Exception {
        String fileName = Util.generateUniqueId(20) + "." + S3Service.getFileExtension(original);
        Image logo = new Image();
        logo.name = fileName;
        S3Service.storeLogo(file, fileName, true);
        return logo;
    }

    public static String validatePhone(String phone, String country) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phone.trim(), country);
            boolean isValid = phoneUtil.isValidNumber(number);
            if (!isValid) {
                return "Invalid";
            }
            phone = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            return phone.replace(" ", "").replace("+", "");
        } catch (Exception e) {
            return "Error";
        }
    }

    public static String cleanNumber(String number, String dial) {
        if(Util.isEmpty(number)) {
            return "";
        }

        number = number.trim();

        number = number.replaceAll("[^0-9]", "");

        if(!number.startsWith("+") && "234".equals(dial)) {
            log.info("UnCleaned NG: " + number);
            if (number.startsWith("234")) {
                number = number.substring(3);
            }
            number = number.startsWith("0") ? number : "0" + number;

            Pattern r = Pattern.compile("(00+)");
            Matcher m = r.matcher(number);
            if (m.find()) {
                String match = m.group(1);
                if(number.startsWith(match)) {
                    number = number.replaceFirst(match, "0");
                }
            }
            log.info("Cleaned NG: " + number);
        }

        return number;
    }
}