package pojos;

public class EmailSender {
    public String email;
    public String name;
    public String replyTo;

    public EmailSender(String email, String name, String replyTo) {
        this.email = email;
        this.name = name;
        this.replyTo = replyTo;
    }
}
