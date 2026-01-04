package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByEntrepriseIdUtilisateur(Long idEntreprise);
    List<Offre> findByEtat(Offre.EtatOffre etat);
    List<Offre> findByEntrepriseIdUtilisateurAndEtat(Long idEntreprise, Offre.EtatOffre etat);
    
    @Query("SELECT o FROM Offre o JOIN FETCH o.entreprise")
    List<Offre> findAllWithEntreprise();
}


