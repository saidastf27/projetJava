package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Abonnement;
import com.example.agencerecrutement.model.Entreprise;
import com.example.agencerecrutement.model.Journal;
import com.example.agencerecrutement.repository.AbonnementRepository;
import com.example.agencerecrutement.repository.EntrepriseRepository;
import com.example.agencerecrutement.repository.JournalRepository;
import com.example.agencerecrutement.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntrepriseService {
    
    private final EntrepriseRepository entrepriseRepository;
    private final AbonnementRepository abonnementRepository;
    private final JournalRepository journalRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthentificationService authentificationService;
    
    @Transactional
    public Entreprise creerEntreprise(String login, String motDePasse, String raisonSociale,
                                     String adresse, String telephone, String descriptionActivite) {
        if (utilisateurRepository.existsByLogin(login)) {
            throw new RuntimeException("Ce login existe déjà");
        }
        
        String motDePasseHash = authentificationService.hasherMotDePasse(motDePasse);
        Entreprise entreprise = new Entreprise(login, motDePasseHash, raisonSociale, 
                                               adresse, telephone, descriptionActivite);
        return entrepriseRepository.save(entreprise);
    }
    
    public List<Abonnement> getAbonnements(Long idEntreprise) {
        return abonnementRepository.findByEntrepriseIdUtilisateur(idEntreprise);
    }
    
    public List<Abonnement> getAbonnementsActifs(Long idEntreprise) {
        return abonnementRepository.findByEntrepriseIdUtilisateurAndEtat(idEntreprise, 
                                                                         Abonnement.EtatAbonnement.ACTIF);
    }
    
    @Transactional
    public Abonnement souscrireAbonnement(Long idEntreprise, String codeJournal, LocalDate dateExpiration) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        
        Journal journal = journalRepository.findById(codeJournal)
            .orElseThrow(() -> new RuntimeException("Journal introuvable"));
        
        // Vérifier qu'il n'y a pas d'abonnement actif pour ce journal
        List<Abonnement> abonnementsExistant = abonnementRepository
            .findByEntrepriseIdUtilisateurAndEtat(idEntreprise, Abonnement.EtatAbonnement.ACTIF);
        
        for (Abonnement ab : abonnementsExistant) {
            if (ab.getJournal().getCodeJournal().equals(codeJournal) && ab.estActif()) {
                throw new RuntimeException("Un abonnement actif existe déjà pour ce journal. " +
                                         "Veuillez attendre l'expiration avant de vous réabonner.");
            }
        }
        
        Abonnement abonnement = new Abonnement();
        abonnement.setEntreprise(entreprise);
        abonnement.setJournal(journal);
        abonnement.setEtat(Abonnement.EtatAbonnement.ACTIF);
        abonnement.setDateExpiration(dateExpiration);
        
        return abonnementRepository.save(abonnement);
    }
    
    public boolean aAbonnementActif(Long idEntreprise) {
        List<Abonnement> abonnementsActifs = getAbonnementsActifs(idEntreprise);
        return abonnementsActifs.stream().anyMatch(Abonnement::estActif);
    }
}

