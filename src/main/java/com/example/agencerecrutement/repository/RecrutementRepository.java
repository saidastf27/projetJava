package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Recrutement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecrutementRepository extends JpaRepository<Recrutement, Long> {
    List<Recrutement> findByOffreIdOffre(Long idOffre);
    List<Recrutement> findByEntrepriseIdUtilisateur(Long idEntreprise);
    List<Recrutement> findByDemandeurIdUtilisateur(Long idDemandeur);
    long countByOffreIdOffre(Long idOffre);
    
    @Query("SELECT r FROM Recrutement r JOIN FETCH r.offre JOIN FETCH r.entreprise JOIN FETCH r.demandeur")
    List<Recrutement> findAllWithRelations();
}


