package be.vdab.taken;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        var repository = new BrouwerRepository();
        try {
            System.out.println();
            System.out.println("Gemiddelde omzet: " + repository.findGemiddeldeOmzet());
            System.out.println();
            repository.findByBovenGemiddeldeOmzet().forEach(System.out::println);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}