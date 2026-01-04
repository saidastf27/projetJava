package com.example.agencerecrutement;

import com.example.agencerecrutement.javafx.AgenceRecrutementApp;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgencerecrutementApplication {

	public static void main(String[] args) {
		// Lancer l'application JavaFX
		Application.launch(AgenceRecrutementApp.class, args);
	}

}
