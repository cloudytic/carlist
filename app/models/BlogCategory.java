package models;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

@Entity
@Table(name="blog_categories")
@Getter @Setter
public class BlogCategory extends Nameable implements Pluralable, Comparable<BlogCategory> {

    @Constraints.Required
    public String plural;

    public BlogCategory() {}

    public BlogCategory(String name, String plural) {
        this.name = name;
        this.plural = plural;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(BlogCategory o) {
        return this.getId().compareTo(o.getId());
    }
}
