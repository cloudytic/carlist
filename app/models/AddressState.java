package models;

import lombok.Getter;
import lombok.Setter;
import pojos.Param;
import services.DB;

import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name="address_states")
@Getter @Setter
public class AddressState extends Nameable implements Comparable<AddressState>{

	public AddressState() {}
	
	public AddressState(String name) {
		this.setName(name);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(AddressState o) {
		return this.getId().compareTo(o.getId());
	}
	
	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
    }

	@Override
	public List<AddressLocality> children(boolean only) {
		DB.Filter filter = DB.where();
		if(only) filter.field("parent", this);
		return DB.find(AddressLocality.class, filter, Param.getAll("name", "asc"));
	}
}