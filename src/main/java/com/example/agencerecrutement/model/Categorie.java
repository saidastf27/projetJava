package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategorie;
    
    @Column(unique = true, nullable = false)
    private String libelle;
    
    
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<Journal> journaux = new ArrayList<>();
}


