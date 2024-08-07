package models;

import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name="address_areas")
@Getter @Setter
public class AddressArea extends Nameable implements Comparable<AddressArea>{

	public AddressArea() {}
	
	public AddressArea(String name) {
		this.setName(name);
	}

	@ModelFormat(AddressLocality.class)
	@ManyToOne
	@JoinColumn(name="parent_id")
	public AddressLocality parent;

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
	public int compareTo(AddressArea o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

    @PostLoad
    @PreUpdate
	public void init() {
		super.init();
    }
}