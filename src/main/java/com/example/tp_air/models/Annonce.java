package com.example.tp_air.models;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "annonce")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 64, message = "Le titre ne doit pas dépasser 64 caractères")
    @Column(length = 64, nullable = false)
    private String title;

    @Size(max = 256, message = "La description ne doit pas dépasser 256 caractères")
    @Column(length = 256)
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 64)
    @Column(length = 64)
    private String address;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email de contact est obligatoire")
    @Size(max = 64)
    @Column(length = 64)
    private String mail;

    @Column(name = "date")
    private Timestamp date;

    @Enumerated(EnumType.STRING)
    private AnnonceStatus status;

    @NotNull(message = "L'auteur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @NotNull(message = "La catégorie est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Annonce() {}

    public Annonce(String title, String description, String address, String mail) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.mail = mail;
        this.date = Timestamp.from(Instant.now());
        this.status = AnnonceStatus.DRAFT;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public AnnonceStatus getStatus() { return status; }
    public void setStatus(AnnonceStatus status) { this.status = status; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}