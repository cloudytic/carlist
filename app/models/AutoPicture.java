package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="auto_pictures", indexes = {
		@Index(name = "uuid_index", columnList="uuid"),
		@Index(name = "sort_index", columnList="sort"),
		@Index(name="uuid_sort_index", columnList="uuid,sort")
})
@Getter @Setter
public class AutoPicture extends ModelBase implements Comparable<AutoPicture>{

	@Constraints.Required
	@Column(nullable=false)
	public String name;

	@Constraints.Required
    @Column(nullable=false)
    public String uuid;

	public String originalName;

	public Integer sort;

	public Integer height;

	public Integer width;

	@Column(nullable=false)
	public boolean hide;

	public AutoPicture() {}

	public AutoPicture(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	@Override
	public int compareTo(AutoPicture o) {
		return this.name.compareTo(o.name);
	}

	public String alt(String caption) {
		return caption + " - " + sort;
	}

	public String getUrl(String size) {
		if(name.contains("youtu.be") || name.contains("youtube")) {
			try {
				if(name.contains("youtu.be")) {
					String[] arr =  name.split("youtu.be/");
					if(arr.length > 1) {
						String id = arr[1];
						return "https://img.youtube.com/vi/" + id + "/0.jpg";
					}
				} else {
					String id = name.split("v=")[1].split("&")[0];
					return "https://img.youtube.com/vi/" + id + "/0.jpg";
				}
			} catch (Exception e) {
				return "comingsoon.jpg";
			}
		}
		return size + "/" + name;
	}
}