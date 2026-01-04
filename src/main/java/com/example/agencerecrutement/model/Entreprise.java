package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ENTREPRISE")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Entreprise extends Utilisateur {
    
    @Column
    private String raisonSociale;
    
    @Column
    private String adresse;
    
    @Column
    private String telephone;
    
    @Column(columnDefinition = "TEXT")
    private String descriptionActivite;
    
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abonnement> abonnements = new ArrayList<>();
    
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offre> offres = new ArrayList<>();
    
    public Entreprise(String login, String motDePasse, String raisonSociale, 
                     String adresse, String telephone, String descriptionActivite) {
        this.setLogin(login);
        this.setMotDePasse(motDePasse);
        this.setRole(Role.ENTREPRISE);
        this.setActif(true);
        this.raisonSociale = raisonSociale;
        this.adresse = adresse;
        
        this.telephone = telephone;
        this.descriptionActivite = descriptionActivite;
    }
}

