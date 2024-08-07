package utilities;

import formatters.AmtFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AdCrawl {
    @Getter @Setter
    public String caption;

    @Getter @Setter
	@AmtFormat
	public Double price;

	@Getter @Setter
	public String currency;

    @Getter @Setter
    public Boolean negotiable;

    //@Getter @Setter
    //public String priceType;

    @Getter @Setter
    public String make;

    @Getter @Setter
    public String model;

    @Getter @Setter
    public String color;

    @Getter @Setter
    public String interiorColor;

    @Getter @Setter
    public String condition;

    @Getter @Setter
    public String secondCondition;

    @Getter @Setter
    public String transmission;

    @Getter @Setter
    public String bodyType;

    @Getter @Setter
    public String engineSize;

     @Getter @Setter
    public String horsePower;

    @Getter @Setter
    public String fuelType;

    @Getter @Setter
    public String mileage;

    @Getter @Setter
    public Integer year;

    @Getter @Setter
    public String trim;

    @Getter @Setter
    public String driveTrain;

    @Getter @Setter
    public Integer cylinder;

    @Getter @Setter
    public Integer seats;

    @Getter @Setter
    public String registered;

    @Getter @Setter
    public Boolean exchange;

    @Getter @Setter
    public String description;

    @Getter @Setter
    public String locality;

    @Getter @Setter
    public String state;

    @Getter @Setter
    public String href;

    @Getter @Setter
    public String ref;

    @Getter @Setter
    public String posted;

    @Getter @Setter
    public String contactName;

    @Getter @Setter
    public String contactLogo;

    @Getter @Setter
    public String contactPhone;

    @Getter @Setter
    public List<String> images = new ArrayList<>();

    @Getter @Setter
    public List<String> attributes = new ArrayList<>();

    @Override
    public String toString() {
        return "AdCrawl{" +
                "caption='" + caption + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", negotiable=" + negotiable +
                //", priceType='" + priceType + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", interiorColor='" + interiorColor + '\'' +
                ", condition='" + condition + '\'' +
                ", secondCondition='" + secondCondition + '\'' +
                ", transmission='" + transmission + '\'' +
                ", bodyType='" + bodyType + '\'' +
                ", engineSize='" + engineSize + '\'' +
                ", horsePower='" + horsePower + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", mileage='" + mileage + '\'' +
                ", year=" + year +
                ", trim='" + trim + '\'' +
                ", driveTrain='" + driveTrain + '\'' +
                ", cylinder=" + cylinder +
                ", seats=" + seats +
                ", registered='" + registered + '\'' +
                ", exchange=" + exchange +
                ", description='" + description + '\'' +
                ", locality='" + locality + '\'' +
                ", state='" + state + '\'' +
                ", href='" + href + '\'' +
                ", ref='" + ref + '\'' +
                ", posted='" + posted + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactLogo='" + contactLogo + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", images=" + images +
                ", attributes=" + attributes +
                '}';
    }
}