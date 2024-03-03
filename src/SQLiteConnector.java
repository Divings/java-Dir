import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLiteデータベースにアクセスするクラス
 * 
 * @owner Koga
 */
public class SQLiteConnector {
    private static Connection connection;
    private static final String DATABASE_PATH = createData() + "mypay.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
            System.out.println("\nSQLiteデータベースに接続しました。");

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
                    + "username TEXT NOT NULL,"
                    + "type TEXT,"
                    + "amount REAL,"
                    + "description TEXT,"
                    + "date TEXT)";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT UNIQUE NOT NULL,"
                    + "password TEXT NOT NULL,"
                    + "account_number TEXT,"
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
                    + "username TEXT UNIQUE NOT NULL,"
                    + "balance REAL NOT NULL,"
                    + "description TEXT)";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist() {
        createTransactionsTable();
        createUsersTable();
        createAccountsTable();
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}
