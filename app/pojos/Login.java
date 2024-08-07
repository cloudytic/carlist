package pojos;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

public class Login {
    public Login() {
    }

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Getter @Setter
	@Constraints.Required(message = "Please enter your email address!!!")
	@Constraints.Email(message = "Please enter your a valid email address!!!")
	public String email;

    @Getter @Setter
    @Constraints.Required(message = "Please enter your password!!!")
	public String password;

    public String lastUrl;

    @Override
    public String toString() {
        return "Login{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}