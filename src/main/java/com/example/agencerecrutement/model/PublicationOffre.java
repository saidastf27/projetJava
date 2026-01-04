package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "publications_offres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationOffre {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPublication;
    
    @Column(nullable = false)
    private LocalDate datePublication;
    
    @ManyToOne
    @JoinColumn(name = "id_offre", nullable = false)
    private Offre offre;
    
    @ManyToOne
    @JoinColumn(name = "id_abonnement", nullable = false)
    private Abonnement abonnement;
    
    @ManyToOne
    @JoinColumn(name = "id_edition", nullable = false)
    private Edition edition;
}


