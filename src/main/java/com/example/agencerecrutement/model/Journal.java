package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journaux")
@Data
@NoArgsConstructor


@AllArgsConstructor
public class Journal
 {
    
    @Id
    @Column(unique = true, nullable = false)
    private String codeJournal;
    
    @Column(nullable = false)
    private String nom;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicite periodicite;
    
    @Column(nullable = false)
    private String langue;
    
    @ManyToOne
    @JoinColumn(name = "id_categorie", nullable = false)
    private Categorie categorie;
    
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Edition> editions = new ArrayList<>();
    
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abonnement> abonnements = new ArrayList<>();
    
    public enum Periodicite {
        QUOTIDIEN,
        HEBDOMADAIRE,
        MENSUEL,
        BIMENSUEL,
        TRIMESTRIEL
    }
}


