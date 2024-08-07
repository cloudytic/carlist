package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.AdAcctCrud;
import controllers.StaticAssets;
import controllers.Util;
import controllers.routes;
import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;
import pojos.Needed;
import pojos.SellerContact;
import services.DB;
import services.PasswordService;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="accounts")
@Getter @Setter
public class Account extends ModelBase implements Serializable {

    @Column(nullable=false)
    public boolean active = true;

    @Needed
    @Column(nullable=false, unique=true)
    public String email;

    public String username;

    public Boolean emailVerified;

    @Column(nullable=false)
    public String hashedPassword;

    public String businessName;

    @Needed
    public String firstName;

    @Needed
    public String lastName;

    @Needed
    public String phone;
    public String dial;
    public String whatsapp;
    public String website;

    @Column(length=100000)
    @Lob
    public String about;

    public String facebook;
    public String instagram;
    public String linkedin;
    public String twitter;

    //@Constraints.Required
    @ModelFormat(AddressLocality.class)
    @ManyToOne
    @JoinColumn(name="locality_id")
    public AddressLocality locality;

    @Getter @Setter
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="logo_id")
    public Image logo;

    public void setHashedPassword(String password) {
        this.hashedPassword = PasswordService.hashPassword(password);
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @JsonIgnore
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String search;
    public String getSearch() {
        return getFullName() +  " " + businessName +  " " + phone + " " + whatsapp;
    }

    @Override
    public String toString() {
        if(Util.isNotEmpty(businessName))
            return businessName;
        return getFullName();
    }

    @Transient
    public boolean searcheable;

    @PrePersist
    @PreUpdate
    @PostLoad
    public void init() {
        if(Util.isEmpty(username)) {
            username = AdAcctCrud.generateUsername(this.getBusinessName(), 0);
        }

        searcheable = Util.isNotEmpty(businessName) && Util.isNotEmpty(phone);
    }

    public String firstName() {
        if(Util.isNotEmpty(firstName)) {
            return firstName;
        }
        return "";
    }
    public String image() {
        if(logo != null) {
            return StaticAssets.getImg("logo/"+logo.name);
        }
        return StaticAssets.getUrl("image_not_available.png");
    }

    public String whatsapp() {
        String whatsapp = this.whatsapp;
        if(Util.isEmpty(whatsapp)) {
            whatsapp = phone;
        }
        return whatsapp(whatsapp, this.dial);
    }

    public static String whatsapp(String whatsapp, String dial) {
        if(Util.isEmpty(whatsapp)) {
            return "";
        }

        if(whatsapp.contains(",")) {
            whatsapp = whatsapp.split(",")[0].trim();
        }

        if(whatsapp.startsWith("+")) {
            return whatsapp.replace("+", "");
        }

        if(Util.isEmpty(dial) || dial.equals("234")) {
            if (whatsapp.startsWith("0")) {
                return "234" + whatsapp.substring(1);
            }
            if (whatsapp.startsWith("234")) {
                return whatsapp;
            }
            return "234" + whatsapp;
        } else {
            String ret;
            if(whatsapp.contains(dial)) {
                ret = whatsapp;
            } else {
                ret = dial + whatsapp;
            }
            return ret.replace("+", "");
        }
    }

    public String url() {
        return routes.SellerController.single(username).toString();
    }

    public long countListings() {
        DB.Filter filter = DB.where();
        filter.field("account.id", id);
        filter.field("trashed", false);
        return DB.count(Auto.class, filter);
    }

    public String name() {
        return Util.isNotEmpty(businessName) ? businessName : getFullName();
    }
}