package be.vdab.taken.test;

import java.sql.SQLException;
import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var repository = new RekeningRepository();

        System.out.println("Welkom bij de BankApp! Kies een optie:");
        System.out.println("1. Nieuwe rekening");
        System.out.println("2. Saldo consulteren (binnenkort beschikbaar)");
        System.out.println("3. Overschrijven (binnenkort beschikbaar)");

        System.out.print("\nMaak je keuze: ");
        String keuze = scanner.nextLine();

        switch (keuze) {
            case "1": // Nieuwe rekening
                System.out.println("\nJe hebt gekozen: Nieuwe rekening aanmaken.");
                System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");
                String rekeningnummer = scanner.nextLine();

                try {
                    // Maak een nieuwe rekening aan en voeg deze toe aan de database
                    var rekening = new Rekening(rekeningnummer);
                    repository.voegRekeningToe(rekening);
                    System.out.println("Succes! Het rekeningnummer is gevalideerd en toegevoegd aan de database.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Fout: " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("Het toevoegen aan de database is niet gelukt.");
                    System.out.println("Foutmelding: " + e.getMessage());
                }
                break;

            case "2": // Saldo consulteren (placeholder)
                System.out.println("\nJe hebt gekozen: Saldo consulteren.");
                System.out.println("Deze functionaliteit is nog niet geïmplementeerd. Probeer later opnieuw.");
                break;

            case "3": // Overschrijven (placeholder)
                System.out.println("\nJe hebt gekozen: Overschrijven.");
                System.out.println("Deze functionaliteit is nog niet geïmplementeerd. Probeer later opnieuw.");
                break;

            default: // Ongeldige keuze
                System.out.println("\nOngeldige keuze. Het programma wordt beëindigd.");
                break;
        }

        scanner.close();
        System.out.println("Bedankt voor het gebruik van de BankApp. Tot ziens!");
    }
}