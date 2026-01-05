package com.example.agencerecrutement.javafx.controllers;

import com.example.agencerecrutement.javafx.dialogs.*;
import com.example.agencerecrutement.model.*;
import com.example.agencerecrutement.repository.*;
import com.example.agencerecrutement.service.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

// Imports pour la génération d'images
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

@Component
public class MainController {
    
    private final EntrepriseService entrepriseService;
    private final OffreService offreService;
    private final CandidatureService candidatureService;
    private final RecrutementService recrutementService;
    private final JournalService journalService;
    private final DemandeurEmploiService demandeurEmploiService;
    private final DocumentService documentService;
    private final UtilisateurRepository utilisateurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DemandeurEmploiRepository demandeurEmploiRepository;
    private final JournalRepository journalRepository;
    private final OffreRepository offreRepository;
    private final CandidatureRepository candidatureRepository;
    private final RecrutementRepository recrutementRepository;
    private final AbonnementRepository abonnementRepository;
    private final PublicationOffreRepository publicationOffreRepository;
    private final DocumentRepository documentRepository;
    private final AuthentificationService authentificationService;
    
    private ConfigurableApplicationContext applicationContext;
    private Utilisateur utilisateurConnecte;
    private BorderPane root;
    
    public MainController(EntrepriseService entrepriseService, OffreService offreService,
                         CandidatureService candidatureService, RecrutementService recrutementService,
                         JournalService journalService, DemandeurEmploiService demandeurEmploiService,
                         DocumentService documentService, UtilisateurRepository utilisateurRepository, 
                         EntrepriseRepository entrepriseRepository, DemandeurEmploiRepository demandeurEmploiRepository, 
                         JournalRepository journalRepository, OffreRepository offreRepository, 
                         CandidatureRepository candidatureRepository, RecrutementRepository recrutementRepository, 
                         AbonnementRepository abonnementRepository, PublicationOffreRepository publicationOffreRepository, 
                         DocumentRepository documentRepository, AuthentificationService authentificationService) {
        this.entrepriseService = entrepriseService;
        this.offreService = offreService;
        this.candidatureService = candidatureService;
        this.recrutementService = recrutementService;
        this.journalService = journalService;
        this.demandeurEmploiService = demandeurEmploiService;
        this.documentService = documentService;
        this.utilisateurRepository = utilisateurRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.demandeurEmploiRepository = demandeurEmploiRepository;
        this.journalRepository = journalRepository;
        this.offreRepository = offreRepository;
        this.candidatureRepository = candidatureRepository;
        this.recrutementRepository = recrutementRepository;
        this.abonnementRepository = abonnementRepository;
        this.publicationOffreRepository = publicationOffreRepository;
        this.documentRepository = documentRepository;
        this.authentificationService = authentificationService;
        initializeView();
    }
    
    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        updateView();
    }
    
    private void initializeView() {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        attachStylesheets(root);
    }

  
    private void attachStylesheets(Parent parent) {
        try {
            String css = getClass().getResource("/styles/MainController.css").toExternalForm();
            if (!parent.getStylesheets().contains(css)) {
                parent.getStylesheets().add(css);
            }
        } catch (Exception ignored) {
            // Si le CSS n'est pas trouvé, l'UI fonctionne quand même (sans styles)
        }
    }

    
  private void updateView() {
    if (utilisateurConnecte == null) return;

    root = new BorderPane();
    root.setPadding(new Insets(10));
    attachStylesheets(root);

    //  Créer la barre du haut (MenuBar + bouton logout à droite)
    javafx.scene.Node topBar = createMenuBar();
    root.setTop(topBar);

    // Créer le contenu principal selon le rôle
    Pane content = createContentPane();
    root.setCenter(content);
}

    
    private void updateViewAndScene() {
        updateView();
        // Mettre à jour la scène avec le nouveau root
        Stage stage = null;
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window instanceof Stage && window.isShowing()) {
                stage = (Stage) window;
                break;
            }
        }
        if (stage != null && stage.getScene() != null) {
            Scene scene = stage.getScene();
            scene.setRoot(root);
        }
    }
    
private javafx.scene.Node createMenuBar() {

    // --- Menus à gauche ---
    MenuBar menuBar = new MenuBar();


    // Ajouter des menus selon le rôle
    if (utilisateurConnecte.getRole() == Utilisateur.Role.ADMINISTRATEUR) {

        Menu menuAdmin = new Menu("Administration");
        menuAdmin.getStyleClass().add("menu-button"); 

        MenuItem itemGestionUsers = new MenuItem("Gérer les utilisateurs");
        MenuItem itemGestionJournaux = new MenuItem("Gérer les journaux");
        MenuItem itemStats = new MenuItem("Statistiques");
        MenuItem itemRapports = new MenuItem("Rapports de recrutement");

        itemGestionUsers.setOnAction(e -> showGestionUtilisateurs());
        itemGestionJournaux.setOnAction(e -> showGestionJournaux());
        itemStats.setOnAction(e -> showStatistiques());
        itemRapports.setOnAction(e -> showRapportsRecrutement());

        menuAdmin.getItems().addAll(itemGestionUsers, itemGestionJournaux, itemStats, itemRapports);
        menuBar.getMenus().add(menuAdmin);

    } else if (utilisateurConnecte.getRole() == Utilisateur.Role.DEMANDEUR_EMPLOI) {

        Menu menuDemandeur = new Menu("Mes Documents");
        menuDemandeur.getStyleClass().add("menu-button"); 

        MenuItem itemMesDocuments = new MenuItem("Gérer mes documents");
        MenuItem itemUploadCV = new MenuItem("Téléverser un CV");

        itemMesDocuments.setOnAction(e -> showMesDocuments());
        itemUploadCV.setOnAction(e -> handleUploadCV());

        menuDemandeur.getItems().addAll(itemMesDocuments, itemUploadCV);
        menuBar.getMenus().add(menuDemandeur);

    } else if (utilisateurConnecte.getRole() == Utilisateur.Role.ENTREPRISE) {

        Menu menuEntreprise = new Menu("Recrutement");
        menuEntreprise.getStyleClass().add("menu-button"); 

        MenuItem itemCandidats = new MenuItem("Voir les candidats");
        MenuItem itemMesRecrutements = new MenuItem("Mes recrutements");

        itemCandidats.setOnAction(e -> showCandidatsPourRecrutement());
        itemMesRecrutements.setOnAction(e -> showMesRecrutements());

        menuEntreprise.getItems().addAll(itemCandidats, itemMesRecrutements);
        menuBar.getMenus().add(menuEntreprise);
    }

    Button logoutButton = new Button("Déconnexion");
    logoutButton.getStyleClass().addAll("mc-style-10", "logout-danger"); 
    logoutButton.setOnAction(e -> {
        Stage stage = null;
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window instanceof Stage && window.isShowing()) {
                stage = (Stage) window;
                break;
            }
        }
        handleDeconnexion(stage);
    });

    // --- Spacer qui pousse le bouton à droite ---
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // --- Navbar finale ---
    HBox navbar = new HBox(menuBar, spacer, logoutButton);
    navbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    navbar.setSpacing(10);
    navbar.setPadding(new Insets(6, 14, 6, 14));
    navbar.getStyleClass().add("mc-navbar"); 

    // Bonus : MenuBar prend tout l'espace à gauche
    HBox.setHgrow(menuBar, Priority.NEVER);

    return navbar;
}


    


