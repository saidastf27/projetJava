package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "recrutements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recrutement {


    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecrutement;
    
    
    @Column(nullable = false)
    private LocalDate dateRecrutement;
    
    @ManyToOne
    @JoinColumn(name = "id_offre")
    private Offre offre;
    
    @ManyToOne
    @JoinColumn(name = "id_demandeur", nullable = false)
    private DemandeurEmploi demandeur;
    
    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;
}


