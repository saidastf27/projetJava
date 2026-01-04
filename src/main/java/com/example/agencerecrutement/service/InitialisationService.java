package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Administrateur;
import com.example.agencerecrutement.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialisationService implements CommandLineRunner {
    
    private final UtilisateurRepository utilisateurRepository;
    private final AuthentificationService authentificationService;
    
    @Override
    public void run(String... args) throws Exception {
        // Créer un administrateur par défaut s'il n'existe pas
        if (utilisateurRepository.findByLogin("admin").isEmpty()) {
            String motDePasseHash = authentificationService.hasherMotDePasse("admin");
            Administrateur admin = new Administrateur("admin", motDePasseHash);
            utilisateurRepository.save(admin);
            System.out.println("Administrateur par défaut créé : login=admin, password=admin");
        }
    }
}


