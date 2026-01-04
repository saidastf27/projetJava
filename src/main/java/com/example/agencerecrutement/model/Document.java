package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nomFichier;
    
    @Column(nullable = false)
    private String typeMime;
    
    @Column(nullable = false)
    private Long taille;
    
    @Column(nullable = false)
    private String cheminStockage;
    
    @Column(nullable = false)
    private boolean valide = false;
    
    @Column(nullable = false)
    private LocalDateTime dateUpload = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "demandeur_id", nullable = false)
    private DemandeurEmploi demandeur;
    
    @Column(nullable = false)
    private String typeDocument; // "CV", "LETTRE_MOTIVATION", etc.
}
