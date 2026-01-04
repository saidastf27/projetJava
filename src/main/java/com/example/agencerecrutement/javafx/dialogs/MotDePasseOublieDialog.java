package com.example.agencerecrutement.javafx.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MotDePasseOublieDialog {

    private Dialog<Void> dialog;

    public MotDePasseOublieDialog() {
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog<>();
        dialog.setTitle("Mot de passe oublié");
        dialog.setHeaderText("Récupération de mot de passe");

        // =================== CSS ===================
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/MotDePasseOublieDialog.css").toExternalForm()
        );

        // =================== Contenu ===================
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        // Message principal
        Text mainMessage = new Text("Pour récupérer votre mot de passe, veuillez contacter le bureau sur un de ces emails :");
        mainMessage.setTextAlignment(TextAlignment.CENTER);
        mainMessage.setWrappingWidth(400);

        // Liste des emails
        VBox emailsBox = new VBox(10);
        emailsBox.setAlignment(Pos.CENTER);

        Label email1 = new Label("1. HibaZouitina@gmail.com");
        Label email2 = new Label("2. Saida27stifi@gmail.com");
        Label email3 = new Label("3. Imanetaleb@gmail.com");
        Label email4 = new Label("4. chouaibbouslamti7@gmail.com");

        // Appliquer la classe CSS pour les emails
        email1.getStyleClass().add("label");
        email2.getStyleClass().add("label");
        email3.getStyleClass().add("label");
        email4.getStyleClass().add("label");

        emailsBox.getChildren().addAll(email1, email2, email3, email4);
        emailsBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-border-radius: 5px; -fx-border-color: #dee2e6;");

        // Message informatif
        Text infoMessage = new Text("Notre équipe vous répondra dans les plus brefs délais pour vous aider à récupérer l'accès à votre compte.");
        infoMessage.setTextAlignment(TextAlignment.CENTER);
        infoMessage.setWrappingWidth(400);
        infoMessage.getStyleClass().add("text-info");

        content.getChildren().addAll(mainMessage, emailsBox, infoMessage);

        // =================== Boutons ===================
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk);

        dialog.getDialogPane().setContent(content);

        // Style du bouton OK
        Button okButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        okButton.getStyleClass().add("button"); 
    }

    public void showAndWait() {
        dialog.showAndWait();
    }
}
