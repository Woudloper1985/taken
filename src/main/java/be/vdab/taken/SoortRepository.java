package be.vdab.taken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SoortRepository extends AbstractRepository {
    List<String> findAllNamen() throws SQLException {
        var sql = """
                select naam
                from soorten
                order by naam
                """;
        var namen = new ArrayList<String>();
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (ResultSet result = statement.executeQuery(); result.next(); ) {
                namen.add(result.getString("naam"));
            }
            return namen;
        }
    }

    List<Soort> findAll() throws SQLException {
        var soorten = new ArrayList<Soort>();
        var sql = """
                select id, naam
                from soorten
                order by naam
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (var result = statement.executeQuery(); result.next(); ) {
                soorten.add(naarSoort(result));
            }
            return soorten;
        }
    }

    private Soort naarSoort (ResultSet result) throws SQLException {
        return new Soort(result.getLong("id"), result.getString("naam"));
    }
}
