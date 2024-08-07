package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.Util;
import formatters.AmtFormat;
import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;
import pojos.Param;
import services.DB;
import services.PidCalculator;

import jakarta.persistence.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name="autos")
@Getter @Setter
public class Auto extends ModelBase {

    @Column(unique=true, nullable=false)
    public String uuid;

    @Constraints.Required
    @ModelFormat(AutoCategory.class)
    @ManyToOne
    @JoinColumn(name="category_id")
    public AutoCategory category;

    @Constraints.Required
    @AmtFormat
    public Double price;

    public String priceCurrency;

    public Boolean priceNegotiable;

    public String caption;

    @Constraints.Required
    @Column(length=100000)
    @Lob
    public String description;

    @Constraints.Required
    @ModelFormat(AutoModel.class)
    @ManyToOne
    @JoinColumn(name="model_id")
    public AutoModel model;

    @Constraints.Required
    public Integer year;

    @ModelFormat(AutoColor.class)
    @ManyToOne
    @JoinColumn(name="color_ext_id")
    public AutoColor colorExterior;

    @ModelFormat(AutoColor.class)
    @ManyToOne
    @JoinColumn(name="color_int_id")
    public AutoColor colorInterior;

    @Constraints.Required
    @ModelFormat(AutoCondition.class)
    @ManyToOne
    @JoinColumn(name="condition_id")
    public AutoCondition condition;
    
    public String secondCondition;

    @Constraints.Required
    @ModelFormat(AutoTransmission.class)
    @ManyToOne
    @JoinColumn(name="transmission_id")
    public AutoTransmission transmission;

    public Integer mileage;

    public String vin;

    public Integer seats;

    /*@ModelFormat(AutoEngineType.class)
    @ManyToOne
    @JoinColumn(name="engineType_id")
    public AutoEngineType engineType;*/

    public String engineSize;

    public boolean registered;

    @Getter @Setter
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="auto_feature_mapper",
            joinColumns=@JoinColumn(name="auto_id"),
            inverseJoinColumns=@JoinColumn(name="feature_id"))
    public Set<AutoFeature> features = new HashSet<>();

    public boolean exchangePossible;

    @ModelFormat(AutoFuelType.class)
    @ManyToOne
    @JoinColumn(name="fuelType_id")
    public AutoFuelType fuelType;

    @ModelFormat(AutoBodyType.class)
    @ManyToOne
    @JoinColumn(name="bodyType_id")
    public AutoBodyType bodyType;

    @ModelFormat(AutoDriveTrain.class)
    @ManyToOne
    @JoinColumn(name="driveTrain_id")
    public AutoDriveTrain drive;

    public String driveTrain;

    public String trim;

    public String horsePower;

    public Integer cylinder;

    @Column(nullable=false)
    public boolean published;

    @Column(nullable=false)
    public boolean trashed;

    @Constraints.Required
    @ModelFormat(AddressLocality.class)
    @ManyToOne
    @JoinColumn(name="locality_id")
    public AddressLocality locality;
    
    @ModelFormat(Account.class)
    @ManyToOne
    @JoinColumn(name="account_id")
    public Account account;

    @ManyToOne
    @JoinColumn(name="contact_id")
    public Contact contact;

    public String crawlHref;

    public Boolean notVisible;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date boostedOn;

    @PostLoad
    public void init() {
        if(contact != null && contact.notFound) {
            this.trashed = true;
        }
        if(boostedOn == null) {
            this.boostedOn = this.created;
        }
        if(notVisible == null) {
            this.notVisible = Util.isNotEmpty(crawlHref);
        }

        if(Util.isEmpty(priceCurrency)) {
            priceCurrency = "₦";
        }
    }

    @PrePersist
    @PreUpdate
    public void preSave() {
        if(Util.isEmpty(caption)) {
            caption = getHeader(false);
        }
        if(boostedOn == null) {
            this.boostedOn = this.created;
        }
        if(notVisible == null) {
            this.notVisible = Util.isNotEmpty(crawlHref);
        }
    }

    @Transient
    private List<AutoPicture> pictures;

    public List<AutoPicture> getPictures() {
        if(pictures == null) {
            DB.Filter filter = DB.where();
            filter.field("uuid").eq(this.uuid);
            Param param = Param.getAll("sort", "asc");
            pictures = DB.find(AutoPicture.class, filter, param);
        }
        return pictures;
    }

    public void refreshPictures() {
        pictures = null;
    }

    public static final DecimalFormat df = new DecimalFormat("#,###");

    public String planPrice() {
        return df.format(this.price);
    }

    public String toPrice() {
        String pr = df.format(this.price);
        return "₦ " + pr;
    }

    @Transient
    @Getter
    private String search;

    public String getSearch() {
        return this.caption
                + ((model != null) ? " " + model.name + " " + model.parent.name: "")
                + ((locality != null) ? " " + locality.name + " " + locality.parent.name: "")
                + ((condition != null) ? " " + condition.name: "")
                + ((colorExterior != null) ? " " + colorExterior.name: "")
                + ((colorInterior != null) ? " " + colorInterior.name: "")
                + ((transmission != null) ? " " + transmission.name: "")
                + ((fuelType != null) ? " " + fuelType.name: "")
                + ((bodyType != null) ? " " + bodyType.name: "");
    }

    @JsonIgnore
    @Transient
    public Map<String, String> feat = new HashMap<>();

    @JsonIgnore
    @Transient
    public String featureIds;

    @Transient
    @Setter
    private String ref;

    public String getRef() {
        return PidCalculator.getRef(this.id);
    }

    @JsonIgnore
    public String getHeader(boolean cond) {
        String model = this.model.toString();
        String make = this.model.parent.toString();
        String ret = make + " " + model;

        String condition = (this.condition != null)? this.condition + " " : "";
        if(cond) {
            ret = condition + ret;
        }

        ret += (this.year != null)? " " + this.year : "";
        ret += (this.colorExterior != null && !"Other".equalsIgnoreCase(this.colorExterior.name))? " " + this.colorExterior : "";

        ret += (this.bodyType != null)? " " + this.bodyType : "";

        return ret.replace('_', ' ').replace('-', ' ').trim();
    }

    @JsonIgnore
    public String getH1() {
        return getHeader(true) + " in " + address() + " - " + getRef();
    }

    @JsonIgnore
    public String address(){
        String localityName = locality.toString();
        String stateName = locality.parent.name;

        return localityName + ", " + stateName;
    }

    public final static SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
    public String showDate() {
        Date today = Util.getStartOfDay(new Date());
        String date = "";
        try {
            if(boostedOn.after(created)) {
                date += "Updated " + format.format(boostedOn);
            } else {
                if (created.after(today)) {
                    return "Posted Today";
                } else {
                    date += "Posted " + format.format(created);
                }
            }
        } catch (Exception e) {}
        return date;
    }

    @JsonIgnore
    public String getUrl(){
        return slugUrl(getHeader(true) + " in " + address(), id);
    }

    public static String slugUrl(String url, Long id){
        if(Util.isEmpty(url)) return id.toString();
        return Util.makeSlug(url.replace("/", " ")) + "-" + id;
    }
}
