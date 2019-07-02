package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.PostDTO;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = "Post.all", query = "SELECT OBJECT (u) FROM Post u ORDER BY u.created DESC")
public class Post
        implements Comparable<Post>,
        DTOable {

    @Id
    @GeneratedValue
    public Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imageURL;

    @Column(nullable = false)
    private String sourceURL;

    @Index
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Post() {
    }

    public Post(String title, String imageURL, String sourceURL) {
        this.title = title;
        this.imageURL = imageURL;
        this.sourceURL = sourceURL;
        this.onCreate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }


    @Override
    public int compareTo(Post post) {
        return this.created.compareTo(post.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        if (!getTitle().equals(post.getTitle())) return false;
        if (!getImageURL().equals(post.getImageURL())) return false;
        return getSourceURL().equals(post.getSourceURL());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getImageURL().hashCode();
        result = 31 * result + getSourceURL().hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public DTO toDTO() {
        return new PostDTO(id, title, imageURL, sourceURL, created);
    }

    @PrePersist
    protected void onCreate() {
        updated = created = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(System.currentTimeMillis());
    }

}
