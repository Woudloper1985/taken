package be.vdab.taken;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BrouwerRepository extends AbstractRepository {
    BigDecimal findGemiddeldeOmzet() throws SQLException {
        var sql = """
                SELECT ROUND(AVG(omzet),2) as gemiddelde
                FROM brouwers
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();
            result.next();
            return result.getBigDecimal("gemiddelde");
        }
    }

    List<Brouwer> findByBovenGemiddeldeOmzet() throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        var sql = """
                SELECT *
                FROM brouwers
                WHERE omzet > (SELECT AVG(omzet) FROM brouwers)
                ORDER BY omzet
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }
            return brouwers;
        }
    }

    private Brouwer naarBrouwer(ResultSet result) throws SQLException {
        return new Brouwer(result.getLong("id"),
                result.getString("naam"),
                result.getString("adres"),
                result.getInt("postcode"),
                result.getString("gemeente"),
                result.getBigDecimal("omzet"));
    }

    List<Brouwer> findByOmzetTussenMinEnMax(int min, int max) throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        var sql = """
                SELECT *
                FROM brouwers
                WHERE omzet BETWEEN ? AND ?
                ORDER BY omzet, id
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, min);
            statement.setInt(2, max);
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }
            return brouwers;
        }
    }

    Optional<Brouwer> findById(long id) throws SQLException {
        var sql = """
                select id, naam, adres, postcode, gemeente, omzet
                from brouwers
                where id = ?
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
        /* Als je enkele specifieke kolommen wilt printen:

            if (result.next()) {
                System.out.println("ID: " + result.getLong("id"));
                System.out.println("Naam: " + result.getString("naam"));
                System.out.println("Postcode: " + result.getString("postcode"));
                return Optional.of(naarBrouwer(result));
            }
            return Optional.empty();*/
            return result.next() ? Optional.of(naarBrouwer(result)) : Optional.empty(); //als je alle kolommen van naarBrouwer wilt printen.
        }
    }

    List<Brouwer> findByOmzetTussen(int van, int tot) throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        try (var connection = super.getConnection();
             var statement = connection.prepareCall("{call BrouwersMetOmzetTussen(?,?)}")) {
            statement.setInt(1, van);
            statement.setInt(2, tot);
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }
            return brouwers;
        }
    }

    void brouwer1GaatFailliet() throws SQLException {
        var sqlNaarBrouwer2 = """
                update bieren
                set brouwerId = 2
                where brouwerId = 1 and alcohol >= 8.5
                """;
        var sqlNaarBrouwer3 = """
                update bieren
                set brouwerId = 3
                where brouwerId = 1 and alcohol < 8.5
                """;
        var sqlDeleteBrouwer1 = """
                DELETE FROM brouwers
                WHERE id = 1;
                """;
        try (var connection = super.getConnection();
             var statementNaarBrouwer2 = connection.prepareStatement(sqlNaarBrouwer2);
             var statementNaarBrouwer3 = connection.prepareStatement(sqlNaarBrouwer3);
             var statementDeleteBrouwer1 = connection.prepareStatement(sqlDeleteBrouwer1);) {
            connection.setAutoCommit(false);
            statementNaarBrouwer2.executeUpdate();
            statementNaarBrouwer3.executeUpdate();
            statementDeleteBrouwer1.executeUpdate();
            connection.commit();
        }
    }

    List<BrouwerMetAantal> aantalBierenPerBrouwer() throws SQLException {
        var list = new ArrayList<BrouwerMetAantal>();
        String sql = """
                SELECT brouwers.naam AS brouwer, count(*) as aantal
                from bieren.bieren
                inner join bieren.brouwers
                on bieren.brouwerId = brouwers.id
                group by brouwers.naam
                order by brouwers.naam
                """;
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            for (ResultSet result = statement.executeQuery(); result.next(); ) {
                list.add(new BrouwerMetAantal(result.getString("brouwer"), result.getInt("aantal")));
            }
            connection.commit();
            return list;
        }
    }

    int maakOmzetLeeg(Set<Long> ids) throws SQLException {
        if (ids.isEmpty()) {
            return 0;
        }
        var sqlUpdate = """
                update brouwers
                set omzet = null
                where id in (
                """
                + "?,".repeat(ids.size() - 1)
                + "?)";
        try (var connection = super.getConnection();
             var statementUpdate = connection.prepareStatement(sqlUpdate)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            var index = 1;
            for (var id : ids) {
                statementUpdate.setLong(index++, id); //post-increment, dus verhoogt pas ná 1ste iteratie.
            }
            var aantalAangepasteRecords = statementUpdate.executeUpdate();
            connection.commit();
            return aantalAangepasteRecords;
        }
    }

    Set<Long> findIds(Set<Long> ids) throws SQLException {
        if (ids.isEmpty()) {
            return Set.of();
        }
        var gevondenIds = new HashSet<Long>();
        var sql = """
                select id
                from brouwers
                where id in (
                """
                + "?,".repeat(ids.size() - 1)
                + "?)";
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)){
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            var index = 1;
            for (var id : ids) {
                statement.setLong(index++, id);
            }
            for (var result = statement.executeQuery(); result.next(); ) {
                gevondenIds.add(result.getLong("id"));
            }
            return gevondenIds;
        }

    }
}