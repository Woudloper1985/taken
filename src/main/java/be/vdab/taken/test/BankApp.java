package be.vdab.taken.test;

import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        nieuweRekening();
    }
    public static void nieuweRekening() {
        Scanner scanner = new Scanner(System.in);
        Rekening rekening = null; // Variabele voor het aan te maken Rekening-object

        System.out.println("Welkom! Je gaat een nieuw rekeningnummer aanmaken.");

        // While-loop, totdat er correct een nieuw Rekening-object werd aangemaakt
        while (rekening == null) {
            System.out.print("Voer een Belgisch (BE) rekeningnummer in (IBAN, 16 tekens): ");
            String input = scanner.nextLine();

            try {
                // Probeer een nieuw Rekening-object aan te maken
                rekening = new Rekening(input);
                System.out.println("Succes! Het rekeningnummer is gevalideerd en opgeslagen, met beginsaldo 0.");
                System.out.println("Aangemaakte rekening: " + rekening.getRekeningnummer());
            } catch (IllegalArgumentException e) {
                // Toon de specifieke foutmelding en vraag opnieuw om input
                System.out.println("Fout: " + e.getMessage());
                System.out.println("Probeer het opnieuw.");
            }
        }

        scanner.close();
        System.out.println("Bedankt voor het aanmaken van een rekeningnummer. Programma beëindigd.");
    }
}