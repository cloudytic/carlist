package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="images")
@Getter @Setter
public class Image extends ModelBase implements Comparable<Image> {

	@Constraints.Required
	@Column(nullable = false)
	public String name;

	public String originalName;

	public Image() {
	}

	public Image(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(Image o) {
		return this.name.compareTo(o.name);
	}
}