package be.vdab.taken;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}