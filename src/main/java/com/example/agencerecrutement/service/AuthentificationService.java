package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Utilisateur;
import com.example.agencerecrutement.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthentificationService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public Utilisateur authentifier(String login, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByLogin(login);
        
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Login ou mot de passe incorrect");
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        if (!utilisateur.getActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        // Temporairement accepter les mots de passe en clair pour le débogage
        if (!motDePasse.equals(utilisateur.getMotDePasse()) && !passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Login ou mot de passe incorrect");
        }
        
        return utilisateur;
    }
    
    public String hasherMotDePasse(String motDePasse) {
        return passwordEncoder.encode(motDePasse);
    }
    
    public boolean verifierMotDePasse(String motDePasse, String hash) {
        return passwordEncoder.matches(motDePasse, hash);
    }
    
    @Transactional
    public void modifierMotDePasse(Long idUtilisateur, String nouveauMotDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(idUtilisateur);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        String hashPassword = passwordEncoder.encode(nouveauMotDePasse);
        utilisateur.setMotDePasse(hashPassword);
        utilisateurRepository.save(utilisateur);
    }
}


