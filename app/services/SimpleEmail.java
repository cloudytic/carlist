package services;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import pojos.EmailSender;
import pojos.Smtp;

public class SimpleEmail {
    public static Smtp smtp() {
        Smtp smtp = new Smtp();
        smtp.username = "";//"info@{placeholder}";//"AKIAV5DML3CZZOZY4KHF";
        smtp.password = "";//"FCFDCF4FEB169969DABFF13E4FC83C05BC44";//"BCHDbs9yqh2huFf/UqJ9HTrgzS0Y2BaIWoCuWYZO9Z3p";
        smtp.host = "";//"smtp.elasticemail.com";//"email-smtp.us-east-1.amazonaws.com";
        smtp.port = 587;//2525; //587;
        return smtp;
    }

    public static void send(String email, String subject, String body, EmailSender sender) {
        sendSmtp(email, subject, body, sender, smtp());
    }

    public static void sendAsync(String email, String subject, String body, EmailSender sender) {

        System.out.println("threaded here");
        new Thread(() -> {
            sendSmtp(email, subject, body, sender, smtp());
        }).start();
    }

    private static void sendSmtp(String emailAddress, String subject, String body, EmailSender sender, Smtp cred) {
        Email email = EmailBuilder.startingBlank()
                .to(emailAddress)
                .from(sender.name, sender.email)
                //.to("C. Cane", "candycane@candyshop.org")
                //.ccWithFixedName("C. Bo group", "chocobo1@candyshop.org", "chocobo2@candyshop.org")
                //.withRecipientsUsingFixedName("Tasting Group", BCC, "taster1@cgroup.org;taster2@cgroup.org;tester <taster3@cgroup.org>")
                //.bcc("Mr Sweetnose <snose@candyshop.org>")
                .withReplyTo(sender.replyTo)
                .withSubject(subject)
                .withHTMLText(body)
                //.withPlainText("Please view this email in a modern email client!")
                //.withCalendar(CalendarMethod.REQUEST, iCalendarText)
                //.withEmbeddedImage("wink1", imageByteArray, "image/png")
                //.withEmbeddedImage("wink2", imageDatesource)
                //.withAttachment("invitation", pdfByteArray, "application/pdf")
                //.withAttachment("dresscode", odfDatasource)
                //.withHeader("X-Priority", 5)
                //.withReturnReceiptTo()
                //.withDispositionNotificationTo("notify-read-emails@candyshop.com")
                //.withBounceTo("tech@candyshop.com")
                //.signWithDomainKey(privateKeyData, "somemail.com", "selector") // DKIM
                //.signWithSmime(pkcs12Config)
                //.encryptWithSmime(x509Certificate)
                .buildEmail();

        Mailer mailer = MailerBuilder
                .withSMTPServer(cred.host, cred.port, cred.username, cred.password)
                //.withTransportStrategy(TransportStrategy.SMTP_TLS)
                //.withProxy("socksproxy.host.com", 1080, "proxy user", "proxy password")
                //.withSessionTimeout(10 * 1000)
                //.clearEmailAddressCriteria() // turns off email validation
                //.withProperty("mail.smtp.sendpartial", true)
                //.withDebugLogging(true)
                //.async()
                // not enough? what about this:
                //.withClusterKey(myPowerfulMailingCluster)
                //.withThreadPoolSize(20) // multi-threaded batch handling
                //.withConnectionPoolCoreSize(10) // reusable connection(s) / multi-server sending
                //.withCustomSSLFactoryClass("org.mypackage.MySSLFactory")
                //.withCustomSSLFactoryInstance(mySSLFactoryInstance)
                //.manyMoreOptions()
                .buildMailer();

        System.out.println("mailer called here");

        mailer.sendMail(email);

        System.out.println("mailer ends");

        //mailer.testConnection();
        //mailer.validate(email);
        //mailer.getServerConfig();
        //mailer.getOperationalConfig();
        //mailer.getProxyConfig();
        //mailer.getTransportStrategy();
        //mailer.getSession();
        //mailer.getEmailAddressCriteria();
    }
}