private Pane createContentPane() {
    VBox content = new VBox(20);
    content.setPadding(new Insets(20));

    // Header avec bienvenue
    HBox headerBox = new HBox();
    headerBox.setSpacing(20);
    headerBox.setAlignment(javafx.geometry.Pos.CENTER);
    headerBox.getStyleClass().add("mc-style-12");

    Label welcomeLabel = new Label("Bienvenue, " + utilisateurConnecte.getLogin());
    welcomeLabel.getStyleClass().add("welcome-title");

    headerBox.getChildren().add(welcomeLabel);
    content.getChildren().add(headerBox);

    // Barre de recherche globale
    HBox searchBox = new HBox(10);
    searchBox.setPadding(new Insets(10, 0, 0, 0));
    Label searchLabel = new Label("Recherche :");
    searchLabel.getStyleClass().add("search-label");


    TextField searchField = new TextField();
    searchField.setPromptText("Tapez un mot-clé (titre d'offre, nom de journal, numéro d'édition...)");
    searchField.setPrefWidth(400);

    ComboBox<String> typeCombo = new ComboBox<>();
    typeCombo.getItems().addAll("Offres", "Journaux", "Éditions");
    typeCombo.setValue("Offres");

    Button searchButton = new Button("Rechercher");
    searchButton.setOnAction(e -> showSearchDialog(searchField.getText().trim(), typeCombo.getValue()));

    searchBox.getChildren().addAll(searchLabel, searchField, typeCombo, searchButton);
    content.getChildren().add(searchBox);

    // Contenu spécifique selon le rôle
    if (utilisateurConnecte.getRole() == Utilisateur.Role.ENTREPRISE) {
        content.getChildren().add(createEntreprisePane());
    } else if (utilisateurConnecte.getRole() == Utilisateur.Role.DEMANDEUR_EMPLOI) {
        content.getChildren().add(createDemandeurPane());
    } else if (utilisateurConnecte.getRole() == Utilisateur.Role.ADMINISTRATEUR) {
        content.getChildren().add(createAdminPane());
    }

    return content;
}

    
    private Pane createEntreprisePane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        
        Label title = new Label("Espace Entreprise");
        title.getStyleClass().add("page-title");
        
        Button btnEditCoordonnees = new Button("Modifier mes coordonnées");
        btnEditCoordonnees.setOnAction(e -> showEditEntrepriseCoordonneesDialog());
        
        TabPane tabPane = new TabPane();
        
        Tab tabOffres = new Tab("Mes Offres");
        tabOffres.setContent(createOffresTab());
        tabOffres.setClosable(false);
        
        Tab tabAbonnements = new Tab("Mes Abonnements");
        tabAbonnements.setContent(createAbonnementsTab());
        tabAbonnements.setClosable(false);
        
        Tab tabCandidatures = new Tab("Candidatures");
        tabCandidatures.setContent(createCandidaturesTab());
        tabCandidatures.setClosable(false);
        
        Tab tabRecrutements = new Tab("Recrutements");
        tabRecrutements.setContent(createRecrutementsTab());
        tabRecrutements.setClosable(false);
        
        tabPane.getTabs().addAll(tabOffres, tabAbonnements, tabCandidatures, tabRecrutements);
        
        pane.getChildren().addAll(title, btnEditCoordonnees, tabPane);
        return pane;
    }
    
    private Pane createDemandeurPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        
        Label title = new Label("Espace Demandeur d'Emploi");
        title.getStyleClass().add("page-title");

        
        Button btnEditCoordonnees = new Button("Modifier mes coordonnées");
        btnEditCoordonnees.setOnAction(e -> showEditDemandeurCoordonneesDialog());
        
        TabPane tabPane = new TabPane();
        
        Tab tabOffres = new Tab("Offres disponibles");
        tabOffres.setContent(createOffresDisponiblesTab());
        tabOffres.setClosable(false);
        
        Tab tabMesCandidatures = new Tab("Mes candidatures");
        tabMesCandidatures.setContent(createMesCandidaturesTab());
        tabMesCandidatures.setClosable(false);

        Tab tabJournaux = new Tab("Journaux & éditions");
        tabJournaux.setContent(createJournauxPourDemandeurPane());
        tabJournaux.setClosable(false);
        
        tabPane.getTabs().addAll(tabOffres, tabMesCandidatures, tabJournaux);
        
        pane.getChildren().addAll(title, btnEditCoordonnees, tabPane);
        return pane;
    }
    
    private Pane createAdminPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        
        Label title = new Label("Espace Administrateur");
        title.getStyleClass().add("page-title");
        
        // Onglets ou boutons pour les différentes fonctions admin
        Button btnGestionUsers = new Button("Gérer les utilisateurs");
        btnGestionUsers.setOnAction(e -> showGestionUtilisateurs());
        
        Button btnGestionJournaux = new Button("Gérer les journaux");
        btnGestionJournaux.setOnAction(e -> showGestionJournaux());
        
        Button btnGestionOffres = new Button("Gérer les offres");
        btnGestionOffres.setOnAction(e -> showGestionOffresAdmin());
        
        Button btnStats = new Button("Statistiques");
        btnStats.setOnAction(e -> showStatistiques());
        
        Button btnRapports = new Button("Rapports de recrutement");
        btnRapports.setOnAction(e -> showRapportsRecrutement());
        
        HBox buttons = new HBox(10, btnGestionUsers, btnGestionJournaux, btnGestionOffres, btnStats, btnRapports);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        
        pane.getChildren().addAll(title, buttons);
        
        return pane;
    }
    //pour l'entreprise
    
  private Pane createOffresTab() {
    VBox pane = new VBox(10);

    Button btnNouvelleOffre = new Button("Nouvelle offre");
    btnNouvelleOffre.getStyleClass().add("btn-secondary");
    btnNouvelleOffre.setOnAction(e -> showNouvelleOffreDialog());

    Button btnPublierOffre = new Button("Publier l'offre sélectionnée");
    btnPublierOffre.getStyleClass().add("btn-secondary-strong");

    // TableView pour afficher les offres
    TableView<Offre> tableView = new TableView<>();

    TableColumn<Offre, String> titreCol = new TableColumn<>("Titre");
    titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));

    TableColumn<Offre, Integer> expCol = new TableColumn<>("Expérience requise");
    expCol.setCellValueFactory(new PropertyValueFactory<>("experienceRequise"));

    TableColumn<Offre, Integer> postesCol = new TableColumn<>("Postes disponibles");
    postesCol.setCellValueFactory(param ->
        new javafx.beans.property.SimpleIntegerProperty(param.getValue().getNbPostesDisponibles()).asObject()
    );

    TableColumn<Offre, String> etatCol = new TableColumn<>("État");
    etatCol.setCellValueFactory(param ->
        new javafx.beans.property.SimpleStringProperty(
            param.getValue().estActive() ? "Active" : "Désactivée"
        )
    );

    tableView.getColumns().addAll(titreCol, expCol, postesCol, etatCol);

    // ✅ la table prend tout l'espace vertical dispo
    VBox.setVgrow(tableView, Priority.ALWAYS);

    try {
        Entreprise entreprise = (Entreprise) utilisateurConnecte;
        tableView.getItems().addAll(offreService.getOffresByEntreprise(entreprise.getIdUtilisateur()));
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    // Action de publication
    btnPublierOffre.setOnAction(e -> {
        Offre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection", "Veuillez sélectionner une offre à publier.");
            return;
        }
        if (!selected.estActive()) {
            showWarning("Offre désactivée", "Vous ne pouvez publier qu'une offre active.");
            return;
        }
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            PublierOffreDialog dialog = new PublierOffreDialog(
                offreService,
                entrepriseService,
                journalService,
                entreprise.getIdUtilisateur(),
                selected
            );
            java.util.Optional<PublicationOffre> result = dialog.showAndWait();
            result.ifPresent(pub -> showInfo("Succès", "Offre publiée avec succès dans le journal."));
        } catch (Exception ex) {
            showError("Erreur lors de la publication", ex.getMessage());
        }
    });

    // ✅ Boutons à droite (spacer)
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox buttons = new HBox(12, spacer, btnNouvelleOffre, btnPublierOffre);
    buttons.setAlignment(Pos.CENTER_LEFT);

    pane.getChildren().addAll(buttons, tableView);
    return pane;
}

    
private Pane createAbonnementsTab() {
    VBox pane = new VBox(10);

    Button btnNouvelAbonnement = new Button("Souscrire un abonnement");
    btnNouvelAbonnement.getStyleClass().add("btn-secondary");
    btnNouvelAbonnement.setOnAction(e -> showNouvelAbonnementDialog());

    TableView<Abonnement> tableView = new TableView<>();

    TableColumn<Abonnement, String> journalCol = new TableColumn<>("Journal");
    journalCol.setCellValueFactory(param ->
        new javafx.beans.property.SimpleStringProperty(
            param.getValue().getJournal().getNom()
        )
    );

    TableColumn<Abonnement, String> etatCol = new TableColumn<>("État");
    etatCol.setCellValueFactory(param ->
        new javafx.beans.property.SimpleStringProperty(
            param.getValue().getEtat().toString()
        )
    );

    TableColumn<Abonnement, String> expirationCol = new TableColumn<>("Date expiration");
    expirationCol.setCellValueFactory(param ->
        new javafx.beans.property.SimpleStringProperty(
            param.getValue().getDateExpiration().toString()
        )
    );

    tableView.getColumns().addAll(journalCol, etatCol, expirationCol);

    // ✅ la table prend l'espace vertical disponible
    VBox.setVgrow(tableView, Priority.ALWAYS);

    try {
        Entreprise entreprise = (Entreprise) utilisateurConnecte;
        tableView.getItems().addAll(
            entrepriseService.getAbonnements(entreprise.getIdUtilisateur())
        );
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    // ✅ Bouton aligné à droite (spacer)
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox buttons = new HBox(12, spacer, btnNouvelAbonnement);
    buttons.setAlignment(Pos.CENTER_LEFT);

    pane.getChildren().addAll(buttons, tableView);
    return pane;
}

    
    private Pane createCandidaturesTab() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));
        
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            
            // TableView pour afficher les candidatures
            TableView<Candidature> tableView = new TableView<>();
            
            TableColumn<Candidature, String> demandeurCol = new TableColumn<>("Demandeur");
            demandeurCol.setCellValueFactory(param -> {
                DemandeurEmploi d = param.getValue().getDemandeur();
                return new javafx.beans.property.SimpleStringProperty(d.getNom() + " " + d.getPrenom());
            });
            
            TableColumn<Candidature, String> offreCol = new TableColumn<>("Offre");
            offreCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleStringProperty(param.getValue().getOffre().getTitre()));
            
            TableColumn<Candidature, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleStringProperty(param.getValue().getDateCandidature().toString()));
            
            TableColumn<Candidature, Integer> expCol = new TableColumn<>("Expérience");
            expCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleIntegerProperty(param.getValue().getDemandeur().getExperience()).asObject());
            
            // Colonne CV avec boutons pour voir et télécharger
            TableColumn<Candidature, Void> cvCol = new TableColumn<>("CV");
            cvCol.setCellFactory(param -> new TableCell<>() {
                private final Button btnTelechargerCV = new Button("Télécharger CV");
                private final HBox cvBox = new HBox(5, btnTelechargerCV);
                
                {
                    btnTelechargerCV.getStyleClass().add("mc-style-05");
                    
                    btnTelechargerCV.setOnAction(event -> {
                        Candidature candidature = getTableView().getItems().get(getIndex());
                        DemandeurEmploi demandeur = candidature.getDemandeur();
                        telechargerCVDemandeur(demandeur);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(cvBox);
                    }
                }
            });
            cvCol.setPrefWidth(200);
            
            tableView.getColumns().addAll(demandeurCol, offreCol, dateCol, expCol, cvCol);
            
            // Charger les candidatures de toutes les offres de l'entreprise
            offreService.getOffresByEntreprise(entreprise.getIdUtilisateur())
                .forEach(offre -> tableView.getItems().addAll(candidatureService.getCandidaturesByOffre(offre.getIdOffre())));
            
            // Bouton pour recruter
            Button btnRecruter = new Button("Recruter le candidat sélectionné");
             btnRecruter.getStyleClass().add("btn-secondary");

            btnRecruter.setOnAction(e -> {
                Candidature selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    handleRecrutement(selected);
                    tableView.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sélection");
                    alert.setHeaderText("Aucune candidature sélectionnée");
                    alert.setContentText("Veuillez sélectionner une candidature pour recruter");
                    alert.showAndWait();
                }
            });
            
            pane.getChildren().addAll(new Label("Candidatures reçues :"), tableView, btnRecruter);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            pane.getChildren().add(new Label("Erreur : " + ex.getMessage()));
        }
        
        return pane;
    }
    
    private Pane createRecrutementsTab() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));
        
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            
            TableView<Recrutement> tableView = new TableView<>();
            
            TableColumn<Recrutement, String> demandeurCol = new TableColumn<>("Demandeur");
            demandeurCol.setCellValueFactory(param -> {
                DemandeurEmploi d = param.getValue().getDemandeur();
                return new javafx.beans.property.SimpleStringProperty(d.getNom() + " " + d.getPrenom());
            });
            
            TableColumn<Recrutement, String> offreCol = new TableColumn<>("Offre");
            offreCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleStringProperty(param.getValue().getOffre().getTitre()));
            
            TableColumn<Recrutement, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleStringProperty(param.getValue().getDateRecrutement().toString()));
            
            tableView.getColumns().addAll(demandeurCol, offreCol, dateCol);
            
            tableView.getItems().addAll(recrutementService.getRecrutementsByEntreprise(entreprise.getIdUtilisateur()));
            
            pane.getChildren().addAll(new Label("Historique des recrutements :"), tableView);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            pane.getChildren().add(new Label("Erreur : " + ex.getMessage()));
        }
        
        return pane;
    }
    
    private Pane createOffresDisponiblesTab() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));
        
        try {
            DemandeurEmploi demandeur = (DemandeurEmploi) utilisateurConnecte;
            
            TableView<Offre> tableView = new TableView<>();
            
            TableColumn<Offre, String> titreCol = new TableColumn<>("Titre");
            titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
            
            TableColumn<Offre, String> competencesCol = new TableColumn<>("Compétences");
            competencesCol.setCellValueFactory(new PropertyValueFactory<>("competences"));
            
            TableColumn<Offre, Integer> expCol = new TableColumn<>("Expérience requise");
            expCol.setCellValueFactory(new PropertyValueFactory<>("experienceRequise"));
            
            TableColumn<Offre, Integer> postesCol = new TableColumn<>("Postes disponibles");
            postesCol.setCellValueFactory(param -> 
                new javafx.beans.property.SimpleIntegerProperty(param.getValue().getNbPostesDisponibles()).asObject());
            
            tableView.getColumns().addAll(titreCol, competencesCol, expCol, postesCol);
            
            tableView.getItems().addAll(offreService.getOffresActives());
            
            Button btnPostuler = new Button("Postuler");
            btnPostuler.getStyleClass().add("btn-secondary");
            btnPostuler.setOnAction(e -> {
                Offre selected = tableView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sélection");
                    alert.setHeaderText("Aucune offre sélectionnée");
                    alert.setContentText("Veuillez sélectionner une offre pour postuler.");
                    alert.showAndWait();
                    return;
                }

                // Nouveau flux métier : la candidature se fait depuis l'onglet
                // \"Journaux & éditions\" afin que l'édition soit cohérente.
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Postuler à une offre");
                info.setHeaderText("Utilisez l'onglet \"Journaux & éditions\"");
                info.setContentText(
                    "Pour postuler à une offre, veuillez utiliser l'onglet \"Journaux & éditions\".\n\n" +
                    "Sélectionnez le journal puis l'édition où l'offre a été publiée,\n" +
                    "puis choisissez l'offre et cliquez sur \"Postuler à l'offre sélectionnée\"."
                );
                info.showAndWait();
            });
            
            pane.getChildren().addAll(new Label("Offres disponibles :"), tableView, btnPostuler);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            pane.getChildren().add(new Label("Erreur : " + ex.getMessage()));
        }
        
        return pane;
    }
    
  private Pane createMesCandidaturesTab() {

    // ===== Contenu réel =====
    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    content.setFillWidth(true);

    try {
        DemandeurEmploi demandeur = (DemandeurEmploi) utilisateurConnecte;

        // ======================
        // Statistiques
        // ======================
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(10));
        statsBox.getStyleClass().add("mc-style-15");

        java.util.List<Candidature> toutesCandidatures =
                candidatureService.getCandidaturesByDemandeur(demandeur.getIdUtilisateur());

        long enAttente = toutesCandidatures.stream()
                .filter(c -> c.getStatut() == Candidature.StatutCandidature.EN_ATTENTE).count();
        long approuvees = toutesCandidatures.stream()
                .filter(c -> c.getStatut() == Candidature.StatutCandidature.APPROUVEE).count();
        long rejetees = toutesCandidatures.stream()
                .filter(c -> c.getStatut() == Candidature.StatutCandidature.REJETEE).count();
        long recrutees = toutesCandidatures.stream()
                .filter(c -> c.getStatut() == Candidature.StatutCandidature.RECRUTEE).count();

        VBox statsLeft = new VBox(10,
                createStatCard("En attente", String.valueOf(enAttente), "#f39c12"),
                createStatCard("Approuvées", String.valueOf(approuvees), "#28a745")
        );

        VBox statsRight = new VBox(10,
                createStatCard("Rejetées", String.valueOf(rejetees), "#dc3545"),
                createStatCard("Recrutées", String.valueOf(recrutees), "#17a2b8")
        );

        statsBox.getChildren().addAll(statsLeft, statsRight);

        // ======================
        // Titre
        // ======================
        Label titleLabel = new Label("Détail de mes candidatures :");
        titleLabel.getStyleClass().add("mc-style-21");

        // ======================
        // TableView
        // ======================
        TableView<Candidature> tableView = new TableView<>();
        tableView.setPrefHeight(350);

        TableColumn<Candidature, String> offreCol = new TableColumn<>("Offre");
        offreCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getOffre().getTitre()
                ));
        offreCol.setPrefWidth(250);

        TableColumn<Candidature, String> entrepriseCol = new TableColumn<>("Entreprise");
        entrepriseCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getOffre().getEntreprise().getRaisonSociale()
                ));
        entrepriseCol.setPrefWidth(180);

        TableColumn<Candidature, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getDateCandidature().toString()
                ));
        dateCol.setPrefWidth(120);

        TableColumn<Candidature, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        getStatutCandidatureDisplay(param.getValue().getStatut())
                ));
        statutCol.setPrefWidth(120);

        statutCol.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Candidature.StatutCandidature statut =
                            getTableView().getItems().get(getIndex()).getStatut();
                    setStyle(getStatutStyle(statut));
                }
            }
        });

        TableColumn<Candidature, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);
        actionsCol.setCellFactory(param -> new TableCell<Candidature, Void>() {
            private final Button btnDetails = new Button("Détails");

            {
                btnDetails.getStyleClass().add("mc-style-01");
                btnDetails.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    showDetailsCandidature(candidature);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDetails);
            }
        });

        tableView.getColumns().addAll(offreCol, entrepriseCol, dateCol, statutCol, actionsCol);

        java.util.List<Candidature> candidatures =
                candidatureService.getCandidaturesWithRelations(demandeur.getIdUtilisateur());
        tableView.getItems().addAll(candidatures);

        // ======================
        // Notifications
        // ======================
        java.util.List<Candidature> nouvellesApprouvees = candidatures.stream()
                .filter(c -> c.getStatut() == Candidature.StatutCandidature.APPROUVEE)
                .filter(c -> !c.isNotifiee())
                .collect(java.util.stream.Collectors.toList());

        if (!nouvellesApprouvees.isEmpty()) {
            HBox notificationBox = new HBox(10);
            notificationBox.getStyleClass().add("mc-style-09");
            notificationBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label icon = new Label("🎉");
            icon.getStyleClass().add("mc-style-20");

            Label text = new Label("Félicitations ! Vous avez " + nouvellesApprouvees.size() + " candidature(s) approuvée(s).");
            text.getStyleClass().add("mc-style-29");

            Button btnMarquerLues = new Button("Marquer comme lues");
            btnMarquerLues.getStyleClass().add("mc-style-02");
            btnMarquerLues.setOnAction(e -> marquerCandidaturesCommeLues(nouvellesApprouvees));

            notificationBox.getChildren().addAll(icon, text, btnMarquerLues);
            content.getChildren().add(notificationBox);
        }

        content.getChildren().addAll(statsBox, titleLabel, tableView);

    } catch (Exception ex) {
        ex.printStackTrace();
        content.getChildren().add(new Label("Erreur : " + ex.getMessage()));
    }

    // ======================
    // ScrollPane (mais wrapper Pane obligatoire)
    // ======================
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    // ✅ IMPORTANT: on retourne un Pane (StackPane) pour respecter la signature
    StackPane wrapper = new StackPane(scrollPane);
    wrapper.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

    return wrapper;
}


    /**
     * Onglet \"Journaux & éditions\" pour le demandeur :
     * Journal -> Éditions -> Offres publiées dans l'édition sélectionnée.
     */
    private Pane createJournauxPourDemandeurPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label journauxLabel = new Label("Journaux");
        TableView<Journal> journauxTable = new TableView<>();

        TableColumn<Journal, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeJournal"));
        codeCol.setPrefWidth(100);

        TableColumn<Journal, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(200);

        journauxTable.getColumns().addAll(codeCol, nomCol);
        journauxTable.getItems().addAll(journalRepository.findAll());

        Label editionsLabel = new Label("Éditions");
        TableView<Edition> editionsTable = new TableView<>();

        TableColumn<Edition, Long> idEditionCol = new TableColumn<>("ID");
        idEditionCol.setCellValueFactory(new PropertyValueFactory<>("idEdition"));
        idEditionCol.setPrefWidth(80);

        TableColumn<Edition, Integer> numEditionCol = new TableColumn<>("Numéro");
        numEditionCol.setCellValueFactory(new PropertyValueFactory<>("numeroEdition"));
        numEditionCol.setPrefWidth(100);

        TableColumn<Edition, String> dateParutionCol = new TableColumn<>("Date de parution");
        dateParutionCol.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(param.getValue().getDateParution().toString()));
        dateParutionCol.setPrefWidth(150);

        editionsTable.getColumns().addAll(idEditionCol, numEditionCol, dateParutionCol);

        Label offresLabel = new Label("Offres publiées dans l'édition sélectionnée");
        TableView<Offre> offresTable = new TableView<>();

        TableColumn<Offre, String> titreOffreCol = new TableColumn<>("Titre");
        titreOffreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        titreOffreCol.setPrefWidth(250);

        TableColumn<Offre, String> compOffreCol = new TableColumn<>("Compétences");
        compOffreCol.setCellValueFactory(new PropertyValueFactory<>("competences"));
        compOffreCol.setPrefWidth(250);

        offresTable.getColumns().addAll(titreOffreCol, compOffreCol);

        // Quand on sélectionne un journal, charger ses éditions
        journauxTable.getSelectionModel().selectedItemProperty().addListener((obs, oldJ, newJ) -> {
            editionsTable.getItems().clear();
            offresTable.getItems().clear();
            if (newJ != null) {
                editionsTable.getItems().addAll(journalService.getEditionsByJournal(newJ.getCodeJournal()));
            }
        });

        // Quand on sélectionne une édition, charger les offres publiées
        editionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldE, newE) -> {
            offresTable.getItems().clear();
            if (newE != null) {
                var pubs = publicationOffreRepository.findByEditionIdEdition(newE.getIdEdition());
                pubs.stream()
                    .map(PublicationOffre::getOffre)
                    .forEach(offresTable.getItems()::add);
            }
        });

        Button btnPostuler = new Button("Postuler à l'offre sélectionnée");
        btnPostuler.getStyleClass().add("btn-secondary");
        btnPostuler.setOnAction(e -> {
            Offre selectedOffre = offresTable.getSelectionModel().getSelectedItem();
            Edition selectedEdition = editionsTable.getSelectionModel().getSelectedItem();
            if (selectedOffre == null || selectedEdition == null) {
                showWarning("Sélection", "Veuillez sélectionner une édition et une offre.");
                return;
            }
            try {
                DemandeurEmploi demandeur = (DemandeurEmploi) utilisateurConnecte;
                candidatureService.postuler(
                    demandeur.getIdUtilisateur(),
                    selectedOffre.getIdOffre(),
                    selectedEdition.getIdEdition()
                );
                showInfo("Succès", "Votre candidature a été envoyée avec succès.");
                updateViewAndScene();
            } catch (Exception ex) {
                showError("Erreur lors de la candidature", ex.getMessage());
            }
        });

        root.getChildren().addAll(
            journauxLabel, journauxTable,
            editionsLabel, editionsTable,
            offresLabel, offresTable,
            btnPostuler
        );

        // Wrap the whole page in a global ScrollPane so the bottom button never gets clipped
        ScrollPane pageScroll = new ScrollPane(root);
        pageScroll.setFitToWidth(true);
        pageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pageScroll.setPannable(true);

        StackPane wrapper = new StackPane(pageScroll);
        wrapper.setPadding(Insets.EMPTY);
        return wrapper;
}
    
    private void showNouvelleOffreDialog() {
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            NouvelleOffreDialog dialog = new NouvelleOffreDialog(offreService, entreprise.getIdUtilisateur());
            
            java.util.Optional<Offre> result = dialog.showAndWait();
            if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Offre créée");
                alert.setContentText("L'offre a été créée avec succès");
                alert.showAndWait();
                
                // Rafraîchir complètement la vue pour que les nouvelles offres apparaissent
                updateViewAndScene();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la création");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    private void showNouvelAbonnementDialog() {
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            NouvelAbonnementDialog dialog = new NouvelAbonnementDialog(
                entrepriseService, journalService, entreprise.getIdUtilisateur());
            
            java.util.Optional<Abonnement> result = dialog.showAndWait();
            if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Abonnement souscrit");
                alert.setContentText("L'abonnement a été souscrit avec succès");
                alert.showAndWait();
                // Rafraîchir la vue
                updateViewAndScene();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la souscription");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    private void handlePostuler(Offre offre) {
        try {
            DemandeurEmploi demandeur = (DemandeurEmploi) utilisateurConnecte;
            CandidatureDialog dialog = new CandidatureDialog(
                candidatureService, journalService, demandeur.getIdUtilisateur(), offre);
            
            java.util.Optional<Candidature> result = dialog.showAndWait();
            if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Candidature envoyée");
                alert.setContentText("Votre candidature a été envoyée avec succès");
                alert.showAndWait();
                updateViewAndScene();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la candidature");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    private void handleRecrutement(Candidature candidature) {
        try {
            Entreprise entreprise = (Entreprise) utilisateurConnecte;
            DemandeurEmploi demandeur = candidature.getDemandeur();
            Offre offre = candidature.getOffre();
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Confirmer le recrutement");
            confirmAlert.setContentText("Voulez-vous recruter " + demandeur.getNom() + " " + demandeur.getPrenom() + 
                                       " pour l'offre \"" + offre.getTitre() + "\" ?");
            
            java.util.Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                recrutementService.recruter(entreprise.getIdUtilisateur(), offre.getIdOffre(), 
                                           demandeur.getIdUtilisateur());
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Recrutement effectué");
                successAlert.setContentText("Le candidat a été recruté avec succès");
                successAlert.showAndWait();
                updateViewAndScene();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du recrutement");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
   private void handleDeconnexion(Stage stage) {
    try {
        utilisateurConnecte = null;

        if (stage == null) {
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage && window.isShowing()) {
                    stage = (Stage) window;
                    break;
                }
            }
        }

        if (stage == null) {
            throw new IllegalStateException("Impossible de trouver la fenêtre principale");
        }

        // ✅ TOUJOURS créer un NOUVEAU LoginController
        LoginController loginController =
                applicationContext.getAutowireCapableBeanFactory()
                        .createBean(LoginController.class);

        loginController.setApplicationContext(applicationContext);

        Scene loginScene = new Scene(loginController.getView(), 00, 300);

        stage.setScene(loginScene);
        stage.setTitle("Agence de Recrutement - Connexion");
        stage.setMaximized(false);
        stage.centerOnScreen();

    } catch (Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText("Erreur de déconnexion");
        errorAlert.setContentText(
                "Une erreur est survenue lors de la déconnexion : " + e.getMessage()
        );
        errorAlert.showAndWait();
    }
}

    
    // Méthode de compatibilité pour les appels sans paramètre
    private void handleDeconnexion() {
        handleDeconnexion(null);
    }
    
    public BorderPane getView() {
        return root;
    }
    
    // Méthodes pour les actions d'administration
    /**
     * Gestion des offres côté administrateur.
     * L'admin peut :
     * - voir toutes les offres
     * - désactiver une offre
     * - supprimer une offre (seulement si aucune candidature / recrutement / publication)
     */
    private void showGestionOffresAdmin() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des offres");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
         attachStylesheets(root);
        
        Label title = new Label("Gestion des offres");
        title.getStyleClass().add("mc-style-22");
        
        TableView<Offre> tableView = new TableView<>();
        
        TableColumn<Offre, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idOffre"));
        idCol.setPrefWidth(70);
        
        TableColumn<Offre, String> titreCol = new TableColumn<>("Titre");
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        titreCol.setPrefWidth(200);
        
        TableColumn<Offre, String> entrepriseCol = new TableColumn<>("Entreprise");
        entrepriseCol.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(
                param.getValue().getEntreprise() != null ? param.getValue().getEntreprise().getRaisonSociale() : ""));
        entrepriseCol.setPrefWidth(200);
        
        TableColumn<Offre, Integer> postesCol = new TableColumn<>("Postes");
        postesCol.setCellValueFactory(new PropertyValueFactory<>("nbPostes"));
        postesCol.setPrefWidth(80);
        
        TableColumn<Offre, Integer> postesDispCol = new TableColumn<>("Postes dispo.");
        postesDispCol.setCellValueFactory(param ->
            new javafx.beans.property.SimpleIntegerProperty(param.getValue().getNbPostesDisponibles()).asObject());
        postesDispCol.setPrefWidth(100);
        
        TableColumn<Offre, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(
                param.getValue().estActive() ? "Active" : "Désactivée"));
        etatCol.setPrefWidth(100);
        
        tableView.getColumns().addAll(idCol, titreCol, entrepriseCol, postesCol, postesDispCol, etatCol);
        
        // Charger toutes les offres
        tableView.getItems().addAll(offreRepository.findAll());
        
        HBox buttonBox = new HBox(10);
        
        Button btnDesactiver = new Button("Désactiver l'offre");
        btnDesactiver.setOnAction(e -> {
            Offre selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showWarning("Sélection", "Veuillez sélectionner une offre");
                return;
            }
            if (!selected.estActive()) {
                showWarning("État de l'offre", "L'offre est déjà désactivée");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Désactiver l'offre");
            confirm.setContentText("Êtes-vous sûr de vouloir désactiver l'offre \"" + selected.getTitre() + "\" ?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    offreService.desactiverOffre(selected.getIdOffre());
                    tableView.getItems().clear();
                    tableView.getItems().addAll(offreRepository.findAll());
                    showInfo("Succès", "Offre désactivée avec succès");
                    // rafraîchir éventuellement la vue principale
                    updateViewAndScene();
                } catch (Exception ex) {
                    showError("Erreur lors de la désactivation", ex.getMessage());
                }
            }
        });
        
        Button btnSupprimer = new Button("Supprimer l'offre");
        btnSupprimer.setOnAction(e -> {
            Offre selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showWarning("Sélection", "Veuillez sélectionner une offre");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer l'offre");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer l'offre \"" + selected.getTitre() + "\" ?\n\n" +
                                   "Attention : cette action est définitive.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    offreService.supprimerOffre(selected.getIdOffre());
                    tableView.getItems().remove(selected);
                    showInfo("Succès", "Offre supprimée avec succès");
                    updateViewAndScene();
                } catch (Exception ex) {
                    showError("Suppression impossible", ex.getMessage());
                }
            }
        });
        
        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setOnAction(e -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(offreRepository.findAll());
        });
        
        buttonBox.getChildren().addAll(btnDesactiver, btnSupprimer, btnRefresh);
        
        root.getChildren().addAll(title, tableView, buttonBox);
        
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Recherche globale (offres, journaux, éditions) accessible dans tous les espaces
     * via la barre de recherche en haut de l'écran.
     */
    private void showSearchDialog(String query, String type) {
    if (query == null || query.trim().isEmpty()) {
        showWarning("Recherche", "Veuillez saisir un mot-clé pour la recherche.");
        return;
    }

    final String q = query.trim().toLowerCase();

    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Résultats de la recherche");
    dialog.setHeaderText(null); // on gère un header custom
    dialog.setResizable(true);
attachStylesheets(dialog.getDialogPane());   // ✅ ici (obligatoire)
dialog.getDialogPane().getStyleClass().add("root");

    ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().add(closeButtonType);

    // ====== Root (contenu) ======
    VBox root = new VBox(12);
    root.setPadding(new Insets(16));
    root.getStyleClass().add("card"); // si ton CSS contient .card (tu l’as déjà)

    // ====== Header propre ======
    Label title = new Label("Résultats pour : \"" + query.trim() + "\"");
    title.getStyleClass().add("page-title"); // ou "title" si tu préfères

    Label typeChip = new Label(type);
    typeChip.getStyleClass().add("chip"); // tu as déjà .chip dans ton thème

    HBox headerLine = new HBox(10, title, typeChip);
    headerLine.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    Label subtitle = new Label("Filtre appliqué : " + type.toLowerCase());
    subtitle.getStyleClass().add("muted");

    VBox headerBox = new VBox(4, headerLine, subtitle);
    headerBox.getStyleClass().add("header-box"); // si tu veux l’effet header

    // ====== Zone résultats ======
    VBox resultsBox = new VBox(10);

    // On prépare une table + liste
    if ("Offres".equals(type)) {
        TableView<Offre> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(420);
        table.setPlaceholder(new Label("Aucune offre ne correspond à votre recherche."));

        TableColumn<Offre, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idOffre"));
        idCol.setMaxWidth(1f * Integer.MAX_VALUE);
        idCol.setPrefWidth(80);

        TableColumn<Offre, String> titreCol = new TableColumn<>("Titre");
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        titreCol.setMaxWidth(1f * Integer.MAX_VALUE);

        TableColumn<Offre, String> compCol = new TableColumn<>("Compétences");
        compCol.setCellValueFactory(new PropertyValueFactory<>("competences"));
        compCol.setMaxWidth(1f * Integer.MAX_VALUE);

        TableColumn<Offre, String> entrepriseCol = new TableColumn<>("Entreprise");
        entrepriseCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getEntreprise() != null
                                ? param.getValue().getEntreprise().getRaisonSociale()
                                : ""
                )
        );
        entrepriseCol.setMaxWidth(1f * Integer.MAX_VALUE);

        table.getColumns().addAll(idCol, titreCol, compCol, entrepriseCol);

        java.util.List<Offre> allOffres = offreRepository.findAll();
        java.util.List<Offre> filtered = allOffres.stream()
                .filter(o ->
                        (o.getTitre() != null && o.getTitre().toLowerCase().contains(q)) ||
                        (o.getCompetences() != null && o.getCompetences().toLowerCase().contains(q))
                )
                .collect(java.util.stream.Collectors.toList());

        table.setItems(javafx.collections.FXCollections.observableArrayList(filtered));

        Label count = new Label("Résultats : " + filtered.size());
        count.getStyleClass().add("chip");

        resultsBox.getChildren().addAll(count, table);

    } else if ("Journaux".equals(type)) {
        TableView<Journal> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(420);
        table.setPlaceholder(new Label("Aucun journal ne correspond à votre recherche."));

        TableColumn<Journal, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeJournal"));
        codeCol.setPrefWidth(120);

        TableColumn<Journal, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Journal, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getCategorie() != null
                                ? param.getValue().getCategorie().getLibelle()
                                : ""
                )
        );

        table.getColumns().addAll(codeCol, nomCol, catCol);

        java.util.List<Journal> journaux = journalService.getAllJournaux();
        java.util.List<Journal> filtered = journaux.stream()
                .filter(j ->
                        (j.getNom() != null && j.getNom().toLowerCase().contains(q)) ||
                        (j.getCodeJournal() != null && j.getCodeJournal().toLowerCase().contains(q))
                )
                .collect(java.util.stream.Collectors.toList());

        table.setItems(javafx.collections.FXCollections.observableArrayList(filtered));

        Label count = new Label("Résultats : " + filtered.size());
        count.getStyleClass().add("chip");

        resultsBox.getChildren().addAll(count, table);

    } else if ("Éditions".equals(type)) {
        TableView<Edition> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(420);
        table.setPlaceholder(new Label("Aucune édition ne correspond à votre recherche."));

        TableColumn<Edition, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idEdition"));
        idCol.setPrefWidth(90);

        TableColumn<Edition, Integer> numCol = new TableColumn<>("Numéro");
        numCol.setCellValueFactory(new PropertyValueFactory<>("numeroEdition"));
        numCol.setPrefWidth(110);

        TableColumn<Edition, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getDateParution() != null
                                ? param.getValue().getDateParution().toString()
                                : ""
                )
        );
        dateCol.setPrefWidth(140);

        TableColumn<Edition, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getJournal() != null
                                ? param.getValue().getJournal().getNom()
                                : ""
                )
        );

        table.getColumns().addAll(idCol, numCol, dateCol, journalCol);

        java.util.List<Journal> journaux = journalService.getAllJournaux();
        java.util.List<Edition> filtered = journaux.stream()
                .flatMap(j -> journalService.getEditionsByJournal(j.getCodeJournal()).stream())
                .filter(ed ->
                        String.valueOf(ed.getNumeroEdition()).toLowerCase().contains(q) ||
                        (ed.getDateParution() != null && ed.getDateParution().toString().toLowerCase().contains(q)) ||
                        (ed.getJournal() != null && (
                                (ed.getJournal().getNom() != null && ed.getJournal().getNom().toLowerCase().contains(q)) ||
                                (ed.getJournal().getCodeJournal() != null && ed.getJournal().getCodeJournal().toLowerCase().contains(q))
                        ))
                )
                .collect(java.util.stream.Collectors.toList());

        table.setItems(javafx.collections.FXCollections.observableArrayList(filtered));

        Label count = new Label("Résultats : " + filtered.size());
        count.getStyleClass().add("chip");

        resultsBox.getChildren().addAll(count, table);
    }

    // ====== Scroll global (si la fenêtre est petite) ======
    VBox content = new VBox(12, headerBox, resultsBox);
    content.setPadding(new Insets(2));

    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    dialog.getDialogPane().setContent(scroll);

    // Taille par défaut plus “desktop”
    dialog.getDialogPane().setPrefWidth(950);
    dialog.getDialogPane().setPrefHeight(620);

    dialog.showAndWait();
}

    
    /**
     * Dialog pour permettre à une entreprise de modifier ses coordonnées.
     */
   private void showEditEntrepriseCoordonneesDialog() {
    if (!(utilisateurConnecte instanceof Entreprise)) {
        showError("Accès refusé", "Cette fonctionnalité est réservée aux entreprises.");
        return;
    }
    Entreprise entreprise = (Entreprise) utilisateurConnecte;

    Dialog<Entreprise> dialog = new Dialog<>();
    dialog.setTitle("Modifier mes coordonnées");
    dialog.setHeaderText(null); // ✅ on remplace par un header custom plus pro

    // ✅ IMPORTANT : appliquer ton CSS au DialogPane aussi
    attachStylesheets(dialog.getDialogPane());
    dialog.getDialogPane().getStyleClass().addAll("card", "mc-dialog"); // classes custom

    ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // ===== Header pro =====
    Label title = new Label("Modifier les coordonnées de l'entreprise");
    title.getStyleClass().add("page-title");

    Label sub = new Label("Mettez à jour vos informations. Les champs sont enregistrés immédiatement après validation.");
    sub.getStyleClass().add("muted");

    VBox header = new VBox(6, title, sub);
    header.getStyleClass().add("header-box");

    // ===== Form =====
    GridPane grid = new GridPane();
    grid.setHgap(14);
    grid.setVgap(12);
    grid.setPadding(new Insets(10, 0, 0, 0));

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setMinWidth(160);
    col1.setPrefWidth(180);

    ColumnConstraints col2 = new ColumnConstraints();
    col2.setHgrow(javafx.scene.layout.Priority.ALWAYS);
    col2.setFillWidth(true);

    grid.getColumnConstraints().addAll(col1, col2);

    TextField raisonSocialeField = new TextField(entreprise.getRaisonSociale());
    TextField adresseField = new TextField(entreprise.getAdresse());
    TextField telephoneField = new TextField(entreprise.getTelephone());
    TextArea descriptionArea = new TextArea(entreprise.getDescriptionActivite());
    descriptionArea.setPrefRowCount(4);
    descriptionArea.setWrapText(true);

    // ✅ largeur confortable
    raisonSocialeField.setPrefWidth(360);
    adresseField.setPrefWidth(360);
    telephoneField.setPrefWidth(220);

    // ✅ labels pro
    Label l1 = new Label("Raison sociale");
    Label l2 = new Label("Adresse");
    Label l3 = new Label("Téléphone");
    Label l4 = new Label("Description");

    l1.getStyleClass().add("section-title");
    l2.getStyleClass().add("section-title");
    l3.getStyleClass().add("section-title");
    l4.getStyleClass().add("section-title");

    grid.add(l1, 0, 0); grid.add(raisonSocialeField, 1, 0);
    grid.add(l2, 0, 1); grid.add(adresseField, 1, 1);
    grid.add(l3, 0, 2); grid.add(telephoneField, 1, 2);
    grid.add(l4, 0, 3); grid.add(descriptionArea, 1, 3);

    // ===== Wrapper =====
    VBox content = new VBox(14, header, grid);
    content.getStyleClass().add("card");
    dialog.getDialogPane().setContent(content);

    // ✅ Styliser les boutons du Dialog avec ton thème
    javafx.scene.Node saveBtn = dialog.getDialogPane().lookupButton(saveButtonType);
    saveBtn.getStyleClass().add("btn-green-strong"); // ou "button" par défaut

    javafx.scene.Node cancelBtn = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
    cancelBtn.getStyleClass().add("btn-outline"); // on va ajouter ce style dans CSS

    // ===== Validation simple =====
    saveBtn.disableProperty().bind(
        raisonSocialeField.textProperty().isEmpty()
            .or(adresseField.textProperty().isEmpty())
            .or(telephoneField.textProperty().isEmpty())
    );

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            entreprise.setRaisonSociale(raisonSocialeField.getText().trim());
            entreprise.setAdresse(adresseField.getText().trim());
            entreprise.setTelephone(telephoneField.getText().trim());
            entreprise.setDescriptionActivite(descriptionArea.getText().trim());
            try {
                entrepriseRepository.save(entreprise);
                return entreprise;
            } catch (Exception e) {
                showError("Erreur lors de la mise à jour", e.getMessage());
                return null;
            }
        }
        return null;
    });

    java.util.Optional<Entreprise> result = dialog.showAndWait();
    if (result.isPresent()) {
        showInfo("Succès", "Vos coordonnées ont été mises à jour.");
        updateViewAndScene();
    }
}

    
    /**
     * Dialog pour permettre à un demandeur d'emploi de modifier ses coordonnées.
     */
 private void showEditDemandeurCoordonneesDialog() {
    if (!(utilisateurConnecte instanceof DemandeurEmploi)) {
        showError("Accès refusé", "Cette fonctionnalité est réservée aux demandeurs d'emploi.");
        return;
    }
    DemandeurEmploi demandeur = (DemandeurEmploi) utilisateurConnecte;

    Dialog<DemandeurEmploi> dialog = new Dialog<>();
    dialog.setTitle("Modifier mes coordonnées");
    dialog.setHeaderText(null);

    // ✅ appliquer le CSS au dialog
    attachStylesheets(dialog.getDialogPane());
    dialog.getDialogPane().getStyleClass().add("card");

    ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // ================= HEADER =================
    Label title = new Label("Modifier vos coordonnées");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Mettez à jour vos informations personnelles et votre CV.");
    subtitle.getStyleClass().add("muted");

    VBox header = new VBox(6, title, subtitle);
    header.getStyleClass().add("header-box");

    // ================= FORM =================
    GridPane grid = new GridPane();
    grid.setHgap(14);
    grid.setVgap(12);

    ColumnConstraints c1 = new ColumnConstraints(180);
    ColumnConstraints c2 = new ColumnConstraints();
    c2.setHgrow(javafx.scene.layout.Priority.ALWAYS);
    grid.getColumnConstraints().addAll(c1, c2);

    TextField nomField = new TextField(demandeur.getNom());
    TextField prenomField = new TextField(demandeur.getPrenom());
    TextField adresseField = new TextField(demandeur.getAdresse());
    TextField telephoneField = new TextField(demandeur.getTelephone());
    TextField faxField = new TextField(demandeur.getFax());
    TextField diplomeField = new TextField(demandeur.getDiplome());

    Spinner<Integer> experienceSpinner = new Spinner<>(0, 50,
            demandeur.getExperience() != null ? demandeur.getExperience() : 0, 1);

    Spinner<Double> salaireSpinner = new Spinner<>(0.0, 1_000_000.0,
            demandeur.getSalaireSouhaite() != null ? demandeur.getSalaireSouhaite() : 0.0, 100.0);
    salaireSpinner.setEditable(true);

    int r = 0;
    grid.add(new Label("Nom"), 0, r); grid.add(nomField, 1, r++);
    grid.add(new Label("Prénom"), 0, r); grid.add(prenomField, 1, r++);
    grid.add(new Label("Adresse"), 0, r); grid.add(adresseField, 1, r++);
    grid.add(new Label("Téléphone"), 0, r); grid.add(telephoneField, 1, r++);
    grid.add(new Label("Fax"), 0, r); grid.add(faxField, 1, r++);
    grid.add(new Label("Diplôme"), 0, r); grid.add(diplomeField, 1, r++);
    grid.add(new Label("Expérience (années)"), 0, r); grid.add(experienceSpinner, 1, r++);
    grid.add(new Label("Salaire souhaité"), 0, r); grid.add(salaireSpinner, 1, r++);

    // ================= CV =================
    Separator sep = new Separator();

    Document cvActuel = documentService.getCvValideParDemandeur(demandeur.getIdUtilisateur());
    String cvInfo = (cvActuel != null) ? cvActuel.getNomFichier() : "Aucun CV";

    Label cvLabel = new Label("CV actuel : " + cvInfo);
    cvLabel.getStyleClass().add("muted");

    Button btnRemplacerCV = new Button("Remplacer mon CV");
    btnRemplacerCV.getStyleClass().add("btn-green-soft");

    Label newCVLabel = new Label();
    newCVLabel.getStyleClass().add("help");

    final File[] newCVFile = new File[1];

    btnRemplacerCV.setOnAction(e -> {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir votre nouveau CV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PNG", "*.png")
        );

        File file = chooser.showOpenDialog(dialog.getOwner());
        if (file != null) {
            newCVFile[0] = file;
            newCVLabel.setText("Nouveau CV sélectionné : " + file.getName());
        }
    });

    VBox cvBox = new VBox(8, cvLabel, btnRemplacerCV, newCVLabel);
    cvBox.getStyleClass().add("card");

    // ================= CONTENT =================
    VBox content = new VBox(16, header, grid, sep, cvBox);
    content.setPadding(new Insets(20));

    // ================= SCROLL =================
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setPrefViewportWidth(600);
    scrollPane.setPrefViewportHeight(500);

    dialog.getDialogPane().setContent(scrollPane);

    // ================= BUTTON STYLES =================
    dialog.getDialogPane().lookupButton(saveButtonType)
            .getStyleClass().add("btn-green-strong");

    dialog.getDialogPane().lookupButton(ButtonType.CANCEL)
            .getStyleClass().add("btn-outline");

    dialog.setResultConverter(btn -> {
        if (btn == saveButtonType) {
            demandeur.setNom(nomField.getText().trim());
            demandeur.setPrenom(prenomField.getText().trim());
            demandeur.setAdresse(adresseField.getText().trim());
            demandeur.setTelephone(telephoneField.getText().trim());
            demandeur.setFax(faxField.getText().trim());
            demandeur.setDiplome(diplomeField.getText().trim());
            demandeur.setExperience(experienceSpinner.getValue());
            demandeur.setSalaireSouhaite(salaireSpinner.getValue());

            try {
                demandeurEmploiRepository.save(demandeur);
                if (newCVFile[0] != null) {
                    remplacerCVDemandeur(demandeur, newCVFile[0]);
                }
                return demandeur;
            } catch (Exception e) {
                showError("Erreur", e.getMessage());
            }
        }
        return null;
    });

  dialog.showAndWait().ifPresent(result -> {
    showInfo("Succès", "Vos coordonnées ont été mises à jour.");
    updateViewAndScene();
});

}


    
    private void showGestionUtilisateurs() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des utilisateurs");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        attachStylesheets(root);
        root.getStyleClass().add("gestion-users-root");
        
        Label title = new Label("Gestion des utilisateurs");
        title.getStyleClass().add("mc-style-22");
        
        // TableView pour afficher les utilisateurs
        TableView<Utilisateur> tableView = new TableView<>();
         tableView.getStyleClass().add("gestion-users-table");
        
        TableColumn<Utilisateur, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        idCol.setPrefWidth(80);
        
        TableColumn<Utilisateur, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        loginCol.setPrefWidth(150);
        
        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getRole().toString()));
        roleCol.setPrefWidth(150);
        
        TableColumn<Utilisateur, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> {
            Utilisateur u = param.getValue();
            String type = "";
            if (u instanceof Entreprise) {
                type = "Entreprise";
            } else if (u instanceof DemandeurEmploi) {
                type = "Demandeur d'emploi";
            } else if (u instanceof Administrateur) {
                type = "Administrateur";
            }
            return new javafx.beans.property.SimpleStringProperty(type);
        });
        typeCol.setPrefWidth(150);
        
        TableColumn<Utilisateur, Boolean> actifCol = new TableColumn<>("Actif");
        actifCol.setCellValueFactory(new PropertyValueFactory<>("actif"));
        actifCol.setPrefWidth(100);
        
        // Colonne Actions avec boutons CV pour les demandeurs d'emploi
        TableColumn<Utilisateur, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnTelechargerCV = new Button("Télécharger CV");
            private final HBox actionsBox = new HBox(5, btnTelechargerCV);
            
            {
                btnTelechargerCV.getStyleClass().add("mc-style-04");
                
                btnTelechargerCV.setOnAction(event -> {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    if (utilisateur instanceof DemandeurEmploi) {
                        telechargerCVDemandeur((DemandeurEmploi) utilisateur);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    if (utilisateur instanceof DemandeurEmploi) {
                        setGraphic(actionsBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        actionsCol.setPrefWidth(200);
        
        tableView.getColumns().addAll(idCol, loginCol, roleCol, typeCol, actifCol, actionsCol);
        
        // Charger les utilisateurs
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        tableView.getItems().addAll(utilisateurs);
        
        // Boutons d'action
        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("gestion-users-actions");
        Button btnActiver = new Button("Activer/Désactiver");
        btnActiver.setOnAction(e -> {
            Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setActif(!selected.getActif());
                utilisateurRepository.save(selected);
                tableView.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setContentText("Statut de l'utilisateur modifié avec succès");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélection");
                alert.setContentText("Veuillez sélectionner un utilisateur");
                alert.showAndWait();
            }
        });
        
        Button btnModifierMotDePasse = new Button("Modifier mot de passe");
        btnModifierMotDePasse.setOnAction(e -> {
            Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ModifierMotDePasseDialog dialog = new ModifierMotDePasseDialog(authentificationService, selected);
                dialog.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélection");
                alert.setContentText("Veuillez sélectionner un utilisateur");
                alert.showAndWait();
            }
        });
        
        Button btnDetails = new Button("Détails");
        btnDetails.setOnAction(e -> {
            Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDetailsUtilisateur(selected);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélection");
                alert.setContentText("Veuillez sélectionner un utilisateur");
                alert.showAndWait();
            }
        });
        
        Button btnRefresh = new Button("Actualiser");
            btnRefresh.getStyleClass().add("refresh");

        btnRefresh.setOnAction(e -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(utilisateurRepository.findAll());
        });
        
        buttonBox.getChildren().addAll(btnActiver, btnModifierMotDePasse, btnDetails, btnRefresh);
        
        root.getChildren().addAll(title, tableView, buttonBox);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
 private void showDetailsUtilisateur(Utilisateur utilisateur) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Détails de l'utilisateur");
    alert.setHeaderText(null); // plus clean

    // ✅ Container principal
    VBox root = new VBox(12);
    root.setPadding(new Insets(14));
    root.getStyleClass().add("card");

    // ✅ Titre
    Label title = new Label("Informations de l'utilisateur");
    title.getStyleClass().add("page-title");

    // ✅ Infos principales en "grille"
    GridPane grid = new GridPane();
    grid.setHgap(12);
    grid.setVgap(10);

    // petites helpers
    java.util.function.BiConsumer<String, String> row = (k, v) -> {
        int r = grid.getRowCount();
        Label key = new Label(k + " :");
        key.getStyleClass().addAll("muted");
        Label val = new Label(v == null ? "" : v);
        val.getStyleClass().add("section-title");
        grid.add(key, 0, r);
        grid.add(val, 1, r);
    };

    row.accept("ID", String.valueOf(utilisateur.getIdUtilisateur()));
    row.accept("Login", utilisateur.getLogin());
    row.accept("Rôle", String.valueOf(utilisateur.getRole()));
    row.accept("Actif", utilisateur.getActif() ? "Oui" : "Non");

    // ✅ Bloc détails (Entreprise / Demandeur) dans TextArea => scroll automatique si long
    StringBuilder details = new StringBuilder();

    if (utilisateur instanceof Entreprise) {
        Entreprise e = (Entreprise) utilisateur;
        details.append("— Informations Entreprise —\n");
        details.append("Raison sociale : ").append(nullSafe(e.getRaisonSociale())).append("\n");
        details.append("Adresse : ").append(nullSafe(e.getAdresse())).append("\n");
        details.append("Téléphone : ").append(nullSafe(e.getTelephone())).append("\n\n");
        details.append("Description :\n").append(nullSafe(e.getDescriptionActivite())).append("\n");
    } else if (utilisateur instanceof DemandeurEmploi) {
        DemandeurEmploi d = (DemandeurEmploi) utilisateur;
        details.append("— Informations Demandeur d'emploi —\n");
        details.append("Nom : ").append(nullSafe(d.getNom())).append("\n");
        details.append("Prénom : ").append(nullSafe(d.getPrenom())).append("\n");
        details.append("Adresse : ").append(nullSafe(d.getAdresse())).append("\n");
        details.append("Téléphone : ").append(nullSafe(d.getTelephone())).append("\n");
        details.append("Diplôme : ").append(nullSafe(d.getDiplome())).append("\n");
        details.append("Expérience : ").append(d.getExperience() != null ? d.getExperience() : 0).append(" ans\n");
        details.append("Salaire souhaité : ").append(d.getSalaireSouhaite() != null ? d.getSalaireSouhaite() : "").append("\n");
    }

    TextArea detailsArea = new TextArea(details.toString());
    detailsArea.setEditable(false);
    detailsArea.setWrapText(true);
    detailsArea.setPrefRowCount(8);
    detailsArea.getStyleClass().add("text-area"); // ton thème inputs

    VBox.setVgrow(detailsArea, Priority.ALWAYS);

    root.getChildren().addAll(title, grid, detailsArea);

    alert.getDialogPane().setContent(root);

    // ✅ appliquer ton CSS
    attachStylesheets(alert.getDialogPane());

    // ✅ rendre la fenêtre plus “pro”
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.getDialogPane().setPrefWidth(520);

    alert.showAndWait();
}

// helper anti-null
private String nullSafe(String s) {
    return s == null ? "" : s;
}

    
    private void showGestionJournaux() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des journaux et éditions");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        attachStylesheets(root);
        
        Label title = new Label("Gestion des journaux et de leurs éditions");
        title.getStyleClass().add("mc-style-22");
        
        // TableView pour afficher les journaux
        TableView<Journal> journauxTable = new TableView<>();
        
        TableColumn<Journal, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeJournal"));
        codeCol.setPrefWidth(100);
        
        TableColumn<Journal, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(200);
        
        TableColumn<Journal, String> periodiciteCol = new TableColumn<>("Périodicité");
        periodiciteCol.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getPeriodicite().toString()));
        periodiciteCol.setPrefWidth(120);
        
        TableColumn<Journal, String> langueCol = new TableColumn<>("Langue");
        langueCol.setCellValueFactory(new PropertyValueFactory<>("langue"));
        langueCol.setPrefWidth(100);
        
        TableColumn<Journal, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getCategorie().getLibelle()));
        categorieCol.setPrefWidth(150);
        
        journauxTable.getColumns().addAll(codeCol, nomCol, periodiciteCol, langueCol, categorieCol);
        
        // Charger les journaux
        List<Journal> journaux = journalRepository.findAll();
        journauxTable.getItems().addAll(journaux);
        
        // TableView pour afficher les éditions du journal sélectionné
        Label editionsTitle = new Label("Éditions du journal sélectionné");
        editionsTitle.getStyleClass().add("mc-style-19");
        
        TableView<Edition> editionsTable = new TableView<>();
        
        TableColumn<Edition, Long> idEditionCol = new TableColumn<>("ID");
        idEditionCol.setCellValueFactory(new PropertyValueFactory<>("idEdition"));
        idEditionCol.setPrefWidth(80);
        
        TableColumn<Edition, Integer> numEditionCol = new TableColumn<>("Numéro");
        numEditionCol.setCellValueFactory(new PropertyValueFactory<>("numeroEdition"));
        numEditionCol.setPrefWidth(100);
        
        TableColumn<Edition, String> dateParutionCol = new TableColumn<>("Date de parution");
        dateParutionCol.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(param.getValue().getDateParution().toString()));
        dateParutionCol.setPrefWidth(150);
        
        editionsTable.getColumns().addAll(idEditionCol, numEditionCol, dateParutionCol);
        
        // Quand on sélectionne un journal, charger ses éditions
        journauxTable.getSelectionModel().selectedItemProperty().addListener((obs, oldJournal, newJournal) -> {
            editionsTable.getItems().clear();
            if (newJournal != null) {
                editionsTable.getItems().addAll(journalService.getEditionsByJournal(newJournal.getCodeJournal()));
            }
        });
        
        // Boutons d'action
        HBox buttonBox = new HBox(10);
        Button btnNouveau = new Button("Nouveau journal");
        btnNouveau.setOnAction(e -> showNouveauJournalDialog(journauxTable));
        
        Button btnSupprimer = new Button("Supprimer journal");
        btnSupprimer.setOnAction(e -> {
            Journal selected = journauxTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer le journal");
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer le journal \"" + selected.getNom() + "\" ?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    try {
                        journalRepository.delete(selected);
                        journauxTable.getItems().remove(selected);
                        editionsTable.getItems().clear();
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Succès");
                        success.setContentText("Journal supprimé avec succès");
                        success.showAndWait();
                    } catch (Exception ex) {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Erreur");
                        error.setContentText("Erreur lors de la suppression: " + ex.getMessage());
                        error.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélection");
                alert.setContentText("Veuillez sélectionner un journal");
                alert.showAndWait();
            }
        });
        
        Button btnNouvelleEdition = new Button("Nouvelle édition");
        btnNouvelleEdition.setOnAction(e -> {
            Journal selected = journauxTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélection");
                alert.setContentText("Veuillez sélectionner un journal pour créer une édition");
                alert.showAndWait();
                return;
            }
            showNouvelleEditionDialog(selected, editionsTable);
        });
        
        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setOnAction(e -> {
            journauxTable.getItems().clear();
            journauxTable.getItems().addAll(journalRepository.findAll());
            editionsTable.getItems().clear();
        });
        
        buttonBox.getChildren().addAll(btnNouveau, btnSupprimer, btnNouvelleEdition, btnRefresh);
        
        root.getChildren().addAll(title, journauxTable, buttonBox, editionsTitle, editionsTable);
        
        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.show();
    }

