package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offres")
@Data
@NoArgsConstructor

@AllArgsConstructor
public class Offre
 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOffre;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String competences;
    
    @Column(nullable = false)
    private Integer experienceRequise; // Nombre d'années d'expérience requises
    
    @Column(nullable = false)
    private Integer nbPostes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatOffre etat;
    
    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;
    
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();
    
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recrutement> recrutements = new ArrayList<>();
    
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicationOffre> publications = new ArrayList<>();
    
    public enum EtatOffre {
        ACTIVE,
        DESACTIVEE
    }
    
    public int getNbPostesDisponibles() {
        // Éviter les LazyInitializationException lorsque la collection recrutements
        // est accédée en dehors d'une session Hibernate (par ex. dans JavaFX).
        if (recrutements == null || !Hibernate.isInitialized(recrutements)) {
            // Si la collection n'est pas initialisée, on retourne simplement nbPostes.
            // Le calcul précis (nbPostes - recrutements.size()) sera fait côté service
            // lorsque la session est encore ouverte.
            return nbPostes != null ? nbPostes : 0;
        }
        return nbPostes != null ? nbPostes - recrutements.size() : 0;
    }
    
    public boolean estActive() {
        return etat == EtatOffre.ACTIVE;
    }
}


