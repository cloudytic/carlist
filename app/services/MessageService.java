package services;

import controllers.Util;
import models.Account;
import models.Inbox;
import pojos.EmailSender;
import utilities.HashUtil;

import java.util.HashMap;
import java.util.Map;

public class MessageService {

    public static void verifyEmail(Account account) {
        String subject = account.getFirstName() + ", Your Verification Link For Carloaded";
        String email = account.getEmail().trim();
        String guid = HashUtil.encodeBase64(email);

        String verifyLink = Util.website() + "/verify-email/"+ guid;

        String body = "Hi "+ account.getFirstName() +", <br/><br/>" +
                "<p>Thank you for registering on Carloaded</p>" +
                "<p>For security purposes, please click the below link to verify your email address.</p>" +
                "<p><a href='"+ verifyLink +"'>"+ verifyLink +"</a></p>" +
                "<br/>" +
                "<p>Regards, <br/>The Carloaded Team.</p>";

        sendEmail(email, subject, body);
    }

    public static String welcome(Account account){
        String subject = "Welcome Email";

        String body = salutation(account.getFirstName())+"<p>We are excited to have you here on  <strong>Carloaded!</strong></p> <br/><br/>" +
                "<p>At Carloaded, we believe that home renting should be affordable.</p>" +
                "<p>Carloaded is built to enable affordable renting by helping you pay your rent monthly</p><br/>"+
                "<p>Start paying rent monthly by following the steps below.</p>"+
                "<ul>" +
                    "<li>Sign up</li>" +
                    "<li>Complete your application and get approved</li>" +
                    "<li>Start paying rent monthly</li>" +
                "</ul>"+mailFooter("The Carloaded Team.");

        sendEmail(account.getEmail(), subject, body);
        //sendInbox(account, account.getEmail(), subject, body);

        return body;
    }

    public static void resetPassword(Account account, String guid) {
        String subject = account.getFirstName() + ", Your Request To Reset Your Password";
        String email = account.getEmail().trim();

        String resetLink = Util.website() + "/reset-password/"+ guid;

        String body = "Hi "+ account.getFirstName() +", <br/><br/>" +
                "<p>Please use the link below to reset your password.</p>" +
                "<p><a href='"+ resetLink +"'>"+ resetLink +"</a></p>" +
                "<p>Link expires in 30 minutes</p>" +
                "<br/>" +
                "<p>Regards, <br/>The Carloaded Team.</p>";

        System.out.println("messaage composed here");

        sendEmail(email, subject, body);
    }
    private static String mailFooter(String regarder){
        return "<br/><br/><p>Need any help? Contact our support team on </p>"+
                "<br/> +234 916 000 0970<br/>info@carloaded.com"+
                "<br/><br/>" +
                "<p>Regards, <br/>"+regarder+"</p>";
    }

    private static String salutation(String firstname){
        return "<p>Hi "+firstname+"!</p><br/>";

    }

    public static void sendInbox(Account account, String email, String subject, String body) {
        Inbox inbox = new Inbox();
        inbox.setAccount(account);
        inbox.setTitle(subject);
        inbox.setMessage(body);
        DB.save(inbox);
    }

    public static void sendEmail(String email, String subject, String body) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", body);
        map.put("subject", subject);
        //beforeContent,afterContent,url,label,subject
        String htmlEmailContent = views.html.emails.general.render(map).toString();


        SimpleEmail.sendAsync(email, subject, htmlEmailContent, sender());
    }



    public static void sendEmail(String email, String subject, String bodyB4, String bodyAfter, String href, String label) {
        Map<String, Object> map = new HashMap<>();
        map.put("beforeContent", bodyB4);
        map.put("afterContent", bodyAfter);
        map.put("url", href);
        map.put("label", label);
        map.put("subject", subject);
        //,,,,subject
        String htmlEmailContent = views.html.emails.general.render(map).toString();


        SimpleEmail.sendAsync(email, subject, htmlEmailContent, sender());
    }


    public static EmailSender sender() {
        return new EmailSender("info@carloaded.com", "Carloaded", "info@carloaded.com");
    }
}
