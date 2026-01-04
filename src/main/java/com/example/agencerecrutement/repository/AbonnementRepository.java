package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByEntrepriseIdUtilisateur(Long idEntreprise);
    List<Abonnement> findByEntrepriseIdUtilisateurAndEtat(Long idEntreprise, Abonnement.EtatAbonnement etat);
    List<Abonnement> findByJournalCodeJournal(String codeJournal);
    List<Abonnement> findByDateExpirationBefore(LocalDate date);
}


