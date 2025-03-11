package be.vdab.taken;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        //System.out.print("Typ een maandnummer tussen 1 en 12: ");
        System.out.print("Typ de naam van een soort bier: ");
        var soort = scanner.next();
//        System.out.print("Minimum omzet: ");
//        var minimumOmzet = scanner.nextInt();
//        System.out.print("Maximum omzet: ");
//        var maximumOmzet = scanner.nextInt();
        //var id = scanner.nextLong();
        //var repository = new BrouwerRepository();
        var repository = new BierRepository();
        try {
//            System.out.println();
//            System.out.println("Gemiddelde omzet: " + repository.findGemiddeldeOmzet());
//            System.out.println();
            //repository.findByBovenGemiddeldeOmzet().forEach(System.out::println);
            //System.out.println();
            //repository.findByOmzetTussenMinEnMax(minimumOmzet, maximumOmzet).forEach(System.out::println);
            //repository.findById(id).ifPresentOrElse(System.out::println, ()-> System.out.println("Niet gevonden"));
            //repository.findByOmzetTussen(minimumOmzet, maximumOmzet).forEach(System.out::println);
            //repository.brouwer1GaatFailliet();
            //repository.findByMaand(scanner.nextInt()).forEach(bier -> System.out.println(bier.getNaam() + " " + bier.getSinds()));
            //repository.aantalBierenPerBrouwer().forEach(System.out::println);
            var namen = repository.findBySoort(soort);
            if (namen.isEmpty()) {
                System.out.println("Geen bieren gevonden");
            } else {
                namen.forEach(System.out::println);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}