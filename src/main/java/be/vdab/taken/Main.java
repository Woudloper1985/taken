package be.vdab.taken;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        var scanner = new Scanner(System.in);
//        System.out.print("Minimum omzet: ");
//        var minimumOmzet = scanner.nextInt();
//        System.out.print("Maximum omzet: ");
//        var maximumOmzet = scanner.nextInt();
        //var id = scanner.nextLong();
        var repository = new BrouwerRepository();
        try {
//            System.out.println();
//            System.out.println("Gemiddelde omzet: " + repository.findGemiddeldeOmzet());
//            System.out.println();
            //repository.findByBovenGemiddeldeOmzet().forEach(System.out::println);
            //System.out.println();
            //repository.findByOmzetTussenMinEnMax(minimumOmzet, maximumOmzet).forEach(System.out::println);
            //repository.findById(id).ifPresentOrElse(System.out::println, ()-> System.out.println("Niet gevonden"));
            //repository.findByOmzetTussen(minimumOmzet, maximumOmzet).forEach(System.out::println);
        repository.brouwer1GaatFailliet();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}