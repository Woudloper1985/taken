package be.vdab.taken.familie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PersoonRepository extends AbstractRepository {
    void create(Gezin gezin) throws SQLException {
        var sql = """
                insert into personen(voornaam, papaId, mamaId)
                values (?, ?, ?)
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            //papa toevoegen:
            statement.setString(1, gezin.getPapa());
            statement.setNull(2, Types.INTEGER);
            statement.setNull(3, Types.INTEGER);
            statement.addBatch();
            //mama toevoegen:
            statement.setString(1, gezin.getMama());
            statement.addBatch();
            statement.executeBatch();
// gegenereerde autonumbers lezen:
            var result = statement.getGeneratedKeys();
            result.next(); // gegenereerde id van papa lezen
            statement.setLong(2, result.getLong(1)); // id van papa gebruiken als papaId
            result.next();// gegenereerde id van mama lezen
            statement.setLong(3, result.getLong(1)); // id van mama gebruiken als mamaId
// kinderen toevoegen:
            for (var kind : gezin.getKinderen()) {
                statement.setString(1, kind);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        }
    }
}
