package models;

import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;
import pojos.Param;
import services.DB;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="address_localities")
@Getter @Setter
public class AddressLocality extends Nameable implements Comparable<AddressLocality>{

    @ModelFormat(AddressState.class)
    @ManyToOne
	@JoinColumn(name="parent_id")
	public AddressState parent;

	public AddressLocality() {}
	
	public AddressLocality(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public int compareTo(AddressLocality o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
	}

	@Override
	public List<AddressArea> children(boolean only) {
		DB.Filter filter = DB.where();
		if(only) filter.field("parent", this);
        return DB.find(AddressArea.class, filter, Param.getAll("name", "asc"));
    }
}