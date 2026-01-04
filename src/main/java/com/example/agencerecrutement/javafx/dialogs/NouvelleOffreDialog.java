package com.example.agencerecrutement.javafx.dialogs;

import com.example.agencerecrutement.model.Offre;
import com.example.agencerecrutement.service.OffreService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class NouvelleOffreDialog extends Dialog<Offre> {

    private final OffreService offreService;
    private final Long idEntreprise;

    private TextField titreField;
    private TextArea competencesArea;
    private Spinner<Integer> experienceSpinner;
    private Spinner<Integer> nbPostesSpinner;

    public NouvelleOffreDialog(OffreService offreService, Long idEntreprise) {
        this.offreService = offreService;
        this.idEntreprise = idEntreprise;

        setTitle("Nouvelle offre d'emploi");
        setHeaderText("Créer une nouvelle offre d'emploi");

        // Appliquer le CSS global
        getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/NouvelleOffreDialog.css").toExternalForm()
        );

        // Créer les boutons
        ButtonType creerButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(creerButtonType, ButtonType.CANCEL);

        // Créer le formulaire
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        titreField = new TextField();
        titreField.setPromptText("Titre de l'offre");
        titreField.getStyleClass().add("text-field");

        competencesArea = new TextArea();
        competencesArea.setPromptText("Compétences requises");
        competencesArea.setPrefRowCount(5);
        competencesArea.getStyleClass().add("text-area");

        experienceSpinner = new Spinner<>(0, 50, 0, 1);
        experienceSpinner.setEditable(true);
        experienceSpinner.getStyleClass().add("spinner");

        nbPostesSpinner = new Spinner<>(1, 100, 1, 1);
        nbPostesSpinner.setEditable(true);
        nbPostesSpinner.getStyleClass().add("spinner");

        Label titreLabel = new Label("Titre :");
        titreLabel.getStyleClass().add("label");
        Label competencesLabel = new Label("Compétences :");
        competencesLabel.getStyleClass().add("label");
        Label expLabel = new Label("Expérience requise (années) :");
        expLabel.getStyleClass().add("label");
        Label postesLabel = new Label("Nombre de postes :");
        postesLabel.getStyleClass().add("label");

        grid.add(titreLabel, 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(competencesLabel, 0, 1);
        grid.add(competencesArea, 1, 1);
        grid.add(expLabel, 0, 2);
        grid.add(experienceSpinner, 1, 2);
        grid.add(postesLabel, 0, 3);
        grid.add(nbPostesSpinner, 1, 3);

        getDialogPane().setContent(grid);

        // Valider avant de fermer
        Button creerButton = (Button) getDialogPane().lookupButton(creerButtonType);
        creerButton.getStyleClass().add("button");
        creerButton.setOnAction(e -> {
            if (!validerFormulaire()) {
                e.consume();
            }
        });

        // Convertir le résultat
        setResultConverter(dialogButton -> {
            if (dialogButton == creerButtonType) {
                try {
                    return offreService.creerOffre(
                            idEntreprise,
                            titreField.getText(),
                            competencesArea.getText(),
                            experienceSpinner.getValue(),
                            nbPostesSpinner.getValue()
                    );
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    applyAlertCSS(alert);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Erreur lors de la création de l'offre");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    }

    private boolean validerFormulaire() {
        if (titreField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            applyAlertCSS(alert);
            alert.setTitle("Validation");
            alert.setHeaderText("Champ requis");
            alert.setContentText("Le titre est obligatoire");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void applyAlertCSS(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/NouvelleOffreDialog.css").toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("alert");
        Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.getStyleClass().add("button");
        }
    }
}
