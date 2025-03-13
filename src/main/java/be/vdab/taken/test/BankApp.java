package be.vdab.taken.test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var repository = new RekeningRepository();
        String rekeningnummer;

        System.out.println("Welkom bij de BankApp! Kies een optie:");
        System.out.println("1. Nieuwe rekening");
        System.out.println("2. Saldo consulteren");
        System.out.println("3. Overschrijven (binnenkort beschikbaar)");

        System.out.print("\nMaak je keuze: ");
        String keuze = scanner.nextLine();

        switch (keuze) {
            case "1":
                System.out.println("\nJe hebt gekozen: Nieuwe rekening aanmaken.");
                System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");

                try {
                    rekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.out.println("Fout(en): " + e.getMessage());
                    break;
                }
                try {
                    repository.voegRekeningToe(rekeningnummer);
                } catch (SQLException e) {
                    System.out.println("Het toevoegen aan de database is niet gelukt.");
                    System.out.println("Foutmelding: " + e.getMessage());
                }
                break;

            case "2":
                System.out.println("\nJe hebt gekozen: Saldo consulteren.");
                System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");

                try {
                    rekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.out.println("Fout(en): " + e.getMessage());
                    break;
                }

                try {
                    Optional<BigDecimal> saldoOpt = repository.getSaldo(rekeningnummer);
                    if (saldoOpt.isPresent()) {
                        System.out.println("Het saldo van rekening " + rekeningnummer + " is: " + saldoOpt.get());
                    } else {
                        System.out.println("Rekeningnummer niet gevonden.");
                    }
                } catch (SQLException e) {
                    System.out.println("Er is een fout opgetreden bij het consulteren van het saldo.");
                    e.printStackTrace(System.err);
                }
                break;


            case "3": // Overschrijven (placeholder)
                System.out.println("\nJe hebt gekozen: Overschrijven.");
                System.out.println("Deze functionaliteit is nog niet geïmplementeerd. Probeer later opnieuw.");
                break;

            default: // Ongeldige keuze
                System.out.println("\nOngeldige keuze. Herstart het programma.");
                break;
        }

        scanner.close();
        System.out.println("\nBedankt voor het gebruik van de BankApp. Tot ziens!");
    }
}