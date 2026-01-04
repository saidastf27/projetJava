package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.DemandeurEmploi;
import com.example.agencerecrutement.repository.DemandeurEmploiRepository;
import com.example.agencerecrutement.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DemandeurEmploiService {
    
    private final DemandeurEmploiRepository demandeurEmploiRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthentificationService authentificationService;
    
    @Transactional
    public DemandeurEmploi creerDemandeurEmploi(String login, String motDePasse, String nom, 
                                                String prenom, String adresse, String telephone,
                                                String fax, String diplome, Integer experience,
                                                Double salaireSouhaite) {
        if (utilisateurRepository.existsByLogin(login)) {
            throw new RuntimeException("Ce login existe déjà");
        }
        
        String motDePasseHash = authentificationService.hasherMotDePasse(motDePasse);
        DemandeurEmploi demandeur = new DemandeurEmploi(login, motDePasseHash, nom, prenom,
                                                        adresse, telephone, fax, diplome,
                                                        experience, salaireSouhaite);
        return demandeurEmploiRepository.save(demandeur);
    }
    
    public DemandeurEmploi getDemandeurEmploi(Long id) {
        return demandeurEmploiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Demandeur d'emploi introuvable"));
    }
}

