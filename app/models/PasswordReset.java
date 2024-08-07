package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="password_resets")
@Getter @Setter
public class PasswordReset extends ModelBase {

	@Constraints.Required
	private String guid;

	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;

	public PasswordReset() {}

	public PasswordReset(String guid) {
		this.guid = guid;
	}
	
	@Override
	public String toString() {
		return this.guid;
	}
}