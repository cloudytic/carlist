package models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

@Entity
@Table(name="blog_images")
@Getter @Setter
public class BlogImage extends ModelBase implements Comparable<BlogImage> {

	@Constraints.Required
	@Column(nullable=false)
	public String name;

	public String originalName;

	public BlogImage() {
	}

	public BlogImage(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(BlogImage o) {
		return this.name.compareTo(o.name);
	}
}