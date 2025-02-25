package be.vdab.taken;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
