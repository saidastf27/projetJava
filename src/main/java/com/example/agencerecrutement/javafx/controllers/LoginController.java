package com.example.agencerecrutement.javafx.controllers;

import com.example.agencerecrutement.javafx.dialogs.InscriptionDialog;
import com.example.agencerecrutement.javafx.dialogs.MotDePasseOublieDialog;
import com.example.agencerecrutement.model.Utilisateur;
import com.example.agencerecrutement.repository.UtilisateurRepository;
import com.example.agencerecrutement.service.AuthentificationService;
import com.example.agencerecrutement.service.DemandeurEmploiService;
import com.example.agencerecrutement.service.DocumentService;
import com.example.agencerecrutement.service.EntrepriseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoginController {

    private final AuthentificationService authentificationService;
    private final EntrepriseService entrepriseService;
    private final DemandeurEmploiService demandeurEmploiService;
    private final DocumentService documentService;
    private final UtilisateurRepository utilisateurRepository;

    private ConfigurableApplicationContext applicationContext;
    private Utilisateur utilisateurConnecte;

    private VBox root;
    private ComboBox<String> loginCombo;
    private PasswordField passwordField;
    private Label errorLabel;

    private ObservableList<String> loginSuggestions;

    // ✅ Constructeur uniquement pour l’injection
    public LoginController(AuthentificationService authentificationService,
                           EntrepriseService entrepriseService,
                           DemandeurEmploiService demandeurEmploiService,
                           DocumentService documentService,
                           UtilisateurRepository utilisateurRepository) {
        this.authentificationService = authentificationService;
        this.entrepriseService = entrepriseService;
        this.demandeurEmploiService = demandeurEmploiService;
        this.documentService = documentService;
        this.utilisateurRepository = utilisateurRepository;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // ✅ Initialisation après que Spring ait injecté tous les beans
    @PostConstruct
    public void init() {
        initializeView();
    }

    private void initializeView() {
        root = new VBox(14);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        Label titleLabel = new Label("Agence de Recrutement");
        titleLabel.getStyleClass().add("title");

        loadLoginSuggestions();

        loginCombo = new ComboBox<>(loginSuggestions);
        loginCombo.setEditable(true);
        loginCombo.setPromptText("Login");
        loginCombo.setVisibleRowCount(5);

        loginCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                loginCombo.setItems(loginSuggestions);
            } else {
                loginCombo.setItems(FXCollections.observableArrayList(
                        loginSuggestions.stream()
                                .filter(l -> l.toLowerCase().startsWith(newVal.toLowerCase()))
                                .collect(Collectors.toList())
                ));
            }
        });

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });

        Button loginButton = new Button("Se connecter");
        loginButton.getStyleClass().add("button-login");
        loginButton.setOnAction(e -> handleLogin());

        Button inscriptionButton = new Button("S'inscrire");
        inscriptionButton.getStyleClass().add("button-register");
        inscriptionButton.setOnAction(e -> handleInscription());

        Button forgotButton = new Button("Mot de passe oublié");
        forgotButton.getStyleClass().add("button-forgot");
        forgotButton.setOnAction(e -> handleMotDePasseOublie());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("label-error");
        errorLabel.setVisible(false);

        root.getChildren().addAll(
                titleLabel,
                loginCombo,
                passwordField,
                loginButton,
                inscriptionButton,
                forgotButton,
                errorLabel
        );
    }

    private void handleLogin() {
        String login = loginCombo.getEditor().getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            utilisateurConnecte = authentificationService.authentifier(login, password);

            MainController mainController = applicationContext.getBean(MainController.class);
            mainController.setUtilisateurConnecte(utilisateurConnecte);
            mainController.setApplicationContext(applicationContext);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(mainController.getView(), 1200, 800));
            stage.setTitle("Agence de Recrutement - Tableau de bord");
            stage.setMaximized(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void handleInscription() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Entreprise", "Entreprise", "Demandeur d'emploi");
        dialog.setTitle("Inscription");
        dialog.setHeaderText("Type de compte");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/InscriptionDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("button");

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel-button");

        dialog.showAndWait().ifPresent(type -> {
            boolean isEntreprise = type.equals("Entreprise");
            InscriptionDialog inscriptionDialog = new InscriptionDialog(
                    entrepriseService, demandeurEmploiService, isEntreprise);

            inscriptionDialog.showAndWait().ifPresent(result -> {
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Inscription réussie. Vous pouvez maintenant vous connecter.");
            });
        });
    }

    private void handleMotDePasseOublie() {
        new MotDePasseOublieDialog().showAndWait();
    }

    private void loadLoginSuggestions() {
        try {
            List<String> logins = utilisateurRepository.findAll()
                    .stream()
                    .map(Utilisateur::getLogin)
                    .collect(Collectors.toList());
            loginSuggestions = FXCollections.observableArrayList(logins);
        } catch (Exception e) {
            loginSuggestions = FXCollections.observableArrayList();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/styles/InscriptionDialog.css").toExternalForm());
        pane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    public VBox getView() {
        return root;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void reset() {
        utilisateurConnecte = null;
        loadLoginSuggestions();
        if (loginCombo != null) loginCombo.getEditor().clear();
        if (passwordField != null) passwordField.clear();
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }
}
