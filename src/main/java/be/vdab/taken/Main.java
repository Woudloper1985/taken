package be.vdab.taken;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        var repository = new SoortRepository();
        try {
            repository.findAllNamen().forEach(System.out:: println);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}