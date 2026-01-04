package com.example.agencerecrutement.javafx;

import com.example.agencerecrutement.javafx.controllers.LoginController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AgenceRecrutementApp extends JavafxApplication {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginController loginController = applicationContext.getBean(LoginController.class);
        loginController.setApplicationContext(applicationContext);
        
        Scene scene = new Scene(loginController.getView(), 600, 400);
        primaryStage.setTitle("Agence de Recrutement - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}


