package models;

import controllers.Util;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="auto_types")
@Getter @Setter
public class AutoType extends Nameable implements Pluralable, Comparable<AutoType> {

	@Constraints.Required
	public String plural;

	public AutoType() {}

	public AutoType(String name, String plural) {
		this.name = name;
		this.plural = plural;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(AutoType o) {
		return this.getId().compareTo(o.getId());
	}
	
	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
		if(Util.isEmpty(plural)) {
			plural = name;
		}
    }
}