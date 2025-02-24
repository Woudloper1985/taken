package be.vdab.taken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}