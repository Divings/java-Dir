import java.sql.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DebitCardManager {
    private static Connection connection;
    private static final String DATABASE_URL = "jdbc:sqlite:" + createData() + "mypay.db";
    private static final String EXPORT_PATH = createData() + "Export.txt";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);

            // トランザクション内での処理と排他処理
            connection.setAutoCommit(false);
            createTablesIfNotExist();
            connection.commit();
            connection.setAutoCommit(true);

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("エラー: 依存関係の解決に失敗しました");
            e.printStackTrace();
            return null;
        }
    }

    private static void createTransactionsTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username VARCHAR(255) NOT NULL,"
                    + "type VARCHAR(255),"
                    + "amount REAL,"
                    + "description TEXT,"
                    + "date VARCHAR(255))";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username VARCHAR(255) UNIQUE NOT NULL,"
                    + "password TEXT NOT NULL,"
                    + "account_number VARCHAR(255),"
                    + "account_description TEXT)";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createAccountsTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username VARCHAR(255) UNIQUE NOT NULL,"
                    + "balance REAL NOT NULL,"
                    + "description TEXT)";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDebitCardsTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS debit_cards ("
                    + "card_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_name VARCHAR(255),"
                    + "card_number VARCHAR(255),"
                    + "expiration_date VARCHAR(7),"
                    + "security_code VARCHAR(3)"
                    + ")";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist() {
        createTransactionsTable();
        createUsersTable();
        createAccountsTable();
        createDebitCardsTable();
    }

    public static void exportDatabase(String databaseName, String destinationFolderPath) {
        File sourceFile = new File(databaseName);
        File destinationFolder = new File(destinationFolderPath);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        File destinationFile = new File(destinationFolder, "mypay.db");

        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("データベースをエクスポートしました。");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("エクスポート中にエラーが発生しました。");
        }
    }

    private static final String CREATE_DEBIT_CARD_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS debit_cards (" +
                    "card_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_name VARCHAR(255)," +
                    "card_number VARCHAR(255)," +
                    "expiration_date VARCHAR(7)," +
                    "security_code VARCHAR(3)" +
                    ")";

    private static final String INSERT_CARD_SQL =
            "INSERT INTO debit_cards (user_name, card_number, expiration_date, security_code) VALUES (?, ?, ?, ?)";

    private static final String SELECT_CARD_SQL =
            "SELECT * FROM debit_cards WHERE user_name = ?";

    private static String createData() {
        String userDirectory = System.getProperty("user.home");
        String folderName = "FinancialManagement";
        String folderPath = userDirectory + File.separator + folderName;
        File folder = new File(folderPath);

        if (!folder.exists()) {
            if (folder.mkdir()) {
                return folderPath + File.separator;
            } else {
                return "";
            }
        } else {
            return folderPath + File.separator;
        }
    }

    public static void registerOrDisplayDebitCardInfo(String userName) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            // テーブルが存在しない場合に作成
            statement.execute(CREATE_DEBIT_CARD_TABLE_SQL);

            // データベースへの接続が成功したら、デビットカードの登録または表示を行う

            // ユーザー名に合致するデビットカード情報を取得
            try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_CARD_SQL)) {
                selectStatement.setString(1, userName);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    // データが存在する場合は表示
                    displayDebitCardInfo(resultSet);
                } else {
                    // データが存在しない場合は登録
                    String cardNumber = InputUtils.input(" card number >> ");
                    String expirationDate = InputUtils.input(" Date >> ");
                    String securityCode = InputUtils.input(" secure code >> ");
                    registerDebitCard(connection, userName, cardNumber, expirationDate, securityCode);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerDebitCard(Connection connection, String userName, String cardNumber, String expirationDate, String securityCode) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_CARD_SQL)) {
            insertStatement.setString(1, userName);
            insertStatement.setString(2, cardNumber);
            insertStatement.setString(3, expirationDate);
            insertStatement.setString(4, securityCode);
            insertStatement.executeUpdate();
            System.out.println("デビットカード情報が登録されました。");
        }
    }

    private static void displayDebitCardInfo(ResultSet resultSet) throws SQLException {
        do {
            int cardId = resultSet.getInt("card_id");
            String userName = resultSet.getString("user_name");
            String cardNumber = resultSet.getString("card_number");
            String expirationDate = resultSet.getString("expiration_date");
            String securityCode = resultSet.getString("security_code");

            System.out.println("Card ID: " + cardId);
            System.out.println("User Name: " + userName);
            System.out.println("Card Number: " + cardNumber);
            System.out.println("Expiration Date: " + expirationDate);
            System.out.println("Security Code: " + securityCode);
        } while (resultSet.next());
    }

    public static void exportText(String targetUser) {
        try (Connection connection = connect()) {
            exportData(connection, targetUser, createData() + "account_data.txt", "SELECT * FROM accounts WHERE username=?");
            exportData(connection, targetUser, createData() + "debit_card_data.txt", "SELECT * FROM debit_cards WHERE user_name=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportData(Connection connection, String targetUser, String fileName, String sqlQuery)
            throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, targetUser);
            ResultSet resultSet = preparedStatement.executeQuery();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                while (resultSet.next()) {
                    // Extract columns dynamically
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        String columnName = resultSet.getMetaData().getColumnName(i);
                        String columnValue = resultSet.getString(i);
                        writer.write(columnName + ": " + columnValue);
                        writer.newLine();
                    }
                    writer.newLine(); // Add a separator between entries
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
