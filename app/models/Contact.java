package models;

import controllers.StaticAssets;
import controllers.Util;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import pojos.SellerContact;

@Entity
@Table(name="contacts")
public class Contact extends ModelBase {

	@Getter @Setter
	public String name;

	@Getter @Setter
	public String phone;

	@Getter @Setter
	public String logoUrl;

	@Getter @Setter
	public boolean notFound;
	
	@Override
	public String toString() {
		return this.name;
	}

	public String logo() {
		if(Util.isNotEmpty(logoUrl)) {
			return StaticAssets.getImg("logo/"+logoUrl);
		}
		return StaticAssets.getUrl("user.png");
	}

	public String url() {
		String code = SellerContact.getCode(phone);
		return SellerContact.url(code);
	}
}