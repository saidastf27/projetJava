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
public class RecrutementService {
    
    private final RecrutementRepository recrutementRepository;
    private final OffreRepository offreRepository;
    private final CandidatureRepository candidatureRepository;
    private final DemandeurEmploiRepository demandeurEmploiRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final OffreService offreService;
    
    @Transactional
    public Recrutement recruter(Long idEntreprise, Long idOffre, Long idDemandeur) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));
        
        DemandeurEmploi demandeur = demandeurEmploiRepository.findById(idDemandeur)
            .orElseThrow(() -> new RuntimeException("Demandeur d'emploi introuvable"));
        
        // Vérifier que l'offre appartient à l'entreprise
        if (!offre.getEntreprise().getIdUtilisateur().equals(idEntreprise)) {
            throw new RuntimeException("Cette offre n'appartient pas à votre entreprise");
        }
        
        // Vérifier que le demandeur a postulé à cette offre
        candidatureRepository
            .findByDemandeurIdUtilisateurAndOffreIdOffre(idDemandeur, idOffre)
            .orElseThrow(() -> new RuntimeException("Ce candidat n'a pas postulé à cette offre"));
        
        // Vérifier qu'il reste des postes disponibles
        int nbPostesDisponibles = offre.getNbPostesDisponibles();
        if (nbPostesDisponibles <= 0) {
            throw new RuntimeException("Tous les postes ont été pourvus pour cette offre");
        }
        
        Recrutement recrutement = new Recrutement();
        recrutement.setEntreprise(entreprise);
        recrutement.setOffre(offre);
        recrutement.setDemandeur(demandeur);
        recrutement.setDateRecrutement(LocalDate.now());
        
        recrutement = recrutementRepository.save(recrutement);
        
        // Vérifier et désactiver l'offre si nécessaire
        offreService.verifierEtDesactiverOffreSiNecessaire(idOffre);
        
        return recrutement;
    }
    
    public List<Recrutement> getRecrutementsByOffre(Long idOffre) {
        return recrutementRepository.findByOffreIdOffre(idOffre);
    }
    
    public List<Recrutement> getRecrutementsByEntreprise(Long idEntreprise) {
        return recrutementRepository.findByEntrepriseIdUtilisateur(idEntreprise);
    }
    
    public List<Recrutement> getRecrutementsByDemandeur(Long idDemandeur) {
        return recrutementRepository.findByDemandeurIdUtilisateur(idDemandeur);
    }
    
    public List<Recrutement> getAllRecrutements() {
        return recrutementRepository.findAll();
    }
}

