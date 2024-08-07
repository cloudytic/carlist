package models;

import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="auto_drive_trains")
@Getter @Setter
public class AutoDriveTrain extends Nameable implements Comparable<AutoDriveTrain>{

	public AutoDriveTrain() {}

	public AutoDriveTrain(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public int compareTo(AutoDriveTrain o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
	}
}