package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Candidature;
import com.example.agencerecrutement.model.DemandeurEmploi;
import com.example.agencerecrutement.model.Edition;
import com.example.agencerecrutement.model.Offre;
import com.example.agencerecrutement.repository.CandidatureRepository;
import com.example.agencerecrutement.repository.DemandeurEmploiRepository;
import com.example.agencerecrutement.repository.EditionRepository;
import com.example.agencerecrutement.repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {
    
    private final CandidatureRepository candidatureRepository;
    private final OffreRepository offreRepository;
    private final DemandeurEmploiRepository demandeurEmploiRepository;
    private final EditionRepository editionRepository;
    
    @Transactional
    public Candidature postuler(Long idDemandeur, Long idOffre, Long idEdition) {
        DemandeurEmploi demandeur = demandeurEmploiRepository.findById(idDemandeur)
            .orElseThrow(() -> new RuntimeException("Demandeur d'emploi introuvable"));
        
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));
        
        Edition edition = editionRepository.findById(idEdition)
            .orElseThrow(() -> new RuntimeException("Édition introuvable"));
        
        // Vérifier que l'offre est active
        if (!offre.estActive()) {
            throw new RuntimeException("Impossible de postuler à une offre désactivée");
        }
        
        // Vérifier l'expérience : le demandeur doit avoir au moins l'expérience requise
        if (demandeur.getExperience() < offre.getExperienceRequise()) {
            throw new RuntimeException("Expérience insuffisante. Expérience requise : " + 
                                     offre.getExperienceRequise() + " ans, votre expérience : " + 
                                     demandeur.getExperience() + " ans");
        }
        
        // Vérifier qu'il n'y a pas déjà une candidature pour cette offre
        if (candidatureRepository.existsByDemandeurIdUtilisateurAndOffreIdOffre(idDemandeur, idOffre)) {
            throw new RuntimeException("Vous avez déjà postulé à cette offre");
        }
        
        Candidature candidature = new Candidature();
        candidature.setDemandeur(demandeur);
        candidature.setOffre(offre);
        candidature.setEdition(edition);
        candidature.setDateCandidature(LocalDate.now());
        
        return candidatureRepository.save(candidature);
    }
    
    public List<Candidature> getCandidaturesByOffre(Long idOffre) {
        return candidatureRepository.findByOffreIdOffre(idOffre);
    }
    
    public List<Candidature> getCandidaturesByDemandeur(Long idDemandeur) {
        return candidatureRepository.findByDemandeurIdUtilisateur(idDemandeur);
    }
    
    public boolean aDejaPostule(Long idDemandeur, Long idOffre) {
        return candidatureRepository.existsByDemandeurIdUtilisateurAndOffreIdOffre(idDemandeur, idOffre);
    }
    
    public List<Candidature> getCandidaturesWithRelations(Long idDemandeur) {
        return candidatureRepository.findByDemandeurIdUtilisateurWithRelations(idDemandeur);
    }
    
    @Transactional
    public void updateCandidature(Candidature candidature) {
        candidatureRepository.save(candidature);
    }
}


