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
}
