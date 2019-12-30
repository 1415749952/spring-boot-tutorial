package com.mhkj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Package pkg = Application.class.getPackage();
        String version = (pkg != null) ? pkg.getImplementationVersion() : null;

        System.out.println(version);
        SpringApplication.run(Application.class, args);
    }

}
