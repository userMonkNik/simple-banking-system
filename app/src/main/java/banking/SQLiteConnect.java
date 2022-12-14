package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.HashSet;

public class SQLiteConnect {
    public static String dbName;
    public SQLiteConnect() {
        createTableIfNotExist();
    }

    public  Connection connect() {
        setDbNameIfNotExists();
        String url = "jdbc:sqlite:" + dbName;

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

    private void setDbNameIfNotExists() {
        if (dbName == null) {
            dbName = "bank.db";
        }
    }

    public void createTableIfNotExist() {

        String sqlCreateTable =
                "CREATE TABLE IF NOT EXISTS 'card' (\n" +
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
                "INSERT INTO card(id,number, pin, balance)\n" +
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

    public void updateBalance(long income, String number) {

        String sqlUpdateBalance =
                "UPDATE card\n" +
                "SET balance = balance + ?\n" +
                "WHERE number = ?;";

        try (Connection con = connect();
        PreparedStatement statement = con.prepareStatement(sqlUpdateBalance)) {

            statement.setLong(1, income);
            statement.setString(2, number);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean transactionalTransferMoney(String currentUserNumber,String recipientNumber, long balance) {

        String sqlUpdateCurrentUserBalance =
                "UPDATE card\n" +
                "SET balance = balance - ?\n" +
                "WHERE number = ?;";
        String sqlUpdateRecipientBalance =
                "UPDATE card\n" +
                "SET balance = balance + ?\n" +
                "WHERE number = ?;";

        try (Connection con = connect()) {

            con.setAutoCommit(false);

            try (PreparedStatement updateCurrentUserStatement = con.prepareStatement(sqlUpdateCurrentUserBalance);
            PreparedStatement updateRecipientStatement = con.prepareStatement(sqlUpdateRecipientBalance)) {

                updateCurrentUserStatement.setLong(1, balance);
                updateCurrentUserStatement.setString(2, currentUserNumber);
                updateCurrentUserStatement.executeUpdate();

                updateRecipientStatement.setLong(1, balance);
                updateRecipientStatement.setString(2, recipientNumber);
                updateRecipientStatement.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getLastIdValue() {

        String sqlGetMaxId = "SELECT MAX(id) as maxId FROM card;";
        long maxIdValue = 0;

        try (Connection con = connect();
        Statement statement = con.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sqlGetMaxId);

            maxIdValue = resultSet.getLong("maxId");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxIdValue;
    }

    public Card getObject(String number) {

        String sqlGetObject =
                "SELECT *\n" +
                "FROM card\n" +
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

        String sqlGetAllIds = "SELECT id FROM card;";
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

        String sqlGetAllNumbers = "SELECT number FROM card;";
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

    public void deleteObjectFromTable(String number) {

        String sqlDeleteObject =
                "DELETE\n" +
                "FROM card\n" +
                "WHERE number = ?;";

        try (Connection con = connect();
        PreparedStatement statement = con.prepareStatement(sqlDeleteObject)) {
            statement.setString(1, number);
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
