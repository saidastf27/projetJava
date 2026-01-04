package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "candidatures")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Candidature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidature;
    
    @Column(nullable = false)
    private LocalDate dateCandidature;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut = StatutCandidature.EN_ATTENTE;
    
    @Column(nullable = false)
    private boolean notifiee = false;
    
    @ManyToOne
    @JoinColumn(name = "id_demandeur", nullable = false)
    private DemandeurEmploi demandeur;
    
    @ManyToOne
    @JoinColumn(name = "id_offre", nullable = false)
    private Offre offre;
    
    @ManyToOne
    @JoinColumn(name = "id_edition", nullable = false)
    private Edition edition; // Édition du journal: où le demandeur a découvert l'offre
    
    public enum StatutCandidature {
        EN_ATTENTE,
        APPROUVEE,
        REJETEE,
        RECRUTEE
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidature that = (Candidature) o;
        return demandeur.getIdUtilisateur().equals(that.demandeur.getIdUtilisateur()) &&
               offre.getIdOffre().equals(that.offre.getIdOffre());
    }
    
    @Override
    public int hashCode() {
        return demandeur.getIdUtilisateur().hashCode() * 31 + offre.getIdOffre().hashCode();
    }
}

