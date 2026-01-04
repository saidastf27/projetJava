package com.example.agencerecrutement.javafx;

import javafx.application.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class JavafxApplication extends Application {
    
    protected ConfigurableApplicationContext applicationContext;
    
    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.applicationContext = new SpringApplicationBuilder()
            .sources(com.example.agencerecrutement.AgencerecrutementApplication.class)
            .run(args);
    }
    
    @Override
    public void stop() {
        if (applicationContext != null) {
            this.applicationContext.close();
        }
    }
}

