package me.nunum.whereami.model;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.AlgorithmDTO;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Algorithm
        implements Identifiable<String>,
        Comparable<Algorithm>,
        DTOable {

    @Id
    @GeneratedValue
    @Column(name = "ALG_ID")
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String name;


    private String authorName;


    private String paperURL;


    @Index
    private boolean isApproved;


    @ManyToOne(fetch = FetchType.LAZY)
    private Device publisher;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ALG_OWNER_ID", referencedColumnName = "ALG_ID")
    private List<AlgorithmProvider> providers;


    public Algorithm() {
        //JPA
    }

    /**
     * @param name
     * @param authorName
     * @param paperURL
     * @param publisher
     */
    public Algorithm(String name, String authorName, String paperURL, Device publisher) {
        this(name, authorName, paperURL, false, publisher);
    }

    public Algorithm(String name,
                     String authorName,
                     String paperURL,
                     boolean isApproved,
                     Device publisher) {
        this.name = name;
        this.authorName = authorName;
        this.paperURL = paperURL;
        this.isApproved = isApproved;
        this.publisher = publisher;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPaperURL() {
        return paperURL;
    }

    public void setPaperURL(String paperURL) {
        this.paperURL = paperURL;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void addProvider(AlgorithmProvider provider) {
        this.providers.add(provider);
    }

    public void removeProvider(AlgorithmProvider provider) {
        this.providers.remove(provider);
    }

    public Optional<AlgorithmProvider> algorithmProviderById(Long id) {
        return this.providers.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public Optional<AlgorithmProvider> firstAlgorithmProvider() {
        return this.providers.stream().findFirst();
    }

    public boolean isPublisher(final Device publisher) {
        return this.publisher.equals(publisher);
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

        if (!Objects.equals(name, algorithm.name)) return false;
        return Objects.equals(created, algorithm.created);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public DTO toDTO() {
        return new AlgorithmDTO(id,
                name,
                authorName,
                paperURL,
                isApproved,
                providers.stream().map(e -> {
                    final HashMap<String, Object> map = new HashMap<>(4);
                    map.put("id", e.getId());
                    map.put("method", e.getMethod().toString());
                    map.put("isDeployed", e.isDeployed());
                    map.put("predictionRate", e.getPredictionRate());

                    return map;
                }).collect(Collectors.toList()));
    }
}
