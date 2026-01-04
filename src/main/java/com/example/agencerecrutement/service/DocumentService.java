package com.example.agencerecrutement.service;

import com.example.agencerecrutement.model.Document;
import com.example.agencerecrutement.model.DemandeurEmploi;
import com.example.agencerecrutement.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;
    
    @Value("${app.max.file.size:10485760}") // 10MB par défaut
    private long maxFileSize;
    
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    public Document uploadDocument(MultipartFile file, DemandeurEmploi demandeur, String typeDocument) throws IOException {
        // Validation du fichier
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        
        if (!isAllowedFileType(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls les fichiers PNG sont acceptés");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("La taille du fichier dépasse la limite maximale de " + (maxFileSize / 1024 / 1024) + " MB");
        }
        
        // Créer le répertoire d'upload s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath);
        
        // Créer et sauvegarder l'entité Document
        Document document = new Document();
        document.setNomFichier(originalFilename);
        document.setTypeDocument(typeDocument);
        document.setTaille(file.getSize());
        document.setTypeMime(file.getContentType());
        document.setCheminStockage(filePath.toString());
        document.setDateUpload(LocalDateTime.now());
        document.setDemandeur(demandeur);
        document.setValide(true); // Validation automatique - plus besoin d'approbation
        
        return documentRepository.save(document);
    }
    
    public byte[] downloadDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé"));
        
        Path filePath = Paths.get(document.getCheminStockage());
        if (!Files.exists(filePath)) {
            throw new IOException("Fichier non trouvé sur le serveur");
        }
        
        return Files.readAllBytes(filePath);
    }
    
    public void validerDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé"));
        
        document.setValide(true);
        documentRepository.save(document);
    }
    
    public void rejeterDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé"));
        
        // Supprimer le fichier physique
        try {
            Files.deleteIfExists(Paths.get(document.getCheminStockage()));
        } catch (IOException e) {
            // Logger l'erreur mais continuer la suppression de la base de données
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
        }
        
        // Supprimer de la base de données
        documentRepository.delete(document);
    }
    
    public List<Document> getDocumentsEnAttente() {
        return documentRepository.findByValideFalse();
    }
    
    public List<Document> getDocumentsEnAttenteParType(String typeDocument) {
        return documentRepository.findDocumentsEnAttenteParType(typeDocument);
    }
    
    public List<Document> getDocumentsValides() {
        return documentRepository.findByValideTrue();
    }
    
    public List<Document> getDocumentsParDemandeur(Long demandeurId) {
        return documentRepository.findByDemandeurIdUtilisateur(demandeurId);
    }
    
    public List<Document> getDocumentsValidesParDemandeur(Long demandeurId) {
        return documentRepository.findByDemandeurIdUtilisateurAndValide(demandeurId, true);
    }
    
    public Document getCvValideParDemandeur(Long demandeurId) {
        return documentRepository.findDocumentValideParType(demandeurId, "CV");
    }
    
    public long getNombreDocumentsEnAttente() {
        return documentRepository.countDocumentsEnAttente();
    }
    
    public File getDocumentFile(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé"));
        
        Path filePath = Paths.get(document.getCheminStockage());
        if (!Files.exists(filePath)) {
            throw new IOException("Fichier non trouvé sur le serveur");
        }
        
        return filePath.toFile();
    }
    
    public List<Document> getCandidatsAvecCVValides() {
        return documentRepository.findByTypeDocumentAndValide("CV", true);
    }
    
    private boolean isAllowedFileType(String contentType) {
        return contentType != null && (
                contentType.equals("image/png")
        );
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    private String generateUniqueFilename(String originalFilename, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "doc_" + timestamp + "_" + uuid + extension;
    }
}
