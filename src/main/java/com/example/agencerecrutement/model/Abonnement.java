package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "abonnements")
@Data


@NoArgsConstructor
@AllArgsConstructor
public class Abonnement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbonnement;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatAbonnement etat;
    
    @Column(nullable = false)
    private LocalDate dateExpiration;
    
    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;
    
    @ManyToOne
    @JoinColumn(name = "code_journal", nullable = false)
    private Journal journal;
    
    @OneToMany(mappedBy = "abonnement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicationOffre> publications = new ArrayList<>();
    
    public enum EtatAbonnement {
        ACTIF,
        DESACTIVE,
        EXPIRE
    }
    
    public boolean estActif() {
        return etat == EtatAbonnement.ACTIF && dateExpiration.isAfter(LocalDate.now());
    }
}


