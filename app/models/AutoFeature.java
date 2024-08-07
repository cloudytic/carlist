package models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="auto_features")
@Getter @Setter
public class AutoFeature extends Nameable implements Comparable<AutoFeature>{

	public AutoFeature() {}

	public AutoFeature(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public int compareTo(AutoFeature o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
	}
}