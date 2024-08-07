package models;

import controllers.Util;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
@Getter @Setter
public class Nameable extends Hideable {
	@Constraints.Required
	public String name;

	@Constraints.Required
	public String slug;

	@PostLoad
	@PreUpdate
	@PrePersist
	public void init() {
		if(Util.isEmpty(slug)) {
			this.slug = Util.makeSlug(name);
		}
	}
}
