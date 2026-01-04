package com.example.agencerecrutement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "editions")
@Data
@NoArgsConstructor


@AllArgsConstructor
public class Edition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEdition;
    
    @Column(nullable = false)
    private Integer numeroEdition;
    
    @Column(nullable = false)
    private LocalDate dateParution;
    
    @ManyToOne
    @JoinColumn(name = "code_journal", nullable = false)
    private Journal journal;
    
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private List<PublicationOffre> publications = new ArrayList<>();
    
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private List<Candidature> candidatures = new ArrayList<>();
    
    @Override
    public String toString() {
        return journal.getNom() + " - N°" + numeroEdition + " (" + dateParution + ")";
    }
}

