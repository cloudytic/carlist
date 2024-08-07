package models;

import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name="auto_models")
@Getter @Setter
public class AutoModel extends Nameable implements Comparable<AutoModel>{

    @ModelFormat(AutoMake.class)
    @ManyToOne
	@JoinColumn(name="parent_id")
	public AutoMake parent;

	public AutoModel() {}

	public AutoModel(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public int compareTo(AutoModel o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@PostLoad
	@PreUpdate
	public void init() {
		super.init();
	}
}