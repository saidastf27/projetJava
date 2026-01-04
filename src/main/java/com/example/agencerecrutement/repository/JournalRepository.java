package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, String> {

    Optional<Journal> findByCodeJournal(String codeJournal);
    
    List<Journal> findByCategorieIdCategorie(Long idCategorie);
}


