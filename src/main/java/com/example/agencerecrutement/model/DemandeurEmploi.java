package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DEMANDEUR_EMPLOI")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DemandeurEmploi extends Utilisateur {
    
    @Column
    private String nom;
    
    @Column
    private String prenom;
    
    @Column
    private String adresse;
    
    @Column
    private String telephone;
    
    private String fax;
    
    @Column
    private String diplome;
    
    @Column
    private Integer experience; // Nombre d'années d'expérience.
    
    @Column
    private Double salaireSouhaite;
    
    @OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();
    
    @OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();
    
    public DemandeurEmploi(String login, String motDePasse, String nom, String prenom,
                          String adresse, String telephone, String fax, String diplome,
                          Integer experience, Double salaireSouhaite) {
        this.setLogin(login);
        this.setMotDePasse(motDePasse);
        this.setRole(Role.DEMANDEUR_EMPLOI);
        this.setActif(true);
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.fax = fax;
        this.diplome = diplome;
        this.experience = experience;
        this.salaireSouhaite = salaireSouhaite;
    }
    
    public void ajouterDocument(Document document) {
        documents.add(document);
        document.setDemandeur(this);
    }
    
    public void supprimerDocument(Document document) {
        documents.remove(document);
        document.setDemandeur(null);
    }
    
    public List<Document> getDocumentsValides() {
        return documents.stream()
                .filter(Document::isValide)
                .toList();
    }
    
    public Document getCvValide() {
        return documents.stream()
                .filter(doc -> "CV".equals(doc.getTypeDocument()) && doc.isValide())
                .findFirst()
                .orElse(null);
    }
}


