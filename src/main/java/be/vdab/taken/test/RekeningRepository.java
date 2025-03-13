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
                System.out.println("Dit rekeningnummer bestaat al en werd dus niet toegevoegd aan de database.");
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
                    return Optional.of(saldo); // Als de rekening gevonden is, geef je het saldo terug
                } else {
                    return Optional.empty(); // Als de rekening niet gevonden is, geef je een lege Optional terug
                }
            }
        }
    }

    void schrijfOver(String vanRekeningnummer, String naarRekeningnummer, BigDecimal bedrag) throws SQLException {
        StringBuilder foutmeldingen = new StringBuilder();

        if (vanRekeningnummer.equals(naarRekeningnummer)) {
            foutmeldingen.append("Van- en naar-rekening kunnen niet hetzelfde zijn.\n");
        }

        if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
            foutmeldingen.append("Het bedrag moet groter zijn dan 0.\n");
        }

        Optional<BigDecimal> vanSaldoOpt = getSaldo(vanRekeningnummer);
        if (vanSaldoOpt.isEmpty()) {
            foutmeldingen.append("De van-rekening bestaat niet.\n");
        }

        Optional<BigDecimal> naarSaldoOpt = getSaldo(naarRekeningnummer);
        if (naarSaldoOpt.isEmpty()) {
            foutmeldingen.append("De naar-rekening bestaat niet.\n");
        }

        // Als er fouten zijn verzameld, toon ze dan en stop de methode
        if (!foutmeldingen.isEmpty()) {
            throw new IllegalArgumentException(foutmeldingen.toString());
        }

        BigDecimal vanSaldo = vanSaldoOpt.get();
        if (vanSaldo.compareTo(bedrag) < 0) {
            foutmeldingen.append("Het saldo van de van-rekening is onvoldoende voor de overschrijving.\n");
        }

        // Als er fouten zijn verzameld, toon ze dan en stop de methode
        if (!foutmeldingen.isEmpty()) {
            throw new IllegalArgumentException(foutmeldingen.toString());
        }

        // Hier wordt de transactie uitgevoerd.
        var sqlUpdateVan = """
                update rekeningen
                set saldo = saldo - ?
                where nummer = ?
                """;
        var sqlUpdateNaar = """
                update rekeningen
                set saldo = saldo + ?
                where nummer = ?
                """;

        try (var connection = super.getConnection();
             var statementVan = connection.prepareStatement(sqlUpdateVan);
             var statementNaar = connection.prepareStatement(sqlUpdateNaar)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            // Verminder het saldo van de van-rekening
            statementVan.setBigDecimal(1, bedrag);
            statementVan.setString(2, vanRekeningnummer);
            statementVan.executeUpdate();

            // Verhoog het saldo van de naar-rekening
            statementNaar.setBigDecimal(1, bedrag);
            statementNaar.setString(2, naarRekeningnummer);
            statementNaar.executeUpdate();

            connection.commit();
            System.out.println("Overschrijving succesvol uitgevoerd.");
        } catch (SQLException e) {
            foutmeldingen.append("Er is een fout opgetreden bij de overschrijving.\n");
            System.out.println(foutmeldingen.toString());
            e.printStackTrace();
            // Zorg ervoor dat je bij een fout de transactie rolt.
            try (var connection = super.getConnection()) {
                connection.rollback();
            }
        }
    }


}
