package com.example.agencerecrutement.javafx.dialogs;

import com.example.agencerecrutement.model.Utilisateur;
import com.example.agencerecrutement.service.AuthentificationService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class ModifierMotDePasseDialog {

    private final AuthentificationService authentificationService;
    private final Utilisateur utilisateur;
    private Dialog<String> dialog;

    public ModifierMotDePasseDialog(AuthentificationService authentificationService, Utilisateur utilisateur) {
        this.authentificationService = authentificationService;
        this.utilisateur = utilisateur;
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog<>();
        dialog.setTitle("Modifier le mot de passe");
        dialog.setHeaderText("Modifier le mot de passe de : " + utilisateur.getLogin() +
                "\n(" + utilisateur.getRole() + ")");

        // Appliquer le CSS
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/ModifierMotDePasseDialog.css").toExternalForm()
        );

        // Créer les champs
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.getStyleClass().add("password-field");
        newPasswordField.setPrefWidth(250);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
        confirmPasswordField.getStyleClass().add("password-field");
        confirmPasswordField.setPrefWidth(250);

        // Créer le layout
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label newPasswordLabel = new Label("Nouveau mot de passe:");
        newPasswordLabel.getStyleClass().add("label");
        Label confirmPasswordLabel = new Label("Confirmer le mot de passe:");
        confirmPasswordLabel.getStyleClass().add("label");

        grid.add(newPasswordLabel, 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(confirmPasswordLabel, 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Créer les boutons
        ButtonType buttonTypeOk = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        okButton.getStyleClass().add("button");
        okButton.setDisable(true);

        // Validation en temps réel
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) ->
                validateFields(newPasswordField, confirmPasswordField, okButton));

        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) ->
                validateFields(newPasswordField, confirmPasswordField, okButton));

        // Résultat quand on clique sur OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                return newPasswordField.getText();
            }
            return null;
        });
    }

    private void validateFields(PasswordField newPasswordField, PasswordField confirmPasswordField, Button okButton) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean isValid = !newPassword.isEmpty() &&
                !confirmPassword.isEmpty() &&
                newPassword.equals(confirmPassword) &&
                newPassword.length() >= 6;

        okButton.setDisable(!isValid);

        if (!newPassword.isEmpty() && newPassword.length() < 6) {
            newPasswordField.setStyle("-fx-border-color: red;");
        } else {
            newPasswordField.setStyle("");
        }

        if (!confirmPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
        } else {
            confirmPasswordField.setStyle("");
        }
    }

    public Optional<String> showAndWait() {
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                authentificationService.modifierMotDePasse(utilisateur.getIdUtilisateur(), result.get());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                applyAlertCSS(successAlert);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Mot de passe modifié");
                successAlert.setContentText("Le mot de passe de " + utilisateur.getLogin() + " a été modifié avec succès.");
                successAlert.showAndWait();

                return result;
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                applyAlertCSS(errorAlert);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Erreur lors de la modification");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }
        }

        return Optional.empty();
    }

    private void applyAlertCSS(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/ModifierMotDePasseDialog.css").toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("alert");
        Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.getStyleClass().add("button");
        }
    }
}