private void showNouvelleEditionDialog(Journal journal, TableView<Edition> editionsTable) {
    Dialog<Edition> dialog = new Dialog<>();
    dialog.setTitle("Nouvelle édition");
    dialog.setHeaderText(null);

    // ✅ CSS sur la DialogPane (TRÈS IMPORTANT)
    attachStylesheets(dialog.getDialogPane());
    dialog.getDialogPane().getStyleClass().addAll("card");

    ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

    // ===== Header premium =====
    Label icon = new Label("🗞️");
    icon.getStyleClass().add("page-title");

    Label title = new Label("Nouvelle édition");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Journal : " + (journal.getNom() != null ? journal.getNom() : ""));
    subtitle.getStyleClass().add("muted");

    // petit “chip” (optionnel si tu as déjà .chip, sinon ça marche sans)
    Label chip = new Label(journal.getCodeJournal() != null ? journal.getCodeJournal() : "");
    chip.getStyleClass().add("chip");

    HBox headerTop = new HBox(10, icon, title);
    headerTop.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    HBox headerBottom = new HBox(10, subtitle, chip);
    headerBottom.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    VBox header = new VBox(6, headerTop, headerBottom);
    header.getStyleClass().add("header-box");

    // ===== Form card =====
    VBox card = new VBox(12);
    card.getStyleClass().add("card");

    GridPane grid = new GridPane();
    grid.setHgap(14);
    grid.setVgap(12);

    javafx.scene.layout.ColumnConstraints c1 = new javafx.scene.layout.ColumnConstraints();
    c1.setMinWidth(160);
    c1.setPrefWidth(180);

    javafx.scene.layout.ColumnConstraints c2 = new javafx.scene.layout.ColumnConstraints();
    c2.setHgrow(javafx.scene.layout.Priority.ALWAYS);

    grid.getColumnConstraints().addAll(c1, c2);

    // Champs
    Spinner<Integer> numeroSpinner = new Spinner<>(1, 100000, 1, 1);
    numeroSpinner.setEditable(true);
    numeroSpinner.setMaxWidth(Double.MAX_VALUE);

    DatePicker datePicker = new DatePicker(LocalDate.now());
    datePicker.setMaxWidth(Double.MAX_VALUE);

    Label lNumero = new Label("Numéro d’édition");
    lNumero.getStyleClass().add("section-title");

    Label lDate = new Label("Date de parution");
    lDate.getStyleClass().add("section-title");

    Label hintNumero = new Label("Ex: 1, 2, 3 ... (doit être un entier).");
    hintNumero.getStyleClass().add("muted");

    Label hintDate = new Label("Par défaut : aujourd’hui. Tu peux choisir une autre date.");
    hintDate.getStyleClass().add("muted");

    VBox numeroBox = new VBox(4, numeroSpinner, hintNumero);
    VBox dateBox = new VBox(4, datePicker, hintDate);

    grid.add(lNumero, 0, 0);
    grid.add(numeroBox, 1, 0);

    grid.add(lDate, 0, 1);
    grid.add(dateBox, 1, 1);

    // Message d’erreur inline
    Label errorLabel = new Label();
    errorLabel.getStyleClass().add("status-bad");
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);

    // Séparateur + tout regrouper
    javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

    card.getChildren().addAll(grid, sep, errorLabel);

    VBox content = new VBox(14, header, card);
    content.setPadding(new Insets(16));

    // ===== Scroll (petits écrans) =====
    ScrollPane scroller = new ScrollPane(content);
    scroller.setFitToWidth(true);
    scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scroller.setPannable(true);
    scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    attachStylesheets(scroller);

    dialog.getDialogPane().setContent(scroller);

    // ===== Boutons style + UX =====
    javafx.scene.control.Button createBtn =
        (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(createButtonType);
    javafx.scene.control.Button cancelBtn =
        (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

    // Style (si tu as déjà tes classes)
    createBtn.getStyleClass().add("btn-secondary-strong");
    cancelBtn.getStyleClass().add("btn-secondary");

    // Focus direct sur numéro
    javafx.application.Platform.runLater(() -> numeroSpinner.getEditor().requestFocus());

    // Validation inline
    Runnable showError = (/* no args */) -> {};
    Runnable validate = () -> {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        createBtn.setDisable(false);

        // date obligatoire
        if (datePicker.getValue() == null) {
            createBtn.setDisable(true);
            errorLabel.setText("Veuillez sélectionner une date de parution.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        // numéro valide (entier > 0)
        String raw = numeroSpinner.getEditor().getText();
        try {
            int v = Integer.parseInt(raw.trim());
            if (v <= 0) {
                throw new NumberFormatException();
            }
        } catch (Exception ex) {
            createBtn.setDisable(true);
            errorLabel.setText("Numéro invalide : veuillez saisir un entier positif.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    };

    validate.run();
    datePicker.valueProperty().addListener((obs, o, n) -> validate.run());
    numeroSpinner.getEditor().textProperty().addListener((obs, o, n) -> validate.run());

    dialog.setResultConverter(btn -> {
        if (btn == createButtonType) {
            try {
                int numero = Integer.parseInt(numeroSpinner.getEditor().getText().trim());
                LocalDate date = datePicker.getValue();

                return journalService.creerEdition(
                    journal.getCodeJournal(),
                    numero,
                    date
                );
            } catch (Exception e) {
                // Erreur backend -> Alert (mais CSS appliqué)
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText("Création impossible");
                error.setContentText(e.getMessage());
                attachStylesheets(error.getDialogPane());
                error.showAndWait();
                return null;
            }
        }
        return null;
    });

    java.util.Optional<Edition> result = dialog.showAndWait();
    if (result.isPresent()) {
        editionsTable.getItems().add(result.get());

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Succès");
        success.setHeaderText(null);
        success.setContentText("Édition créée avec succès.");
        attachStylesheets(success.getDialogPane());
        success.showAndWait();
    }
}


// Helper simple (si tu l’as déjà, garde le tien)
private Label labelBold(String text) {
    Label l = new Label(text);
    l.getStyleClass().add("muted"); // ou "section-title" selon ton thème
    return l;
}

    
   private void showNouveauJournalDialog(TableView<Journal> tableView) {
    Dialog<Journal> dialog = new Dialog<>();
    dialog.setTitle("Nouveau journal");
    dialog.setHeaderText(null); // plus clean

    ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

    // ✅ Appliquer le CSS sur la DialogPane
    attachStylesheets(dialog.getDialogPane());
    dialog.getDialogPane().getStyleClass().add("card");

    // ===== Header (style pro) =====
    Label title = new Label("Créer un nouveau journal");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Renseignez les informations ci-dessous pour ajouter un journal.");
    subtitle.getStyleClass().add("muted");

    VBox header = new VBox(4, title, subtitle);
    header.getStyleClass().add("header-box");

    // ===== Form =====
    GridPane grid = new GridPane();
    grid.setHgap(14);
    grid.setVgap(12);
    grid.setPadding(new Insets(16));

    // Colonnes : label à gauche / champs à droite
    javafx.scene.layout.ColumnConstraints c1 = new javafx.scene.layout.ColumnConstraints();
    c1.setMinWidth(140);
    c1.setPrefWidth(160);

    javafx.scene.layout.ColumnConstraints c2 = new javafx.scene.layout.ColumnConstraints();
    c2.setHgrow(javafx.scene.layout.Priority.ALWAYS);

    grid.getColumnConstraints().addAll(c1, c2);

    // Champs
    TextField codeField = new TextField();
    codeField.setPromptText("Ex: JRN001");
    codeField.setPrefWidth(340);

    TextField nomField = new TextField();
    nomField.setPromptText("Nom du journal");

    ComboBox<Journal.Periodicite> periodiciteCombo = new ComboBox<>();
    periodiciteCombo.getItems().addAll(Journal.Periodicite.values());
    periodiciteCombo.setPromptText("Choisir...");
    periodiciteCombo.setMaxWidth(Double.MAX_VALUE);

    TextField langueField = new TextField();
    langueField.setPromptText("Ex: Français");

    ComboBox<Categorie> categorieCombo = new ComboBox<>();
    List<Categorie> categories = journalService.getAllCategories();
    categorieCombo.getItems().addAll(categories);
    categorieCombo.setPromptText("Choisir...");
    categorieCombo.setMaxWidth(Double.MAX_VALUE);

    // Affichage du ComboBox: libellé
    categorieCombo.setCellFactory(param -> new ListCell<>() {
        @Override protected void updateItem(Categorie item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getLibelle());
        }
    });
    categorieCombo.setButtonCell(new ListCell<>() {
        @Override protected void updateItem(Categorie item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getLibelle());
        }
    });

    // Bouton "Nouvelle catégorie" (même style que tes boutons)
    Button btnNouvelleCategorie = new Button("Nouvelle catégorie");
    btnNouvelleCategorie.getStyleClass().add("btn-secondary"); // ou laisse vide si tu veux vert par défaut
    btnNouvelleCategorie.setOnAction(e -> {
        TextInputDialog categorieDialog = new TextInputDialog();
        categorieDialog.setTitle("Nouvelle catégorie");
        categorieDialog.setHeaderText("Créer une nouvelle catégorie");
        categorieDialog.setContentText("Libellé de la catégorie :");

        // ✅ CSS aussi sur ce dialog
        attachStylesheets(categorieDialog.getDialogPane());

        java.util.Optional<String> r = categorieDialog.showAndWait();
        if (r.isPresent() && !r.get().trim().isEmpty()) {
            try {
                Categorie nouvelleCategorie = journalService.creerCategorie(r.get().trim());
                categorieCombo.getItems().add(nouvelleCategorie);
                categorieCombo.setValue(nouvelleCategorie);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText(null);
                success.setContentText("Catégorie créée avec succès.");
                attachStylesheets(success.getDialogPane());
                success.showAndWait();
            } catch (Exception ex) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la création : " + ex.getMessage());
                attachStylesheets(error.getDialogPane());
                error.showAndWait();
            }
        }
    });

    // Layout catégorie + bouton
    HBox categorieBox = new HBox(10, categorieCombo, btnNouvelleCategorie);
    categorieBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    HBox.setHgrow(categorieCombo, javafx.scene.layout.Priority.ALWAYS);

    // Ajout au grid
    grid.add(labelBold("Code :"), 0, 0);
    grid.add(codeField, 1, 0);

    grid.add(labelBold("Nom :"), 0, 1);
    grid.add(nomField, 1, 1);

    grid.add(labelBold("Périodicité :"), 0, 2);
    grid.add(periodiciteCombo, 1, 2);

    grid.add(labelBold("Langue :"), 0, 3);
    grid.add(langueField, 1, 3);

    grid.add(labelBold("Catégorie :"), 0, 4);
    grid.add(categorieBox, 1, 4);

    // ✅ Wrapper (scroll si petit écran)
    VBox formCard = new VBox(12, header, grid);
    formCard.setPadding(new Insets(2));

    ScrollPane scroller = new ScrollPane(formCard);
    scroller.setFitToWidth(true);
    scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scroller.setPannable(true);
    scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    attachStylesheets(scroller);

    dialog.getDialogPane().setContent(scroller);

    // ✅ Désactiver le bouton Créer tant que non valide
    javafx.scene.control.Button createBtn =
        (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(createButtonType);
    createBtn.setDisable(true);
    createBtn.getStyleClass().add("btn-secondary-strong"); // optionnel : style action principale

    Runnable validateFields = () -> {
        boolean ok = !codeField.getText().trim().isEmpty()
                && !nomField.getText().trim().isEmpty()
                && periodiciteCombo.getValue() != null
                && !langueField.getText().trim().isEmpty()
                && categorieCombo.getValue() != null;
        createBtn.setDisable(!ok);
    };

    codeField.textProperty().addListener((obs, o, n) -> validateFields.run());
    nomField.textProperty().addListener((obs, o, n) -> validateFields.run());
    periodiciteCombo.valueProperty().addListener((obs, o, n) -> validateFields.run());
    langueField.textProperty().addListener((obs, o, n) -> validateFields.run());
    categorieCombo.valueProperty().addListener((obs, o, n) -> validateFields.run());

    dialog.setResultConverter(btn -> {
        if (btn == createButtonType) {
            try {
                return journalService.creerJournal(
                    codeField.getText().trim(),
                    nomField.getText().trim(),
                    periodiciteCombo.getValue(),
                    langueField.getText().trim(),
                    categorieCombo.getValue().getIdCategorie()
                );
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la création : " + e.getMessage());
                attachStylesheets(error.getDialogPane());
                error.showAndWait();
                return null;
            }
        }
        return null;
    });

    java.util.Optional<Journal> result = dialog.showAndWait();
    if (result.isPresent()) {
        tableView.getItems().add(result.get());

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Succès");
        success.setHeaderText(null);
        success.setContentText("Journal créé avec succès.");
        attachStylesheets(success.getDialogPane());
        success.showAndWait();
    }
}

// Petit helper: label plus lisible
private Label labelBold(String text) {
    Label l = new Label(text);
    l.getStyleClass().add("muted"); // si tu préfères "section-title", change ici
    return l;
}

    
   private void showStatistiques() {
    Stage stage = new Stage();
    stage.setTitle("Statistiques");

    // ===== Root (contenu réel) =====
    VBox content = new VBox(16);
    content.setPadding(new Insets(20));
    content.setFillWidth(true);

    // Appliquer CSS sur le contenu
    attachStylesheets(content);

    // ===== Header =====
    Label title = new Label("Statistiques du système");
    title.getStyleClass().addAll("mc-style-22", "page-title"); // page-title si tu l'as dans CSS (sinon ignore)

    Label subtitle = new Label("Vue globale des indicateurs clés.");
    subtitle.getStyleClass().addAll("muted"); // si tu as "muted" dans ton CSS

    VBox header = new VBox(4, title, subtitle);
    header.getStyleClass().add("header-box"); // joli bloc (si présent dans ton CSS)

    // ===== Calcul des statistiques =====
    long nbUtilisateurs = utilisateurRepository.count();
    long nbEntreprises = entrepriseRepository.count();
    long nbDemandeurs = demandeurEmploiRepository.count();
    long nbJournaux = journalRepository.count();
    long nbOffres = offreRepository.count();
    long nbCandidatures = candidatureRepository.count();
    long nbRecrutements = recrutementRepository.count();
    long nbAbonnements = abonnementRepository.count();

    // Offres par état
    int offresActives = offreRepository.findByEtat(Offre.EtatOffre.ACTIVE).size();
    int offresDesactivees = offreRepository.findByEtat(Offre.EtatOffre.DESACTIVEE).size();

    // ===== Carte 1 : Statistiques globales =====
    Label globalTitle = new Label("Indicateurs globaux");
    globalTitle.getStyleClass().addAll("section-title");

    GridPane grid = new GridPane();
    grid.setHgap(14);
    grid.setVgap(10);

    // Colonnes : Label | Valeur (chip)
    ColumnConstraints c1 = new ColumnConstraints();
    c1.setHgrow(Priority.ALWAYS);
    ColumnConstraints c2 = new ColumnConstraints();
    c2.setHgrow(Priority.NEVER);
    grid.getColumnConstraints().addAll(c1, c2);

    int r = 0;
    r = addStatRow(grid, r, "Nombre total d'utilisateurs", nbUtilisateurs);
    r = addStatRow(grid, r, "Nombre d'entreprises", nbEntreprises);
    r = addStatRow(grid, r, "Nombre de demandeurs d'emploi", nbDemandeurs);
    r = addStatRow(grid, r, "Nombre de journaux", nbJournaux);
    r = addStatRow(grid, r, "Nombre d'offres", nbOffres);
    r = addStatRow(grid, r, "Nombre de candidatures", nbCandidatures);
    r = addStatRow(grid, r, "Nombre de recrutements", nbRecrutements);
    r = addStatRow(grid, r, "Nombre d'abonnements", nbAbonnements);

    VBox globalCard = new VBox(12, globalTitle, grid);
    globalCard.getStyleClass().add("card");

    // ===== Carte 2 : Offres par état =====
    Label offresTitle = new Label("Offres par état");
    offresTitle.getStyleClass().addAll("mc-style-28", "section-title");

    GridPane offresGrid = new GridPane();
    offresGrid.setHgap(14);
    offresGrid.setVgap(10);

    ColumnConstraints oc1 = new ColumnConstraints();
    oc1.setHgrow(Priority.ALWAYS);
    ColumnConstraints oc2 = new ColumnConstraints();
    oc2.setHgrow(Priority.NEVER);
    offresGrid.getColumnConstraints().addAll(oc1, oc2);

    int r2 = 0;
    r2 = addStatRow(offresGrid, r2, "Offres actives", offresActives);
    r2 = addStatRow(offresGrid, r2, "Offres désactivées", offresDesactivees);

    VBox offresCard = new VBox(12, offresTitle, offresGrid);
    offresCard.getStyleClass().add("card");

    // ===== Assemblage =====
    content.getChildren().addAll(header, globalCard, offresCard);

    // ===== Scroll (si fenêtre petite) =====
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scroll.setPannable(true);

    // IMPORTANT : appliquer CSS au ScrollPane aussi (sinon parfois pas de styles)
    attachStylesheets(scroll);

    Scene scene = new Scene(scroll, 620, 560);
    stage.setMinWidth(560);
    stage.setMinHeight(500);
    stage.setScene(scene);
    stage.show();
}

/**
 * Ajoute une ligne "label + valeur" stylée (valeur en chip).
 */
private int addStatRow(GridPane grid, int row, String labelText, long value) {
    Label label = new Label(labelText);
    label.getStyleClass().add("muted"); // texte secondaire

    Label chip = new Label(String.valueOf(value));
    chip.getStyleClass().add("chip");   // badge vert (si défini dans ton CSS)

    grid.add(label, 0, row);
    grid.add(chip, 1, row);
    return row + 1;
}

    
    private Label createStatLabel(String text, long value) {
        Label label = new Label(text + ": " + value);
        label.getStyleClass().add("mc-style-18");
        return label;
    }
    
    // ========== GESTION DES DOCUMENTS ==========
    
    public void handleUploadCV() {
        if (utilisateurConnecte instanceof DemandeurEmploi) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner votre CV");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PNG", "*.png")
            );
            
            File file = fileChooser.showOpenDialog(getCurrentStage());
            if (file != null) {
                try {
                    // Convertir le fichier en MultipartFile
                    MultipartFile multipartFile = convertFileToMultipartFile(file);
                    
                    Document document = documentService.uploadDocument(
                        multipartFile, 
                        (DemandeurEmploi) utilisateurConnecte,
                        "CV"
                    );
                    
                    showAlert("Succès", "CV téléversé avec succès !\nEn attente de validation par l'administrateur.", Alert.AlertType.INFORMATION);
                    
                } catch (IOException e) {
                    showAlert("Erreur", "Erreur lors du téléversement du fichier: " + e.getMessage(), Alert.AlertType.ERROR);
                } catch (IllegalArgumentException e) {
                    showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    public void showDocumentsEnAttente() {
        if (utilisateurConnecte.getRole() == Utilisateur.Role.ADMINISTRATEUR) {
            Stage stage = new Stage();
            stage.setTitle("Documents en attente de validation");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            attachStylesheets(root);
            Label title = new Label("Documents en attente de validation");
            title.getStyleClass().add("mc-style-22");
            
            // TableView pour afficher les documents
            TableView<Document> tableDocuments = new TableView<>();
            
            TableColumn<Document, String> colNomFichier = new TableColumn<>("Nom du fichier");
            colNomFichier.setCellValueFactory(new PropertyValueFactory<>("nomFichier"));
            
            TableColumn<Document, String> colTypeDocument = new TableColumn<>("Type");
            colTypeDocument.setCellValueFactory(new PropertyValueFactory<>("typeDocument"));
            
            TableColumn<Document, String> colDemandeur = new TableColumn<>("Demandeur");
            colDemandeur.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getNom() + " " + 
                    cellData.getValue().getDemandeur().getPrenom()
                )
            );
            
            TableColumn<Document, String> colDateUpload = new TableColumn<>("Date upload");
            colDateUpload.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateUpload().toLocalDate().toString()
                )
            );
            
            TableColumn<Document, Void> colActions = new TableColumn<>("Actions");
            colActions.setCellFactory(param -> new TableCell<>() {
                private final Button btnValider = new Button("Valider");
                private final Button btnRejeter = new Button("Rejeter");
                private final HBox actionsBox = new HBox(5, btnValider, btnRejeter);
                
                {
                    btnValider.getStyleClass().add("mc-style-04");
                    btnRejeter.getStyleClass().add("mc-style-13");
                    
                    btnValider.setOnAction(event -> {
                        Document doc = getTableView().getItems().get(getIndex());
                        validerDocument(doc);
                        tableDocuments.getItems().remove(doc);
                    });
                    
                    btnRejeter.setOnAction(event -> {
                        Document doc = getTableView().getItems().get(getIndex());
                        rejeterDocument(doc);
                        tableDocuments.getItems().remove(doc);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionsBox);
                    }
                }
            });
            
            tableDocuments.getColumns().addAll(colNomFichier, colTypeDocument, colDemandeur, colDateUpload, colActions);
            
            // Charger les documents en attente
            List<Document> documentsEnAttente = documentService.getDocumentsEnAttente();
            tableDocuments.getItems().addAll(documentsEnAttente);
            
            Button btnRefresh = new Button("Actualiser");
            btnRefresh.setOnAction(e -> {
                tableDocuments.getItems().clear();
                tableDocuments.getItems().addAll(documentService.getDocumentsEnAttente());
            });
            
            root.getChildren().addAll(title, tableDocuments, btnRefresh);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    private void validerDocument(Document document) {
        documentService.validerDocument(document.getId());
        showAlert("Succès", "Document validé avec succès", Alert.AlertType.INFORMATION);
    }
    
    private void rejeterDocument(Document document) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Rejeter le document");
        alert.setContentText("Êtes-vous sûr de vouloir rejeter ce document ?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            documentService.rejeterDocument(document.getId());
            showAlert("Succès", "Document rejeté et supprimé", Alert.AlertType.INFORMATION);
        }
    }
    
    public void showMesDocuments() {
        if (utilisateurConnecte instanceof DemandeurEmploi) {
            Stage stage = new Stage();
            stage.setTitle("Mes documents");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            attachStylesheets(root);
            Label title = new Label("Mes documents");
            title.getStyleClass().add("mc-style-22");
            
            TableView<Document> tableDocuments = new TableView<>();
            
            TableColumn<Document, String> colNomFichier = new TableColumn<>("Nom du fichier");
            colNomFichier.setCellValueFactory(new PropertyValueFactory<>("nomFichier"));
            
            TableColumn<Document, String> colTypeDocument = new TableColumn<>("Type");
            colTypeDocument.setCellValueFactory(new PropertyValueFactory<>("typeDocument"));
            
            TableColumn<Document, String> colStatut = new TableColumn<>("Statut");
            colStatut.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().isValide() ? "Validé" : "En attente"
                )
            );
            
            TableColumn<Document, String> colDateUpload = new TableColumn<>("Date upload");
            colDateUpload.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateUpload().toLocalDate().toString()
                )
            );
            
            tableDocuments.getColumns().addAll(colNomFichier, colTypeDocument, colStatut, colDateUpload);
            
            // Charger les documents du demandeur
            List<Document> mesDocuments = documentService.getDocumentsParDemandeur(
                ((DemandeurEmploi) utilisateurConnecte).getIdUtilisateur()
            );
            tableDocuments.getItems().addAll(mesDocuments);
            
            Button btnUploadCV = new Button("Téléverser un CV");
            btnUploadCV.setOnAction(e -> handleUploadCV());
            
            root.getChildren().addAll(title, tableDocuments, btnUploadCV);
            
            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    private Stage getCurrentStage() {
        // Trouver la fenêtre actuelle
        return javafx.stage.Stage.getWindows().stream()
                .filter(window -> window.isShowing())
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);
    }
    
    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        final String finalContentType = contentType;
        
        return new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }
            
            @Override
            public String getOriginalFilename() {
                return file.getName();
            }
            
            @Override
            public String getContentType() {
                return finalContentType;
            }
            
            @Override
            public boolean isEmpty() {
                return fileContent.length == 0;
            }
            
            @Override
            public long getSize() {
                return fileContent.length;
            }
            
            @Override
            public byte[] getBytes() throws IOException {
                return fileContent;
            }
            
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                return new java.io.ByteArrayInputStream(fileContent);
            }
            
            @Override
            public void transferTo(java.io.File dest) throws IOException {
                Files.write(dest.toPath(), fileContent);
            }
        };
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========== GESTION ADMIN - RAPPORTS RECRUTEMENT ==========
    
 public void showRapportsRecrutement() {
    if (!(utilisateurConnecte instanceof Administrateur)) return;

    Stage stage = new Stage();
    stage.setTitle("Rapports de Recrutement");

    VBox content = new VBox(16);
    content.setPadding(new Insets(20));
    content.setFillWidth(true);
    attachStylesheets(content);

    // ===== Header =====
    Label titleLabel = new Label("Tableau de Bord - Recrutements et Offres");
    titleLabel.getStyleClass().add("report-title");

    Label subtitle = new Label("Analyse globale des recrutements, offres et candidatures par entreprise.");
    subtitle.getStyleClass().add("muted");

    VBox header = new VBox(6, titleLabel, subtitle);
    header.getStyleClass().add("header-box");

    // ===== Stats =====
    long totalOffres = offreRepository.count();
    long offresActives = offreRepository.findByEtat(Offre.EtatOffre.ACTIVE).size();
    long totalRecrutements = recrutementRepository.count();
    long recrutementsActifs = recrutementRepository.findAll().stream()
            .filter(r -> r.getDateRecrutement() != null)
            .count();

    HBox statsRow = new HBox(12);
    statsRow.getStyleClass().add("report-stats-row");

    statsRow.getChildren().addAll(
            createStatCard("Total Offres", String.valueOf(totalOffres), null),
            createStatCard("Offres Actives", String.valueOf(offresActives), null),
            createStatCard("Total Recrutements", String.valueOf(totalRecrutements), null),
            createStatCard("Recrutements Actifs", String.valueOf(recrutementsActifs), null)
    );

    // ===== Section 1: Recrutements =====
    Label recrutementsTitle = new Label("Recrutements en cours");
    recrutementsTitle.getStyleClass().add("report-section-title");

    TableView<Recrutement> tableRecrutements = new TableView<>();
    tableRecrutements.getStyleClass().add("report-table");
    tableRecrutements.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    tableRecrutements.setPrefHeight(260);
    tableRecrutements.setMaxHeight(320);

    TableColumn<Recrutement, String> colOffre = new TableColumn<>("Offre");
    colOffre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getOffre() != null ? cellData.getValue().getOffre().getTitre() : ""
            )
    );

    TableColumn<Recrutement, String> colEntreprise = new TableColumn<>("Entreprise");
    colEntreprise.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEntreprise() != null ? cellData.getValue().getEntreprise().getRaisonSociale() : ""
            )
    );

    TableColumn<Recrutement, String> colCandidat = new TableColumn<>("Candidat recruté");
    colCandidat.setCellValueFactory(cellData -> {
        var d = cellData.getValue().getDemandeur();
        String nom = (d != null) ? (d.getNom() + " " + d.getPrenom()) : "";
        return new javafx.beans.property.SimpleStringProperty(nom);
    });

    TableColumn<Recrutement, String> colDate = new TableColumn<>("Date");
    colDate.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateRecrutement() != null ? cellData.getValue().getDateRecrutement().toString() : ""
            )
    );

    tableRecrutements.getColumns().addAll(colOffre, colEntreprise, colCandidat, colDate);

    List<Recrutement> recrutements = recrutementRepository.findAllWithRelations();
    tableRecrutements.getItems().setAll(recrutements);

    VBox recrutementsCard = new VBox(10, recrutementsTitle, tableRecrutements);
    recrutementsCard.getStyleClass().add("card");

    // ===== Section 2: Offres =====
    Label offresTitle = new Label("Offres publiées par entreprise");
    offresTitle.getStyleClass().add("report-section-title");

    TableView<Offre> tableOffres = new TableView<>();
    tableOffres.getStyleClass().add("report-table");
    tableOffres.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    tableOffres.setPrefHeight(260);
    tableOffres.setMaxHeight(320);

    TableColumn<Offre, String> colTitreOffre = new TableColumn<>("Titre");
    colTitreOffre.setCellValueFactory(new PropertyValueFactory<>("titre"));

    TableColumn<Offre, String> colNomEntreprise = new TableColumn<>("Entreprise");
    colNomEntreprise.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEntreprise() != null ? cellData.getValue().getEntreprise().getRaisonSociale() : ""
            )
    );

    TableColumn<Offre, String> colDatePublication = new TableColumn<>("Publication");
    colDatePublication.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty("N/A")
    );

    TableColumn<Offre, String> colEtatOffre = new TableColumn<>("État");
    colEtatOffre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEtat() != null ? cellData.getValue().getEtat().toString() : ""
            )
    );

    TableColumn<Offre, Integer> colNbCandidats = new TableColumn<>("Candidats");
    colNbCandidats.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(
                    (int) candidatureRepository.countByOffre(cellData.getValue().getIdOffre())
            ).asObject()
    );

    tableOffres.getColumns().addAll(
            colTitreOffre, colNomEntreprise, colDatePublication, colEtatOffre, colNbCandidats
    );

    List<Offre> offres = offreRepository.findAllWithEntreprise();
    tableOffres.getItems().setAll(offres);

    VBox offresCard = new VBox(10, offresTitle, tableOffres);
    offresCard.getStyleClass().add("card");

    // ===== Actions =====
    Button btnRefresh = new Button("🔄 Actualiser");
    btnRefresh.getStyleClass().add("btn-secondary");

    Button btnDownloadRapport = new Button("📊 Télécharger le Rapport (Image)");
    btnDownloadRapport.getStyleClass().add("btn-secondary-strong");

    btnDownloadRapport.setOnAction(e -> generateAndDownloadRapportImage(recrutements, offres));
    btnRefresh.setOnAction(e -> {
        tableRecrutements.getItems().setAll(recrutementRepository.findAllWithRelations());
        tableOffres.getItems().setAll(offreRepository.findAllWithEntreprise());
    });

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox actions = new HBox(12, spacer, btnRefresh, btnDownloadRapport);
    actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    actions.getStyleClass().add("report-actions");

    content.getChildren().addAll(header, statsRow, recrutementsCard, offresCard, actions);

    // ===== Scroll global + taille adaptative écran =====
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setFitToHeight(false); // ✅ IMPORTANT
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scroll.setPannable(true);
    attachStylesheets(scroll);

    javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();

    double w = Math.min(1100, screen.getWidth() - 80);
    double h = Math.min(750, screen.getHeight() - 80);

    Scene scene = new Scene(scroll, w, h);
    stage.setScene(scene);
    stage.show();
}


    
   private VBox createStatCard(String title, String value, String colorIgnored) {
    VBox card = new VBox(6);
    card.setPadding(new Insets(14));
    card.setMinWidth(180);
    card.getStyleClass().add("stat-card");

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("stat-card-title");

    Label valueLabel = new Label(value);
    valueLabel.getStyleClass().add("stat-card-value");

    card.getChildren().addAll(titleLabel, valueLabel);
    return card;
}

    
    private String getStatutCandidatureDisplay(Candidature.StatutCandidature statut) {
        switch (statut) {
            case EN_ATTENTE:
                return "En attente";
            case APPROUVEE:
                return "Approuvée";
            case REJETEE:
                return "Rejetée";
            case RECRUTEE:
                return "Recrutée";
            default:
                return statut.toString();
        }
    }
    
    private String getStatutStyle(Candidature.StatutCandidature statut) {
        switch (statut) {
            case EN_ATTENTE:
                return "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-padding: 4px 8px; -fx-background-radius: 4px;";
            case APPROUVEE:
                return "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-padding: 4px 8px; -fx-background-radius: 4px;";
            case REJETEE:
                return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; -fx-padding: 4px 8px; -fx-background-radius: 4px;";
            case RECRUTEE:
                return "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-font-weight: bold; -fx-padding: 4px 8px; -fx-background-radius: 4px;";
            default:
                return "-fx-background-color: #e2e3e5; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 4px 8px; -fx-background-radius: 4px;";
        }
    }
    
    private void showDetailsCandidature(Candidature candidature) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Détails de la candidature");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.getStyleClass().add("mc-style-14");
            
            Label titleLabel = new Label("Détails de la candidature");
            titleLabel.getStyleClass().add("mc-style-23");
            
            // Informations de l'offre
            VBox offreInfo = new VBox(10);
            offreInfo.getStyleClass().add("mc-style-16");
            
            offreInfo.getChildren().addAll(
                new Label("📋 Offre: " + candidature.getOffre().getTitre()),
                new Label("🏢 Entreprise: " + candidature.getOffre().getEntreprise().getRaisonSociale()),
                new Label("📅 Date de candidature: " + candidature.getDateCandidature().toString()),
                new Label("📊 Statut actuel: " + getStatutCandidatureDisplay(candidature.getStatut()))
            );
            
            // Boutons d'action
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            Button btnClose = new Button("Fermer");
            btnClose.getStyleClass().add("mc-style-06");
            btnClose.setOnAction(e -> stage.close());
            
            buttonBox.getChildren().add(btnClose);
            
            root.getChildren().addAll(titleLabel, offreInfo, buttonBox);
            
            Scene scene = new Scene(root, 500, 300);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'afficher les détails de la candidature: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void marquerCandidaturesCommeLues(List<Candidature> candidatures) {
        try {
            for (Candidature candidature : candidatures) {
                candidature.setNotifiee(true);
                candidatureService.updateCandidature(candidature);
            }
            
            showAlert("Succès", "Les " + candidatures.size() + " candidature(s) ont été marquées comme lues.", Alert.AlertType.INFORMATION);
            
            // Rafraîchir l'interface
            // Note: Dans une vraie application, vous pourriez avoir besoin de rafraîchir le tableau
            // ou d'utiliser un système de notification plus avancé
            
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de marquer les candidatures comme lues: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void generateAndDownloadRapportImage(List<Recrutement> recrutements, List<Offre> offres) {
        try {
            // Créer une image du rapport
            int width = 1200;
            int height = 800;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Activer l'anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Fond blanc
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Couleurs
            Color primaryColor = new Color(52, 73, 94); // #34495e
            Color accentColor = new Color(52, 152, 219); // #3498db
            Color successColor = new Color(46, 204, 113); // #2ecc71
            Color warningColor = new Color(241, 196, 15); // #f1c40f
            
            // En-tête
            g2d.setColor(primaryColor);
            g2d.fillRect(0, 0, width, 60);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("RAPPORT DE RECRUTEMENT", 50, 40);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Généré le: " + java.time.LocalDate.now().toString(), width - 200, 40);
            
            int yPosition = 80;
            
            // Section Statistiques
            g2d.setColor(accentColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("STATISTIQUES GLOBALES", 50, yPosition);
            
            yPosition += 30;
            
            // Cartes de statistiques
            int cardX = 50;
            int cardY = yPosition;
            int cardWidth = 250;
            int cardHeight = 80;
            int cardSpacing = 20;
            
            // Carte 1: Total Offres
            drawStatCard(g2d, "Total Offres", String.valueOf(offres.size()), 
                        cardX, cardY, cardWidth, cardHeight, accentColor);
            
            // Carte 2: Offres Actives
            drawStatCard(g2d, "Offres Actives", String.valueOf(offres.stream().filter(o -> o.getEtat() == Offre.EtatOffre.ACTIVE).count()), 
                        cardX + cardWidth + cardSpacing, cardY, cardWidth, cardHeight, successColor);
            
            // Carte 3: Total Recrutements
            drawStatCard(g2d, "Total Recrutements", String.valueOf(recrutements.size()), 
                        cardX + 2 * (cardWidth + cardSpacing), cardY, cardWidth, cardHeight, warningColor);
            
            // Carte 4: Recrutements Actifs
            drawStatCard(g2d, "Recrutements Actifs", String.valueOf(recrutements.stream().filter(r -> r.getDateRecrutement() != null).count()), 
                        cardX + 3 * (cardWidth + cardSpacing), cardY, cardWidth, cardHeight, successColor);
            
            yPosition += cardHeight + 50;
            
            // Section Recrutements
            g2d.setColor(accentColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("RECRUTEMENTS RÉCENTS", 50, yPosition);
            
            yPosition += 30;
            
            // En-tête du tableau des recrutements
            String[] headers = {"Offre", "Entreprise", "Candidat", "Date", "Statut"};
            int[] colWidths = {200, 150, 180, 120, 100};
            int tableX = 50;
            
            g2d.setColor(primaryColor);
            g2d.fillRect(tableX, yPosition, width - 100, 25);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            int headerX = tableX + 10;
            for (int i = 0; i < headers.length; i++) {
                g2d.drawString(headers[i], headerX, yPosition + 17);
                headerX += colWidths[i];
            }
            
            yPosition += 25;
            
            // Données du tableau (limité à 10 recrutements)
            int maxRows = Math.min(10, recrutements.size());
            for (int i = 0; i < maxRows; i++) {
                Recrutement r = recrutements.get(i);
                
                // Ligne alternée
                if (i % 2 == 0) {
                    g2d.setColor(new Color(248, 249, 250)); // #f8f9fa
                    g2d.fillRect(tableX, yPosition, width - 100, 22);
                }
                
                g2d.setColor(primaryColor);
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                
                int dataX = tableX + 10;
                g2d.drawString(truncateString(r.getOffre().getTitre(), 25), dataX, yPosition + 15);
                dataX += colWidths[0];
                g2d.drawString(truncateString(r.getEntreprise().getRaisonSociale(), 18), dataX, yPosition + 15);
                dataX += colWidths[1];
                g2d.drawString(truncateString(r.getDemandeur().getNom() + " " + r.getDemandeur().getPrenom(), 22), dataX, yPosition + 15);
                dataX += colWidths[2];
                g2d.drawString(r.getDateRecrutement().toString(), dataX, yPosition + 15);
                dataX += colWidths[3];
                
                yPosition += 22;
            }
            
            yPosition += 40;
            
            // Section Offres
            g2d.setColor(accentColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("OFFRES PUBLIÉES RÉCENTES", 50, yPosition);
            
            yPosition += 30;
            
            // En-tête du tableau des offres
            String[] offreHeaders = {"Titre", "Entreprise", "Date Pub", "État", "Candidats"};
            int[] offreColWidths = {200, 150, 120, 100, 80};
            
            g2d.setColor(primaryColor);
            g2d.fillRect(tableX, yPosition, width - 100, 25);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            headerX = tableX + 10;
            for (int i = 0; i < offreHeaders.length; i++) {
                g2d.drawString(offreHeaders[i], headerX, yPosition + 17);
                headerX += offreColWidths[i];
            }
            
            yPosition += 25;
            
            // Données du tableau des offres (limité à 10 offres)
            maxRows = Math.min(10, offres.size());
            for (int i = 0; i < maxRows; i++) {
                Offre o = offres.get(i);
                
                // Ligne alternée
                if (i % 2 == 0) {
                    g2d.setColor(new Color(248, 249, 250)); // #f8f9fa
                    g2d.fillRect(tableX, yPosition, width - 100, 22);
                }
                
                g2d.setColor(primaryColor);
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                
                int dataX = tableX + 10;
                g2d.drawString(truncateString(o.getTitre(), 25), dataX, yPosition + 15);
                dataX += offreColWidths[0];
                g2d.drawString(truncateString(o.getEntreprise().getRaisonSociale(), 18), dataX, yPosition + 15);
                dataX += offreColWidths[1];
                g2d.drawString("N/A", dataX, yPosition + 15);
                dataX += offreColWidths[2];
                g2d.drawString(o.getEtat().toString(), dataX, yPosition + 15);
                dataX += offreColWidths[3];
                g2d.drawString(String.valueOf((int) candidatureRepository.countByOffre(o.getIdOffre())), dataX, yPosition + 15);
                
                yPosition += 22;
            }
            
            // Pied de page
            yPosition = height - 40;
            g2d.setColor(accentColor);
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("Rapport généré par l'Agence de Recrutement - " + java.time.LocalDate.now().toString(), 50, yPosition);
            
            g2d.dispose();
            
            // Sauvegarder l'image
            String userHome = System.getProperty("user.home");
            File downloadDir = new File(userHome, "Downloads");
            System.out.println("Dossier de téléchargement: " + downloadDir.getAbsolutePath());
            
            if (!downloadDir.exists()) {
                boolean created = downloadDir.mkdirs();
                System.out.println("Dossier Downloads créé: " + created);
            }
            
            // Utiliser un nom de fichier valide (remplacer les caractères problématiques)
            String today = java.time.LocalDate.now().toString().replace("-", "_");
            String fileName = "rapport_recrutement_" + today + ".png";
            System.out.println("Nom du fichier: " + fileName);
            
            File rapportFile = new File(downloadDir, fileName);
            System.out.println("Chemin complet du fichier: " + rapportFile.getAbsolutePath());
            
            try {
                javax.imageio.ImageIO.write(image, "PNG", rapportFile);
                System.out.println("Image écrite avec succès");
                
                // Vérifier que le fichier existe et n'est pas vide
                if (rapportFile.exists() && rapportFile.length() > 0) {
                    System.out.println("Fichier vérifié - Taille: " + rapportFile.length() + " bytes");
                    showAlert("Succès", "Rapport téléchargé avec succès dans:\n" + rapportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
                    
                    // Ouvrir le dossier Downloads
                    try {
                        java.awt.Desktop.getDesktop().open(downloadDir);
                        System.out.println("Dossier Downloads ouvert avec succès");
                    } catch (Exception openEx) {
                        System.err.println("Erreur lors de l'ouverture du dossier: " + openEx.getMessage());
                        showAlert("Information", "Rapport téléchargé avec succès dans:\n" + rapportFile.getAbsolutePath() + "\n(Impossible d'ouvrir automatiquement le dossier)", Alert.AlertType.INFORMATION);
                    }
                } else {
                    throw new IOException("Le fichier n'a pas pu être créé ou est vide");
                }
            } catch (java.io.IOException ioEx) {
                System.err.println("Erreur IO lors de l'écriture du fichier: " + ioEx.getMessage());
                ioEx.printStackTrace();
                throw new IOException("Impossible d'écrire le fichier rapport: " + ioEx.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du rapport: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le rapport: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void drawStatCard(Graphics2D g2d, String title, String value, int x, int y, int width, int height, Color color) {
        // Fond de la carte
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, width, height);
        
        // Bordure
        g2d.setColor(color);
        g2d.fillRect(x, y, width, 3); // Top border
        g2d.fillRect(x, y, 3, height); // Left border
        g2d.fillRect(x + width - 3, y, 3, height); // Right border
        g2d.fillRect(x, y + height - 3, width, 3); // Bottom border
        
        // Texte
        g2d.setColor(new Color(127, 140, 141)); // #7f8c8d
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(title, x + 10, y + 25);
        
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(value, x + 10, y + 55);
    }
    
    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private void telechargerCVDemandeur(DemandeurEmploi demandeur) {
        try {
            System.out.println("Téléchargement du CV pour le demandeur ID: " + demandeur.getIdUtilisateur());
            
            List<Document> documents = documentService.getDocumentsParDemandeur(demandeur.getIdUtilisateur());
            System.out.println("Nombre de documents trouvés: " + documents.size());
            
            if (documents.isEmpty()) {
                showAlert("Information", "Aucun CV trouvé pour ce demandeur", Alert.AlertType.INFORMATION);
                return;
            }
            
            // Prendre le premier document (normalement le CV)
            Document cvDocument = documents.get(0);
            System.out.println("Document trouvé - ID: " + cvDocument.getId() + ", Nom: " + cvDocument.getNomFichier());
            
            // Télécharger le contenu du document
            byte[] cvContent = documentService.downloadDocument(cvDocument.getId());
            System.out.println("Taille du contenu téléchargé: " + (cvContent != null ? cvContent.length : 0) + " bytes");
            
            if (cvContent != null && cvContent.length > 0) {
                try {
                    // Créer le dossier Downloads s'il n'existe pas
                    String userHome = System.getProperty("user.home");
                    File downloadDir = new File(userHome, "Downloads");
                    System.out.println("Dossier de téléchargement: " + downloadDir.getAbsolutePath());
                    
                    if (!downloadDir.exists()) {
                        boolean created = downloadDir.mkdirs();
                        System.out.println("Dossier créé: " + created);
                    }
                    
                    // Écrire le fichier
                    File cvFile = new File(downloadDir, cvDocument.getNomFichier());
                    System.out.println("Écriture du fichier vers: " + cvFile.getAbsolutePath());
                    
                    java.nio.file.Files.write(cvFile.toPath(), cvContent);
                    System.out.println("Fichier écrit avec succès");
                    
                    // Vérifier que le fichier existe
                    if (cvFile.exists() && cvFile.length() > 0) {
                        String fileType = cvDocument.getNomFichier().toLowerCase().endsWith(".png") ? "Image PNG" : "CV";
                        showAlert("Information", fileType + " téléchargé avec succès dans: " + cvFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
                        
                        // Ouvrir le dossier Downloads
                        try {
                            java.awt.Desktop.getDesktop().open(downloadDir);
                            System.out.println("Dossier Downloads ouvert avec succès");
                        } catch (Exception openEx) {
                            System.err.println("Erreur lors de l'ouverture du dossier: " + openEx.getMessage());
                            showAlert("Information", fileType + " téléchargé avec succès dans: " + cvFile.getAbsolutePath() + "\n(Impossible d'ouvrir automatiquement le dossier)", Alert.AlertType.INFORMATION);
                        }
                    } else {
                        throw new IOException("Le fichier n'a pas pu être créé ou est vide");
                    }
                    
                } catch (IOException ioEx) {
                    System.err.println("Erreur IO lors du téléchargement: " + ioEx.getMessage());
                    ioEx.printStackTrace();
                    showAlert("Erreur", "Erreur lors de l'écriture du fichier: " + ioEx.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Erreur", "Le contenu du CV est vide ou null.", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur générale lors du téléchargement du CV: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de télécharger le CV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void remplacerCVDemandeur(DemandeurEmploi demandeur, File newCVFile) {
        try {
            System.out.println("Début du remplacement de CV pour le demandeur ID: " + demandeur.getIdUtilisateur());
            System.out.println("Nouveau fichier CV: " + (newCVFile != null ? newCVFile.getName() : "null"));
            
            // Vérifier si le demandeur a déjà un CV
            Document cvActuel = documentService.getCvValideParDemandeur(demandeur.getIdUtilisateur());
            
            if (cvActuel != null) {
                System.out.println("Ancien CV trouvé - ID: " + cvActuel.getId());
                // Supprimer l'ancien CV
                documentService.rejeterDocument(cvActuel.getId());
                System.out.println("Ancien CV supprimé avec succès");
            } else {
                System.out.println("Aucun ancien CV trouvé pour ce demandeur");
            }
            
            // Convertir le fichier en MultipartFile
            MultipartFile multipartFile = convertFileToMultipartFile(newCVFile);
            System.out.println("Fichier converti en MultipartFile avec succès");
            
            // Créer le nouveau CV
            Document nouveauCV = documentService.uploadDocument(
                multipartFile, 
                demandeur, 
                "CV"
            );
            
            System.out.println("Nouveau CV créé avec ID: " + nouveauCV.getId());
            
            showAlert("Succès", "Votre CV a été remplacé avec succès!", Alert.AlertType.INFORMATION);
            
        } catch (IOException e) {
            System.err.println("Erreur IOException lors du remplacement: " + e.getMessage());
            showAlert("Erreur", "Impossible de remplacer le CV: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.err.println("Erreur Exception lors du remplacement: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du remplacement du CV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // ========== GESTION ENTREPRISE - CANDIDATS ==========
    
    public void showCandidatsPourRecrutement() {
        if (utilisateurConnecte instanceof Entreprise) {
            Stage stage = new Stage();
            stage.setTitle("Candidats disponibles pour recrutement");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            
            Label title = new Label("Candidats avec CV validés");
            title.getStyleClass().add("mc-style-22");
            
            // TableView pour afficher les candidats
            TableView<Document> tableCandidats = new TableView<>();
            
            TableColumn<Document, String> colNomCandidat = new TableColumn<>("Nom du candidat");
            colNomCandidat.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getNom() + " " + 
                    cellData.getValue().getDemandeur().getPrenom()
                )
            );
            
            TableColumn<Document, String> colEmail = new TableColumn<>("Email");
            colEmail.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getLogin()
                )
            );
            
            TableColumn<Document, String> colDiplome = new TableColumn<>("Diplôme");
            colDiplome.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getDiplome()
                )
            );
            
            TableColumn<Document, String> colExperience = new TableColumn<>("Expérience");
            colExperience.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getExperience() + " an(s)"
                )
            );
            
            TableColumn<Document, String> colSalaire = new TableColumn<>("Salaire souhaité");
            colSalaire.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getSalaireSouhaite() + " €"
                )
            );
            
            // Colonne CV séparée pour plus de visibilité
            TableColumn<Document, String> colCV = new TableColumn<>("CV");
            colCV.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNomFichier()
                )
            );
            colCV.setPrefWidth(200);
            
            TableColumn<Document, Void> colActions = new TableColumn<>("Actions");
            colActions.setCellFactory(param -> new TableCell<>() {
                private final Button btnTelechargerCV = new Button("Télécharger CV");
                private final HBox actionsBox = new HBox(5, btnTelechargerCV);
                
                {
                    btnTelechargerCV.getStyleClass().add("mc-style-05");
                    
                    btnTelechargerCV.setOnAction(event -> {
                        Document doc = getTableView().getItems().get(getIndex());
                        telechargerCVDemandeur(doc.getDemandeur());
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionsBox);
                    }
                }
            });
            colActions.setPrefWidth(200);
            
            tableCandidats.getColumns().addAll(colNomCandidat, colEmail, colDiplome, colExperience, colSalaire, colCV, colActions);
            
            // Charger les candidats avec CV validés
            List<Document> candidatsAvecCV = documentService.getCandidatsAvecCVValides();
            tableCandidats.getItems().addAll(candidatsAvecCV);
            
            Button btnRefresh = new Button("Actualiser");
            btnRefresh.setOnAction(e -> {
                tableCandidats.getItems().clear();
                tableCandidats.getItems().addAll(documentService.getCandidatsAvecCVValides());
            });
            
            root.getChildren().addAll(title, tableCandidats, btnRefresh);
            
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    private void voirCV(Document document) {
        try {
            File cvFile = documentService.getDocumentFile(document.getId());
            
            // Ouvrir le fichier avec le programme par défaut du système
            java.awt.Desktop.getDesktop().open(cvFile);
            
            showAlert("Information", "CV ouvert avec succès", Alert.AlertType.INFORMATION);
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le CV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void recruterCandidat(Document document, Stage parentStage) {
        DemandeurEmploi candidat = document.getDemandeur();
        Entreprise entreprise = (Entreprise) utilisateurConnecte;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de recrutement");
        alert.setHeaderText("Recruter ce candidat ?");
        alert.setContentText(
            "Êtes-vous sûr de vouloir recruter " + candidat.getNom() + " " + candidat.getPrenom() + " ?\n\n" +
            "Diplôme: " + candidat.getDiplome() + "\n" +
            "Expérience: " + candidat.getExperience() + " an(s)\n" +
            "Salaire souhaité: " + candidat.getSalaireSouhaite() + " €"
        );
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Créer un enregistrement de recrutement
                Recrutement recrutement = new Recrutement();
                recrutement.setEntreprise(entreprise);
                recrutement.setDemandeur(candidat);
                recrutement.setDateRecrutement(LocalDate.now());
                
                recrutementRepository.save(recrutement);
                
                showAlert("Succès", 
                    "Candidat recruté avec succès !\n" +
                    "Un email de confirmation sera envoyé à " + candidat.getLogin(), 
                    Alert.AlertType.INFORMATION);
                
                // Optionnel: fermer la fenêtre des candidats
                parentStage.close();
                
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors du recrutement: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    public void showMesRecrutements() {
        if (utilisateurConnecte instanceof Entreprise) {
            Stage stage = new Stage();
            stage.setTitle("Mes recrutements");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            
            Label title = new Label("Historique des recrutements");
            title.getStyleClass().add("mc-style-22");
            
            // TableView pour afficher les recrutements
            TableView<Recrutement> tableRecrutements = new TableView<>();
            
            TableColumn<Recrutement, String> colNomCandidat = new TableColumn<>("Nom du candidat");
            colNomCandidat.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getNom() + " " + 
                    cellData.getValue().getDemandeur().getPrenom()
                )
            );
            
            TableColumn<Recrutement, String> colEmail = new TableColumn<>("Email");
            colEmail.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDemandeur().getLogin()
                )
            );
            
            TableColumn<Recrutement, String> colDateRecrutement = new TableColumn<>("Date de recrutement");
            colDateRecrutement.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateRecrutement().toString()
                )
            );
            
            TableColumn<Recrutement, String> colOffre = new TableColumn<>("Offre associée");
            colOffre.setCellValueFactory(cellData -> {
                Offre offre = cellData.getValue().getOffre();
                return new javafx.beans.property.SimpleStringProperty(
                    offre != null ? offre.getTitre() : "Recrutement direct"
                );
            });
            
            tableRecrutements.getColumns().addAll(colNomCandidat, colEmail, colDateRecrutement, colOffre);
            
            // Charger les recrutements de l'entreprise
            List<Recrutement> mesRecrutements = recrutementRepository.findByEntrepriseIdUtilisateur(
                ((Entreprise) utilisateurConnecte).getIdUtilisateur()
            );
            tableRecrutements.getItems().addAll(mesRecrutements);
            
            Button btnRefresh = new Button("Actualiser");
            btnRefresh.setOnAction(e -> {
                tableRecrutements.getItems().clear();
                List<Recrutement> recrutements = recrutementRepository.findByEntrepriseIdUtilisateur(
                    ((Entreprise) utilisateurConnecte).getIdUtilisateur()
                );
                tableRecrutements.getItems().addAll(recrutements);
            });
            
            root.getChildren().addAll(title, tableRecrutements, btnRefresh);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    // === ADDED: enable internal TableView scroll (NO existing code modified) ===
    private void enableTableScroll(javafx.scene.control.TableView<?> table) {
        table.setMinHeight(0);
        table.setPrefHeight(400); // visible area
        table.setMaxHeight(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
    }


    // === ADDED: keep action button visible (footer) ===
    private BorderPane wrapTableWithFooter(javafx.scene.control.TableView<?> table, javafx.scene.control.Button actionButton) {
        BorderPane root = new BorderPane();
        root.setCenter(table);

        javafx.scene.layout.HBox footer = new javafx.scene.layout.HBox(actionButton);
        footer.setPadding(new javafx.geometry.Insets(12));
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        root.setBottom(footer);
        return root;
    }

}