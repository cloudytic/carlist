package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.StaticAssets;
import controllers.Util;
import formatters.ModelFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name="blog_articles")
@Getter @Setter
public class BlogArticle extends ModelBase {

    @Column(length=1000)
    @Lob
    public String title;

    @Column(length=100000)
    @Lob
    public String content;

    @Column(length=10000)
    @Lob
    public String summary;

    /*
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="image_id")
    public BlogImage image;*/

    @Column(unique=true, nullable=false)
    public String slug;

    @Column(length=5000)
    @Lob
    public String metaDescription;

    @ModelFormat(BlogCategory.class)
    @ManyToOne
    @JoinColumn(name="category_id")
    public BlogCategory category;

    @Getter @Setter
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="blog_tag_mapper",
            joinColumns=@JoinColumn(name="article_id"),
            inverseJoinColumns=@JoinColumn(name="tag_id"))
    public Set<BlogTag> tags = new HashSet<>();

    @ModelFormat(Admin.class)
    @ManyToOne
    @JoinColumn(name="author_id")
    public Admin author;

    @Column(nullable=false)
    public boolean published;

    @Column(nullable=false)
    public boolean trashed;

    public String imageUrl;

    @Column(nullable=false)
    public long views;

    @JsonIgnore
    @Transient
    public Map<String, String> tagList = new HashMap<>();

    public String imageUrl() {
        if(Util.isNotEmpty(imageUrl)) {
            return imageUrl;
        }
        return StaticAssets.getUrl("image_not_available.png");
    }

    @PostLoad
    public void init() {
        if(Util.isEmpty(imageUrl)) {
            imageUrl = getFirstImage();
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        imageUrl = getFirstImage();
    }

    private String getFirstImage() {
        Elements elements = Jsoup.parse(content).getElementsByTag("img");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            return element.attr("src");
        }
        return null;
    }
}
