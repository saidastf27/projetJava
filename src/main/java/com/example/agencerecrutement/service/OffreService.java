package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.*;
import com.example.agencerecrutement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {
    
    private final OffreRepository offreRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final AbonnementRepository abonnementRepository;
    private final PublicationOffreRepository publicationOffreRepository;
    private final EditionRepository editionRepository;
    
    @Transactional
    public Offre creerOffre(Long idEntreprise, String titre, String competences,
                           Integer experienceRequise, Integer nbPostes) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        
        Offre offre = new Offre();
        offre.setTitre(titre);
        offre.setCompetences(competences);
        offre.setExperienceRequise(experienceRequise);
        offre.setNbPostes(nbPostes);
        offre.setEtat(Offre.EtatOffre.ACTIVE);
        offre.setEntreprise(entreprise);
        
        return offreRepository.save(offre);
    }
    
    @Transactional
    public PublicationOffre publierOffre(Long idOffre, Long idAbonnement, Long idEdition) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));
        
        Abonnement abonnement = abonnementRepository.findById(idAbonnement)
            .orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
        
        Edition edition = editionRepository.findById(idEdition)
            .orElseThrow(() -> new RuntimeException("Édition introuvable"));
        
        // Vérifier que l'abonnement est actif
        if (!abonnement.estActif()) {
            throw new RuntimeException("L'abonnement doit être actif pour publier une offre");
        }
        
        // Vérifier que l'offre est active
        if (!offre.estActive()) {
            throw new RuntimeException("L'offre doit être active pour être publiée");
        }
        
        // Vérifier que l'abonnement appartient à l'entreprise propriétaire de l'offre
        if (!abonnement.getEntreprise().getIdUtilisateur().equals(offre.getEntreprise().getIdUtilisateur())) {
            throw new RuntimeException("L'abonnement n'appartient pas à l'entreprise propriétaire de l'offre");
        }
        
        PublicationOffre publication = new PublicationOffre();
        publication.setOffre(offre);
        publication.setAbonnement(abonnement);
        publication.setEdition(edition);
        publication.setDatePublication(LocalDate.now());
        
        return publicationOffreRepository.save(publication);
    }
    
    public List<Offre> getOffresByEntreprise(Long idEntreprise) {
        return offreRepository.findByEntrepriseIdUtilisateur(idEntreprise);
    }
    
    public List<Offre> getOffresActives() {
        return offreRepository.findByEtat(Offre.EtatOffre.ACTIVE);
    }
    
    public Offre getOffre(Long idOffre) {
        return offreRepository.findById(idOffre)
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));
    }
    
    /**
     * Suppression logique d'une offre côté métier.
     * Règles :
     * - on NE supprime pas une offre qui possède déjà des candidatures, des recrutements
     *   ou des publications.
     * - dans ce cas, on demande plutôt de la désactiver.
     */
    @Transactional
    public void supprimerOffre(Long idOffre) {
        Offre offre = getOffre(idOffre);
        
        if (!offre.getCandidatures().isEmpty() ||
            !offre.getRecrutements().isEmpty() ||
            !offre.getPublications().isEmpty()) {
            throw new RuntimeException(
                "Impossible de supprimer une offre qui possède déjà des candidatures, des recrutements ou des publications. " +
                "Vous pouvez la désactiver à la place."
            );
        }
        
        offreRepository.delete(offre);
    }
    
    @Transactional
    public void desactiverOffre(Long idOffre) {
        Offre offre = getOffre(idOffre);
        offre.setEtat(Offre.EtatOffre.DESACTIVEE);
        offreRepository.save(offre);
    }
    
    @Transactional
    public void verifierEtDesactiverOffreSiNecessaire(Long idOffre) {
        Offre offre = getOffre(idOffre);
        if (offre.getNbPostesDisponibles() <= 0) {
            offre.setEtat(Offre.EtatOffre.DESACTIVEE);
            offreRepository.save(offre);
        }
    }
    
    public List<PublicationOffre> getPublicationsByOffre(Long idOffre) {
        return publicationOffreRepository.findByOffreIdOffre(idOffre);
    }
}

