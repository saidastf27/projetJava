package com.example.agencerecrutement.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMINISTRATEUR")
@Data

@NoArgsConstructor

@EqualsAndHashCode(callSuper = true)
public class Administrateur extends Utilisateur {
    
    public Administrateur(String login, String motDePasse) {
        this.setLogin(login);
        this.setMotDePasse(motDePasse);
        this.setRole(Role.ADMINISTRATEUR);
        this.setActif(true);
    }
}

