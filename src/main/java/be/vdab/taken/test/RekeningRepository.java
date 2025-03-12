package be.vdab.taken.test;

import java.sql.Connection;
import java.sql.SQLException;

public class RekeningRepository extends AbstractRepository {

    // Methode om een rekening toe te voegen aan de database
    public boolean voegRekeningToe(Rekening rekening) throws SQLException {
        var sql = """
                insert into rekeningen(nummer, saldo)
                values (?,0)
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setString(1, rekening.getRekeningnummer());
            statement.executeUpdate();
            connection.commit();
            System.out.println("Rekening succesvol toegevoegd aan de database: " + rekening.getRekeningnummer());
            return true; // toevoeging geslaagd
        } catch (SQLException e) {
            // Controleer op een SQL-foutcode voor duplicate key (foutcode 1062)
            if (e.getErrorCode() == 1062) {
                System.out.println("Dit rekeningnummer bestaat al en werd dus niet toegevoegd aan de database.");
            } else {
                throw e; // voor andere fouten dan duplicate key.
            }
        }
        return false; // toevoegen niet geslaagd
    }
}
