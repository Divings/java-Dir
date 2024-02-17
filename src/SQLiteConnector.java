import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLiteデータベースにアクセスするクラス
 */
public class SQLiteConnector {
	private static Connection connection;

	public static Connection connect() {
		String drivers;
		try {
			String userDir = createData();
			if (userDir.equals("")) {
				System.out.println("エラー: データフォルダの作成に失敗しました");
				drivers = "jdbc:sqlite:mypay.db";
			} else {
				drivers = "jdbc:sqlite:" + userDir + "mypay.db";
			}

			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(drivers);
			System.out.println("Connected to SQLite database.");

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
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "username TEXT NOT NULL," + "type TEXT,"
					+ "amount REAL," + "description TEXT," + "date TEXT)";
			statement.executeUpdate(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createUsersTable() {
		try (Statement statement = connection.createStatement()) {
			String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "username TEXT UNIQUE NOT NULL," + "password TEXT NOT NULL," + "account_number TEXT,"
					+ "account_description TEXT)";
			statement.executeUpdate(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createAccountsTable() {
		try (Statement statement = connection.createStatement()) {
			String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "username TEXT UNIQUE NOT NULL," + "balance REAL NOT NULL," + "description TEXT)";
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
				return folderPath + "/";
			} else {
				return "";
			}
		} else {
			return folderPath + "/";
		}
	}

	public static void exportDatabase(String outputPath) {
		try {
			File outputFile = new File(outputPath);
			if (!outputFile.exists() && !outputFile.createNewFile()) {
				System.err.println("Error: Cannot create the output file.");
				return;
			}

			if (!outputFile.canWrite()) {
				System.err.println("Error: No write permission for the specified output file.");
				return;
			}

			String sourcePath = createData() + "mypay.db";

			ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", sourcePath, ".dump");
			processBuilder.redirectOutput(outputFile);

			Process process = processBuilder.start();
			process.waitFor();

			System.out.println("Database exported successfully to: " + outputPath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void importDatabase(String inputPath) {
		try {
			String targetPath = createData() + "mypay.db";

			File targetFile = new File(targetPath);
			if (!targetFile.exists() && !targetFile.createNewFile()) {
				System.err.println("Error: Cannot create the target file.");
				return;
			}

			if (!targetFile.canWrite()) {
				System.err.println("Error: No write permission for the specified target file.");
				return;
			}

			ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", targetPath, ".read", inputPath);

			Process process = processBuilder.start();
			process.waitFor();

			System.out.println("Database imported successfully from: " + inputPath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
