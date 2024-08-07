package pojos;

import lombok.Getter;
import lombok.Setter;

public class Passwords {
	@Getter @Setter
	public String current;
	@Getter @Setter
	public String password;
	@Getter @Setter
	public String confirmPass;
}