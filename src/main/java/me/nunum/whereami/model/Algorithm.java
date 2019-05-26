package me.nunum.whereami.model;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.AlgorithmDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
public class Algorithm
        implements Identifiable<String>,
        Comparable<Algorithm>,
        DTOable {

    @Id
    @GeneratedValue
    @Column(name="ALG_ID")
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String name;


    private String authorName;


    private String paperURL;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="ALG_OWNER_ID", referencedColumnName="ALG_ID")
    private List<AlgorithmProvider> providers;


    public Algorithm() {
        //JPA
    }

    public Algorithm(String name, String authorName, String paperURL) {
        this.name = name;
        this.authorName = authorName;
        this.paperURL = paperURL;
        this.providers = new ArrayList<>();
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

    public void addProvider(AlgorithmProvider provider) {
        this.providers.add(provider);
    }

    public void removeProvider(AlgorithmProvider provider) {
        this.providers.remove(provider);
    }

    public Optional<AlgorithmProvider> algorithmProviderById(Long id){
        return this.providers.stream().filter(e -> e.getId().equals(id)).findFirst();
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
        return this.authorName.compareTo(algorithm.authorName);
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
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public DTO toDTO() {
        return new AlgorithmDTO(id, name, authorName, paperURL);
    }
}
