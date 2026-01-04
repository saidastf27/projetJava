package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByValideFalse();
    
    List<Document> findByValideTrue();
    
    List<Document> findByDemandeurIdUtilisateur(Long demandeurId);
    
    List<Document> findByDemandeurIdUtilisateurAndValide(Long demandeurId, boolean valide);
    
    List<Document> findByTypeDocument(String typeDocument);
    
    List<Document> findByTypeDocumentAndValide(String typeDocument, boolean valide);
    
    @Query("SELECT d FROM Document d WHERE d.valide = false AND d.typeDocument = :typeDocument")
    List<Document> findDocumentsEnAttenteParType(@Param("typeDocument") String typeDocument);
    
    @Query("SELECT COUNT(d) FROM Document d WHERE d.valide = false")
    long countDocumentsEnAttente();
    
    @Query("SELECT d FROM Document d WHERE d.demandeur.idUtilisateur = :demandeurId AND d.typeDocument = :typeDocument AND d.valide = true")
    Document findDocumentValideParType(@Param("demandeurId") Long demandeurId, @Param("typeDocument") String typeDocument);
}
