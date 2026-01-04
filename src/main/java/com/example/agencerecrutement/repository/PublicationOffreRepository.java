package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.PublicationOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationOffreRepository extends JpaRepository<PublicationOffre, Long> {
    List<PublicationOffre> findByOffreIdOffre(Long idOffre);
    List<PublicationOffre> findByAbonnementIdAbonnement(Long idAbonnement);
    List<PublicationOffre> findByEditionIdEdition(Long idEdition);
}


