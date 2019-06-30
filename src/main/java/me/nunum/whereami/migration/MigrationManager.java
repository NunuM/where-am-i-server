package me.nunum.whereami.migration;

import java.util.HashMap;
import java.util.Scanner;


public class MigrationManager {


    public static void main(String[] args) {

        HashMap<String, Runnable> migrations = new HashMap<>();

        migrations.put(Migration1.class.getSimpleName(), new Migration1());

        final Scanner scanner = new Scanner(System.in);

        String userInput;
        boolean stop = false;

        do {

            System.out.println("Migrations Available:");

            migrations.keySet().forEach(k -> {
                System.out.println("\tMigration: " + k);
            });

            System.out.print(".\n\nInsert migration name or type quit for leaving\n> ");

            userInput = scanner.nextLine().trim();

            if ("quit".equalsIgnoreCase(userInput)) {
                stop = true;
            } else {

                final Runnable runnable = migrations.get(userInput);

                if (runnable != null) {
                    runnable.run();
                } else {
                    System.out.println("Migration " + userInput + " was not recognized");
                }
            }

        } while (!stop);

        System.out.println("Leaving");
    }
}
