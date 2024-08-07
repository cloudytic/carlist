package models;

import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="auto_bodies")
@Getter @Setter
public class AutoBodyType extends Nameable implements Comparable<AutoBodyType>{

	public AutoBodyType() {}

	public AutoBodyType(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public int compareTo(AutoBodyType o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
	}
}