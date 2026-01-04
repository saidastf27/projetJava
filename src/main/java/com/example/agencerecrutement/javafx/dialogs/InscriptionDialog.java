package com.example.agencerecrutement.javafx.dialogs;

import com.example.agencerecrutement.service.DemandeurEmploiService;
import com.example.agencerecrutement.service.EntrepriseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

import java.io.File;

public class InscriptionDialog extends Dialog<Object> {

    private final EntrepriseService entrepriseService;
    private final DemandeurEmploiService demandeurEmploiService;
    private final boolean isEntreprise;

    private TextField loginField;
    private PasswordField passwordField;

    private TextField raisonSocialeField;
    private TextField adresseField;
    private TextField telephoneField;
    private TextArea descriptionArea;

    private TextField nomField;
    private TextField prenomField;
    private TextField faxField;
    private TextField diplomeField;
    private Spinner<Integer> experienceSpinner;
    private Spinner<Double> salaireSpinner;
    private Label cvPathLabel;
    private File selectedCVFile;

    public InscriptionDialog(EntrepriseService entrepriseService,
                             DemandeurEmploiService demandeurEmploiService,
                             boolean isEntreprise) {

        this.entrepriseService = entrepriseService;
        this.demandeurEmploiService = demandeurEmploiService;
        this.isEntreprise = isEntreprise;

        setTitle(isEntreprise ? "Inscription Entreprise" : "Inscription Demandeur d'emploi");
        setHeaderText(isEntreprise
                ? "Créer un compte entreprise"
                : "Créer un compte demandeur d'emploi");

        // ================= CSS =================
        getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/InscriptionDialog.css").toExternalForm()
        );

        // ================= Taille écran =================
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double screenWidth  = Screen.getPrimary().getVisualBounds().getWidth();

        getDialogPane().setMaxHeight(screenHeight * 0.9);
        getDialogPane().setPrefWidth(screenWidth * 0.55);

        // ================= Boutons =================
        ButtonType inscrireBtn = new ButtonType("S'inscrire", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(inscrireBtn, ButtonType.CANCEL);

        // ================= Formulaire =================
        GridPane form = createForm();

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getDialogPane().setContent(scrollPane);

        // ================= Validation =================
        Button btn = (Button) getDialogPane().lookupButton(inscrireBtn);
        btn.setDefaultButton(true);
        btn.setOnAction(e -> {
            if (!validerFormulaire()) {
                e.consume();
            }
        });

        // ================= Résultat =================
        setResultConverter(button -> {
            if (button == inscrireBtn) {
                try {
                    if (isEntreprise) {
                        return entrepriseService.creerEntreprise(
                                loginField.getText(),
                                passwordField.getText(),
                                raisonSocialeField.getText(),
                                adresseField.getText(),
                                telephoneField.getText(),
                                descriptionArea.getText()
                        );
                    } else {
                        return demandeurEmploiService.creerDemandeurEmploi(
                                loginField.getText(),
                                passwordField.getText(),
                                nomField.getText(),
                                prenomField.getText(),
                                adresseField.getText(),
                                telephoneField.getText(),
                                faxField.getText(),
                                diplomeField.getText(),
                                experienceSpinner.getValue(),
                                salaireSpinner.getValue()
                        );
                    }
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
            return null;
        });
    }

    private GridPane createForm() {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_CENTER);

        int row = 0;

        loginField = new TextField();
        loginField.setPromptText("Entrez votre login");  // placeholder
        passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");  // placeholder

        grid.add(new Label("Login"), 0, row);
        grid.add(loginField, 1, row++);
        grid.add(new Label("Mot de passe"), 0, row);
        grid.add(passwordField, 1, row++);

        if (isEntreprise) {

            raisonSocialeField = new TextField();
            raisonSocialeField.setPromptText("Ex: Entreprise ABC");

            adresseField = new TextField();
            adresseField.setPromptText("Ex: 123 Rue Principale, Ville");

            telephoneField = new TextField();
            telephoneField.setPromptText("Ex: 0612345678");

            descriptionArea = new TextArea();
            descriptionArea.setPrefRowCount(3);
            descriptionArea.setPromptText("Décrivez votre entreprise");

            grid.add(new Label("Raison sociale"), 0, row);
            grid.add(raisonSocialeField, 1, row++);
            grid.add(new Label("Adresse"), 0, row);
            grid.add(adresseField, 1, row++);
            grid.add(new Label("Téléphone"), 0, row);
            grid.add(telephoneField, 1, row++);
            grid.add(new Label("Description"), 0, row);
            grid.add(descriptionArea, 1, row++);

        } else {

            nomField = new TextField();
            nomField.setPromptText("Entrez votre nom");

            prenomField = new TextField();
            prenomField.setPromptText("Entrez votre prénom");

            adresseField = new TextField();
            adresseField.setPromptText("Ex: 45 Rue Exemple, Ville");

            telephoneField = new TextField();
            telephoneField.setPromptText("Ex: 0612345678");

            faxField = new TextField();
            faxField.setPromptText("Ex: 0612345678");

            diplomeField = new TextField();
            diplomeField.setPromptText("Ex: Master en Informatique");

            experienceSpinner = new Spinner<>(0, 50, 0);
            experienceSpinner.setEditable(true);

            salaireSpinner = new Spinner<>(0.0, 100000.0, 0.0, 100.0);
            salaireSpinner.setEditable(true);

            cvPathLabel = new Label("Aucun CV sélectionné");

            Button btnCV = new Button("Choisir un CV (PNG)");
            btnCV.getStyleClass().add("secondary");
            btnCV.setOnAction(e -> choisirCV());

            HBox cvBox = new HBox(10, btnCV, cvPathLabel);

            grid.add(new Label("Nom"), 0, row);
            grid.add(nomField, 1, row++);
            grid.add(new Label("Prénom"), 0, row);
            grid.add(prenomField, 1, row++);
            grid.add(new Label("Adresse"), 0, row);
            grid.add(adresseField, 1, row++);
            grid.add(new Label("Téléphone"), 0, row);
            grid.add(telephoneField, 1, row++);
            grid.add(new Label("Fax"), 0, row);
            grid.add(faxField, 1, row++);
            grid.add(new Label("Diplôme"), 0, row);
            grid.add(diplomeField, 1, row++);
            grid.add(new Label("Expérience"), 0, row);
            grid.add(experienceSpinner, 1, row++);
            grid.add(new Label("Salaire"), 0, row);
            grid.add(salaireSpinner, 1, row++);
            grid.add(new Label("CV"), 0, row);
            grid.add(cvBox, 1, row++);
        }

        return grid;
    }

    private boolean validerFormulaire() {
        if (loginField.getText().isBlank() || passwordField.getText().isBlank()) {
            showError("Login et mot de passe obligatoires");
            return false;
        }
        if (!isEntreprise && selectedCVFile == null) {
            showError("Veuillez sélectionner un CV");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Validation");
        a.setHeaderText("Erreur");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void choisirCV() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File f = fc.showOpenDialog(getOwner());
        if (f != null) {
            selectedCVFile = f;
            cvPathLabel.setText(f.getName());
        }
    }

    public File getSelectedCVFile() {
        return selectedCVFile;
    }
}
