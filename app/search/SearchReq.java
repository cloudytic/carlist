package search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchReq implements Serializable {
	@Getter @Setter public String search;

	@Getter @Setter public Boolean active;

	@Getter @Setter public Long accountId;


	@Getter @Setter public String make;
	@Getter @Setter public Long makeId;

	@Getter @Setter public String model;
	@Getter @Setter public Long modelId;

	@Getter @Setter public Integer year;
	@Getter @Setter public String yearRange;
	@Getter @Setter public String priceRange;
	@Getter @Setter public String mileageRange;

	@Getter @Setter public String priceUp;

	@Getter @Setter public String condition;
	@Getter @Setter public Long conditionId;

	@Getter @Setter public String transmission;
	@Getter @Setter public Long transmissionId;

	@Getter @Setter public String fuelType;
	@Getter @Setter public Long fuelTypeId;

	@Getter @Setter public String bodyType;
	@Getter @Setter public Long bodyTypeId;

	@Getter @Setter public String colorExterior;
	@Getter @Setter public Long colorExteriorId;

	@Getter @Setter public String colorInterior;
	@Getter @Setter public Long colorInteriorId;

	@Getter @Setter public String engine;
	@Getter @Setter public String drive;
	@Getter @Setter public String seats;
	@Getter @Setter public String feature;
	@Getter @Setter public List<String> features = new ArrayList<>();

	@Getter @Setter public String state;
	@Getter @Setter public Long stateId;

	@Getter @Setter public String locality;
	@Getter @Setter public Long localityId;

	@Getter @Setter public String contactPhone;

	@Getter @Setter public String start_date;
	@Getter @Setter public String end_date;

	@Getter @Setter public String f;

	@Getter @Setter public Boolean trashed;
	@Getter @Setter public Boolean published;
	
	@Getter @Setter public Boolean searcheable;

	@Getter @Setter public Boolean hasAccount;
	@Getter @Setter public Boolean visible;

	public void init() {
		if(Util.isNotEmpty(model) && model.startsWith("make-")) {
			make = model.replace("make-", "");
			model = null;
		}
		if(Util.isNotEmpty(locality) && locality.startsWith("state-")) {
			state = locality.replace("state-", "");
			locality = null;
		}
	}


	public SearchReq copy() {
		return SerializationUtils.clone(this);
	}

	@JsonIgnore
	public String getCaption() {
		String caption = (Util.isNotEmpty(getHeader()) ? getHeader(): "Cars") + " For sale" + address();
		return Util.capitalize(caption);
	}

	@JsonIgnore
	public String getHeader() {
		String ret = "";

		if(Util.isNotEmpty(condition)) {
			String cond = condition;
			if(cond.equals("foreign-used")) {
				cond += " / Tokunbo";
			}
			ret += cond + " ";
		} else {
			ret += "Used ";
		}

		ret += Util.isNotEmpty(transmission) ? transmission + " ": "";
		ret += year != null ? year + " ": "";

		String mModel = (Util.isNotEmpty(make) ? make + " ": "") + (Util.isNotEmpty(model) ? model: "");
		mModel += " " + (Util.isNotEmpty(bodyType) ? bodyType + " ": "");

		if(Util.isNotEmpty(mModel)) {
			ret += mModel + " Cars";
		} else {
			ret += "Cars";
		}

		ret += Util.isNotEmpty(colorExterior) ?  " " + colorExterior: "";

		ret += Util.isNotEmpty(fuelType) ? " { Fuel Type: " + fuelType + " }": "";
		ret += Util.isNotEmpty(priceRange) ? " { Price between: " + priceRange + " }": "";
		ret += Util.isNotEmpty(mileageRange) ? " { Mileage between: " + mileageRange + " }": "";

		return ret.replace('_', ' ').replace('-', ' ').trim();
	}

	@JsonIgnore
	public String address(){
		String address = Util.isNotEmpty(locality) ? locality + " ": (Util.isNotEmpty(state) ? state: "");
		if(Util.isNotEmpty(address)) {
			return " in " + address;
		} else {
			return " in Nigeria";
		}
	}


}

