package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Categorie;
import com.example.agencerecrutement.model.Edition;
import com.example.agencerecrutement.model.Journal;
import com.example.agencerecrutement.repository.CategorieRepository;
import com.example.agencerecrutement.repository.EditionRepository;
import com.example.agencerecrutement.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {
    
    private final JournalRepository journalRepository;
    private final CategorieRepository categorieRepository;
    private final EditionRepository editionRepository;
    
    @Transactional
    public Categorie creerCategorie(String libelle) {
        if (categorieRepository.findByLibelle(libelle).isPresent()) {
            throw new RuntimeException("Cette catégorie existe déjà");
        }
        Categorie categorie = new Categorie();
        categorie.setLibelle(libelle);
        return categorieRepository.save(categorie);
    }
    
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }
    
    @Transactional
    public Journal creerJournal(String codeJournal, String nom, Journal.Periodicite periodicite,
                               String langue, Long idCategorie) {
        if (journalRepository.existsById(codeJournal)) {
            throw new RuntimeException("Ce code journal existe déjà");
        }
        
        Categorie categorie = categorieRepository.findById(idCategorie)
            .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        Journal journal = new Journal();
        journal.setCodeJournal(codeJournal);
        journal.setNom(nom);
        journal.setPeriodicite(periodicite);
        journal.setLangue(langue);
        journal.setCategorie(categorie);
        
        return journalRepository.save(journal);
    }
    
    public List<Journal> getAllJournaux() {
        return journalRepository.findAll();
    }
    
    public List<Journal> getJournauxByCategorie(Long idCategorie) {
        return journalRepository.findByCategorieIdCategorie(idCategorie);
    }
    
    @Transactional
    public Edition creerEdition(String codeJournal, Integer numeroEdition, LocalDate dateParution) {
        Journal journal = journalRepository.findById(codeJournal)
            .orElseThrow(() -> new RuntimeException("Journal introuvable"));
        
        Edition edition = new Edition();
        edition.setJournal(journal);
        edition.setNumeroEdition(numeroEdition);
        edition.setDateParution(dateParution);
        
        return editionRepository.save(edition);
    }
    
    public List<Edition> getEditionsByJournal(String codeJournal) {
        return editionRepository.findByJournalCodeJournal(codeJournal);
    }
    
    public Journal getJournal(String codeJournal) {
        return journalRepository.findById(codeJournal)
            .orElseThrow(() -> new RuntimeException("Journal introuvable"));
    }
    
    public Edition getEdition(Long idEdition) {
        return editionRepository.findById(idEdition)
            .orElseThrow(() -> new RuntimeException("Édition introuvable"));
    }
}


