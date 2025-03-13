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
        System.out.println("3. Overschrijven");

        System.out.print("\nMaak je keuze: ");
        String keuze = scanner.nextLine();

        switch (keuze) {
            case "1":
                System.out.println("\nJe hebt gekozen: Nieuwe rekening aanmaken.");
                System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");

                try {
                    rekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                    break;
                }
                try {
                    repository.voegRekeningToe(rekeningnummer);
                } catch (SQLException e) {
                    System.err.println("Het toevoegen aan de database is niet gelukt.");
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                }
                break;

            case "2":
                System.out.println("\nJe hebt gekozen: Saldo consulteren.");
                System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");

                try {
                    rekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                    break;
                }

                try {
                    Optional<BigDecimal> saldoOpt = repository.getSaldo(rekeningnummer);
                    if (saldoOpt.isPresent()) {
                        System.out.println("Het saldo van rekening " + rekeningnummer + " is: " + saldoOpt.get());
                    } else {
                        System.err.println("Rekeningnummer niet gevonden.");
                    }
                } catch (SQLException e) {
                    System.err.println("Er is een fout opgetreden bij het consulteren van het saldo.");
                    e.printStackTrace(System.err);
                }
                break;

            case "3":
                System.out.println("\nJe hebt gekozen: Overschrijven.");

                System.out.print("Voer het rekeningnummer van de afzender in (BE IBAN, 16 tekens): ");
                String vanRekeningnummer;
                try {
                    vanRekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                    break;
                }

                System.out.print("Voer het rekeningnummer van de ontvanger in (BE IBAN, 16 tekens): ");
                String naarRekeningnummer;
                try {
                    naarRekeningnummer = Rekeningnummer.valideer(scanner.nextLine());
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                    break;
                }

                System.out.print("Voer het bedrag in voor de overschrijving: ");
                BigDecimal bedrag;
                try {
                    bedrag = new BigDecimal(scanner.nextLine());
                    if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Het bedrag moet groter zijn dan 0.");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                    break;
                }

                // Voer de overschrijving uit
                try {
                    repository.schrijfOver(vanRekeningnummer, naarRekeningnummer, bedrag);
                } catch (SQLException e) {
                    System.err.println("Er is een fout opgetreden bij de overschrijving.");
                    e.printStackTrace(System.err);
                } catch (IllegalArgumentException e) {
                    System.err.println("Foutmeldingen:");
                    System.err.println(e.getMessage());
                }
                break;

            default: // Ongeldige keuze
                System.err.println("Ongeldige keuze. Herstart het programma.\n");
                break;
        }

        scanner.close();
        System.out.println("\nBedankt voor het gebruik van de BankApp. Tot ziens!");
    }
}