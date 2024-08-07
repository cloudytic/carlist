package models;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="blog_tags")
@Getter @Setter
public class BlogTag extends Nameable implements Comparable<BlogTag> {

    public BlogTag() {}

    public BlogTag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(BlogTag o) {
        return this.getId().compareTo(o.getId());
    }
}
