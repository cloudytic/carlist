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
@Table(name="auto_categories")
@Getter @Setter
public class AutoCategory extends Nameable implements Pluralable, Comparable<AutoCategory> {

	@Constraints.Required
	public String plural;

	public AutoCategory() {}

	public AutoCategory(String name, String plural) {
		this.name = name;
		this.plural = plural;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(AutoCategory o) {
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