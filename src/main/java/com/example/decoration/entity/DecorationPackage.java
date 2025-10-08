package com.example.decoration.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "decoration_package")
public class DecorationPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "package_organizers",
        joinColumns = @JoinColumn(name = "package_id"),
        inverseJoinColumns = @JoinColumn(name = "organizer_id")
    )
    @JsonIgnoreProperties("packages") // ignore 'packages' inside User
    private Set<User> organizers = new HashSet<>();

    public DecorationPackage() {}

    public DecorationPackage(String title, String description, Double price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Set<User> getOrganizers() { return organizers; }
    public void setOrganizers(Set<User> organizers) { this.organizers = organizers; }

    public void addOrganizer(User organizer) {
        this.organizers.add(organizer);
        organizer.getPackages().add(this);
    }

    public void removeOrganizer(User organizer) {
        this.organizers.remove(organizer);
        organizer.getPackages().remove(this);
    }
}
