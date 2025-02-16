package com.mycompany.cab;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        // Get Hibernate SessionFactory
        SessionFactory factory = DatabaseConnection.getSessionFactory();

        // Check if the session factory is created
        if (factory != null) {
            System.out.println("Hibernate SessionFactory is created successfully!");
        } else {
            System.out.println("Failed to create Hibernate SessionFactory.");
        }

        // Open a session to test the connection
        Session session = factory.openSession();
        if (session.isConnected()) {
            System.out.println("Database connection is successful!");
        } else {
            System.out.println("Database connection failed.");
        }

        // Close session
        session.close();
        factory.close();
    }
}
