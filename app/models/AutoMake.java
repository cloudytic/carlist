package models;

import lombok.Getter;
import lombok.Setter;
import pojos.Param;
import services.DB;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="auto_makes")
@Getter @Setter
public class AutoMake extends Nameable implements Comparable<AutoMake>{

	public AutoMake() {}

	public AutoMake(String name) {
		this.setName(name);
	}

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
	public int compareTo(AutoMake o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

    @PostLoad
    @PreUpdate
	public void init() {
		super.init();
    }

	@Override
	public List<AutoModel> children(boolean only) {
		DB.Filter filter = DB.where();
		if(only) filter.field("parent", this);
		return DB.find(AutoModel.class, filter, Param.getAll("name", "asc"));
	}
}