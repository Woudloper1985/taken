package be.vdab.taken;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        BierRepository repository = new BierRepository();
        try {
            System.out.print(repository.verwijderAlcoholNull());
            System.out.println(" bieren verwijderd");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
