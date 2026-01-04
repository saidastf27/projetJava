package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Edition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EditionRepository extends JpaRepository<Edition, Long> {
    List<Edition> findByJournalCodeJournal(String codeJournal);
}


