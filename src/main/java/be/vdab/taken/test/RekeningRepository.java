package be.vdab.taken.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

class RekeningRepository extends AbstractRepository {

    void voegRekeningToe(String rekeningnummer) throws SQLException {
        var sql = """
                insert into rekeningen(nummer, saldo)
                values (?,0)
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setString(1, rekeningnummer);
            statement.executeUpdate();
            connection.commit();
            System.out.println("Rekening succesvol toegevoegd aan de database: " + rekeningnummer);
        } catch (SQLException e) {
            // Controleer op een MySQL-foutcode voor duplicate key (foutcode 1062)
            if (e.getErrorCode() == 1062) {
                System.err.println("Dit rekeningnummer bestaat al en werd dus niet toegevoegd aan de database.\n");
            } else {
                throw e; // Voor andere fouten dan duplicate key
            }
        }
    }

    Optional<BigDecimal> getSaldo(String rekeningnummer) throws SQLException {
        var sql = """
                select saldo
                from rekeningen
                where nummer = ?
                for update
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setString(1, rekeningnummer);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal saldo = resultSet.getBigDecimal("saldo");
                    return Optional.of(saldo);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    void schrijfOver(String vanRekeningnummer, String naarRekeningnummer, BigDecimal bedrag) throws SQLException {
        if (vanRekeningnummer.equals(naarRekeningnummer)) {
            throw new IllegalArgumentException("Van- en naar-rekening kunnen niet hetzelfde zijn.");
        }
        if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Het bedrag moet groter zijn dan 0.");
        }

        var sqlSelect = """
                    select saldo
                    from rekeningen
                    where nummer = ?
                    for update
                """;
        var sqlUpdate = """
                    update rekeningen
                    set saldo = saldo + ?
                    where nummer = ?
                """;

        try (var connection = super.getConnection();
             var selectStmtVan = connection.prepareStatement(sqlSelect);
             var selectStmtNaar = connection.prepareStatement(sqlSelect);
             var updateStmt = connection.prepareStatement(sqlUpdate)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            // Lock en lees saldo van van-rekening
            selectStmtVan.setString(1, vanRekeningnummer);
            try (var resultSet = selectStmtVan.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalArgumentException("De van-rekening bestaat niet.");
                }
                BigDecimal vanSaldo = resultSet.getBigDecimal("saldo");
                if (vanSaldo.compareTo(bedrag) < 0) {
                    throw new IllegalArgumentException("Saldo te laag voor overschrijving. Huidig saldo: €" + vanSaldo);
                }
            }

            // Lock en lees saldo van naar-rekening
            selectStmtNaar.setString(1, naarRekeningnummer);
            try (var resultSet = selectStmtNaar.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalArgumentException("De naar-rekening bestaat niet.");
                }
            }

            // Verminder saldo van van-rekening
            updateStmt.setBigDecimal(1, bedrag.negate());
            updateStmt.setString(2, vanRekeningnummer);
            updateStmt.executeUpdate();

            // Verhoog saldo van naar-rekening
            updateStmt.setBigDecimal(1, bedrag);
            updateStmt.setString(2, naarRekeningnummer);
            updateStmt.executeUpdate();

            connection.commit();
            System.out.println("Overschrijving succesvol uitgevoerd.");

        } catch (SQLException e) {
            throw new SQLException("Fout bij overschrijving", e);
        }
    }
}
