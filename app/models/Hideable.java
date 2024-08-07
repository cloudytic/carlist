package models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
public class Hideable extends ModelBase {
	@Column(nullable=false)
	public boolean hide;

	public Hideable getParent() {return null;}

	public List<? extends Hideable> children(boolean only) {
		return Collections.emptyList();
	}
}
