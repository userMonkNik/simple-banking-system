package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.HashSet;

public class SQLiteConnect {

    public SQLiteConnect() {
        createTableIfNotExist();
    }

    public  Connection connect() {
        String url = "jdbc:sqlite:bank.db";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        Connection con = null;

        try {
            con = dataSource.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public void createTableIfNotExist() {

        String sqlCreateTable =
                "CREATE TABLE IF NOT EXISTS 'cards' (\n" +
                        "id INT NOT NULL,\n" +
                        "number VARCHAR,\n" +
                        "pin VARCHAR,\n" +
                        "balance INT\n" +
                        ");";

        try (Connection con = connect();
             Statement statement = con.createStatement()) {

            statement.executeUpdate(sqlCreateTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveObject(long id, String number, String pin, long balance) {

        String sqlCreateObject =
                "INSERT INTO cards(id,number, pin, balance)\n" +
                "VALUES\n" +
                "(?, ?, ?, ?);";

        try (Connection con = connect();
             PreparedStatement statement = con.prepareStatement(sqlCreateObject)) {

            statement.setLong(1, id);
            statement.setString(2, number);
            statement.setString(3, pin);
            statement.setLong(4, balance);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getLastIdValue() {
        String sqlGetMaxIdStatement = "SELECT MAX(id) as maxId FROM cards;";
        long maxIdValue = 0;

        try (Connection con = connect();
        Statement statement = con.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sqlGetMaxIdStatement);

            maxIdValue = resultSet.getLong("maxId");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxIdValue;
    }

    public Card getObject(String number) {

        String sqlGetObject =
                "SELECT *\n" +
                "FROM cards\n" +
                "WHERE number = ?;";

        try (Connection con = connect();
        PreparedStatement statement = con.prepareStatement(sqlGetObject)) {

            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()) {
                return mapToCardObject(resultSet);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashSet<Long> getAllId() {
        String sqlGetAllIds = "SELECT id FROM cards;";
        HashSet<Long> uniqueIdsSet = new HashSet<>();

        try (Connection con = connect();
            Statement statement = con.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sqlGetAllIds);
            while (resultSet.next()) {
                uniqueIdsSet.add(resultSet.getLong("id"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uniqueIdsSet;
    }

    public HashSet<String> getAllNumber() {
        String sqlGetAllNumbers = "SELECT number FROM cards;";
        HashSet<String> uniqueNumbersSet = new HashSet<>();

        try (Connection con = connect();
        Statement statement = con.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sqlGetAllNumbers);
            while (resultSet.next()) {
                uniqueNumbersSet.add(resultSet.getString("number"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uniqueNumbersSet;
    }

    public void deleteObjectFromTable(long id) {
        String sqlDeleteStatement =
                "DELETE\n" +
                "FROM cards\n" +
                "WHERE id = ?;";

        try (Connection con = connect();
        PreparedStatement statement = con.prepareStatement(sqlDeleteStatement)) {
            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Card mapToCardObject(ResultSet resultSet) throws SQLException {
        long tempId = resultSet.getLong("id");
        String number = resultSet.getString("number");
        String tempPin = resultSet.getString("pin");
        long balance = resultSet.getLong("balance");

        Card card = new Card(tempId, number, tempPin);
        card.setBalance(balance);

        return card;
    }

}
