package me.nunum.whereami.model;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.AlgorithmDTO;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Algorithm
        implements Identifiable<String>,
        Comparable<Algorithm>,
        DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String name;


    private String authorName;


    private String paperURL;


    private Float rating;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    public Algorithm() {
        //JPA
    }

    public Algorithm(String name, String authorName, String paperURL) {
        this.name = name;
        this.authorName = authorName;
        this.paperURL = paperURL;
        this.rating = 0.0f;
    }


    @Override
    public boolean is(String id) {
        return this.name.equalsIgnoreCase(id);
    }

    @Override
    public String id() {
        return this.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPaperURL() {
        return paperURL;
    }

    public Float getRating() {
        return rating;
    }

    @PrePersist
    protected void onCreate() {
        updated = created = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(System.currentTimeMillis());
    }

    @Override
    public int compareTo(Algorithm algorithm) {
        return this.rating.compareTo(algorithm.rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Algorithm)) return false;

        Algorithm algorithm = (Algorithm) o;

        if (name != null ? !name.equals(algorithm.name) : algorithm.name != null) return false;
        return created != null ? created.equals(algorithm.created) : algorithm.created == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Algorithm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", authorName='" + authorName + '\'' +
                ", paperURL='" + paperURL + '\'' +
                ", rating=" + rating +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public DTO toDTO() {
        return new AlgorithmDTO(id, name, authorName, paperURL, rating);
    }
}
