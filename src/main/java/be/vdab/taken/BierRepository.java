package be.vdab.taken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BierRepository extends AbstractRepository{
    int verwijderAlcoholNull() throws SQLException {
        String sql = """
                delete from bieren
                where alcohol is null
                """;
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        }
    }
    private Bier naarBier(ResultSet result) throws SQLException {
        return new Bier(result.getLong("id"), result.getString("naam"), result.getLong("brouwerId"), result.getLong("soortId"), result.getDouble("alcohol"), result.getObject("sinds", LocalDate.class));
    }

    List<Bier> findByMaand(int maand) throws SQLException {
        var bieren = new ArrayList<Bier>();
        var sql = """
                select id, naam, brouwerId, soortId, alcohol, sinds
                from bieren
                where {fn month(sinds)} = ?
                order by naam
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setInt(1, maand);
            for (var result = statement.executeQuery(); result.next(); ) {
                bieren.add(naarBier(result));
            }
            connection.commit();
            return bieren;
        }
    }
}