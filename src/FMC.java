
// -*- coding: utf-8 -*-
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.InputMismatchException;

public class FMC {
	private static String loggedInUser = null;
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in); // 新しいScannerオブジェクトを作成

		try {
			// Connect to the database
			Connection connection = SQLiteConnector.connect();
			if (connection==null){
				InputUtils.input(" >> ");
				System.exit(0);
			}
			clearConsole();
			if (connection != null) {
				try {
					while (true) {
						if (loggedInUser == null) {
							displayLoginMenu();
                            int loginChoice=0;
                            try{
							loginChoice = scanner.nextInt();
							scanner.nextLine(); // Consume newline
						}catch (InputMismatchException e) {
							System.out.print("\n エラー許容されない値が入力されました");
							InputUtils.input(" >> ");
							continue;
						}
							System.out.println("");
							clearConsole();
							switch (loginChoice) {
							case 1:
								boolean loginvalue = false;
								int count = 0;
								while (loginvalue = true) {
									if (count >= 3) {
										System.out
												.println("Password authentication failed 3 times. Exit the software.");
										System.out.println("Exiting the program due to login failure.");
										InputUtils.input(" >> ");
										System.exit(0);
									}
									loginvalue = loginScreen(connection, scanner);
									if (loginvalue == true) {

										break;
									}
									if (!loginvalue) {
										count = count + 1;
										System.out.println("certification failed");
										InputUtils.input(" >> ");
										clearConsole();
										continue;
									}
								}
								break;

							case 2:
								createNewUserScreen(connection, scanner);
								continue;
							case 3:
								System.out.println("Exiting the program.");
								return;
							default:
								System.out.println("Invalid choice. Please select a valid option.");
								break;
							}
						}
						displayMenuOptions("Main Menu");
						int choice = getChoiceFromUser(scanner);
						switch (choice) {
						case 1:
							try {
								depositTransaction(connection);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							break;
						case 2:
							try {
								withdrawalTransaction(connection);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							break;
						case 3:
							try {
								transferTransaction(connection);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							break;
						case 4:
							displayTotalAmount(connection);
							break;
						case 5:
							displayTransactionHistory(connection);
							break;
						case 6:
							loggedInUser = null;
							System.out.println("Logout successful.");
							break;
						case 7:
							displayAccountInformation(connection);
							break;
					    case 8:
                        	
                        	// 例：ユーザー名が存在しない場合は登録、存在する場合は表示
                            DebitCardManager.registerOrDisplayDebitCardInfo(loggedInUser);
                            InputUtils.input(" >> ");
                            clearConsole();
                            break;
                        case 9:
                        	DebitCardManager.exportText(loggedInUser);
                        	InputUtils.input(" >> "); // ユーザーに確認(特に戻り値は取得しない)
				            clearConsole();// 画面を初期化
				            break;
                        case 10:
							System.out.println("Exiting the program.");
							return;
						default:
							System.out.println("Invalid choice. Please select a valid option.");
							InputUtils.input(" >> "); // ユーザーに確認(特に戻り値は取得しない)
				            clearConsole();// 画面を初期化
							break;
						}
					}
				} finally {
					// Disconnect from the database
					SQLiteConnector.disconnect();
				}
			}
		} finally {
			scanner.close(); // メソッド終了時にScannerをクローズ
		}
	}

	/**
	 * ログイン用メソッド
	 */
	private static boolean login(Connection connection, String username, String password) {
		try {
			String selectUserSQL = "SELECT * FROM users WHERE username=? AND password=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				ResultSet resultSet = preparedStatement.executeQuery();
				return resultSet.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

// 入金記録用メソッド
	private static void depositTransaction(Connection connection) throws IOException {
		Scanner scanner = new Scanner(System.in);

		try {
			System.out.print("Enter deposit amount: ");
			BigDecimal depositAmount = new BigDecimal(scanner.nextLine());

			System.out.print("Enter description: ");
			String description = scanner.nextLine();

			// Get current balance from the database
			BigDecimal currentBalance = getCurrentBalance(connection, loggedInUser);

			// Calculate new balance after deposit
			BigDecimal newBalance = currentBalance.add(depositAmount);

			// Update balance in the 'accounts' table
			updateBalance(connection, loggedInUser, newBalance);

			// Insert deposit transaction into the database
			String insertTransactionSQL = "INSERT INTO transactions (username, type, amount, description, date) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
				preparedStatement.setString(1, loggedInUser);
				preparedStatement.setString(2, "Deposit");
				preparedStatement.setBigDecimal(3, depositAmount);
				preparedStatement.setString(4, description);
				preparedStatement.setString(5, getCurrentDate());
				preparedStatement.executeUpdate();
				System.out.println("Deposit completed. New balance: " + newBalance);

				// Output to text file
				outputToTextFile("Deposit", depositAmount, description);
			}

			InputUtils.input(" >> ");
			clearConsole();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Helper method to update balance in the 'accounts' table
	private static void updateBalance(Connection connection, String username, double newBalance) throws SQLException {
		String updateBalanceSQL = "UPDATE accounts SET balance=? WHERE username=?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceSQL)) {
			preparedStatement.setDouble(1, newBalance);
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * 出金メソッド
	 */
	private static void withdrawalTransaction(Connection connection) throws IOException {
		Scanner scanner = new Scanner(System.in);

		try {
			System.out.print("Enter withdrawal amount: ");
			BigDecimal withdrawalAmount = new BigDecimal(scanner.nextLine());

			System.out.print("Enter description: ");
			String description = scanner.nextLine();

			// Get current balance from the 'accounts' table
			BigDecimal currentBalance = getCurrentBalance(connection, loggedInUser);

			// Check if withdrawal amount is valid
			if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
				System.out.println("Invalid withdrawal amount. Amount must be greater than 0.");
				return;
			}

			// Check if sufficient balance is available
			if (withdrawalAmount.compareTo(currentBalance) > 0) {
				System.out.println("Insufficient funds. Withdrawal amount exceeds available balance.");
				return;
			}

			// Calculate new balance after withdrawal
			BigDecimal newBalance = currentBalance.subtract(withdrawalAmount);

			// Update balance in the 'accounts' table
			updateBalance(connection, loggedInUser, newBalance);

			// Insert withdrawal transaction into the database
			String insertTransactionSQL = "INSERT INTO transactions (username, type, amount, description, date) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
				preparedStatement.setString(1, loggedInUser);
				preparedStatement.setString(2, "Withdrawal");
				preparedStatement.setBigDecimal(3, withdrawalAmount.negate()); // Withdrawal is recorded as negative
				preparedStatement.setString(4, description);
				preparedStatement.setString(5, getCurrentDate());
				preparedStatement.executeUpdate();
				System.out.println("Withdrawal completed. New balance: " + newBalance);

				// Output to text file
				outputToTextFile("Withdrawal", withdrawalAmount, description);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 送金管理メソッド
	 */
	private static void transferTransaction(Connection connection) throws IOException {
		Scanner scanner = new Scanner(System.in);

		try {
			System.out.print("Enter transfer amount: ");
			BigDecimal transferAmount = new BigDecimal(scanner.nextLine());

			System.out.print("Enter description: ");
			String description = scanner.nextLine();

			// Get current balance from the 'accounts' table
			BigDecimal currentBalance = getCurrentBalance(connection, loggedInUser);

			// Check if transfer amount is valid
			if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
				System.out.println("Invalid transfer amount. Amount must be greater than 0.");
				return;
			}

			// Check if sufficient balance is available
			if (transferAmount.compareTo(currentBalance) > 0) {
				System.out.println("Insufficient funds. Transfer amount exceeds available balance.");
				return;
			}

			// Calculate new balance after transfer
			BigDecimal newBalance = currentBalance.subtract(transferAmount);

			// Update balance in the 'accounts' table
			updateBalance(connection, loggedInUser, newBalance);

			// Insert transfer transaction into the database
			String insertTransactionSQL = "INSERT INTO transactions (username, type, amount, description, date) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
				preparedStatement.setString(1, loggedInUser);
				preparedStatement.setString(2, "Transfer");
				preparedStatement.setBigDecimal(3, transferAmount.negate()); // Transfer is recorded as negative
				preparedStatement.setString(4, description);
				preparedStatement.setString(5, getCurrentDate());
				preparedStatement.executeUpdate();
				System.out.println("Transfer completed. New balance: " + newBalance);

				// Output to text file
				outputToTextFile("Transfer", transferAmount, description);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合計額表示
	 */
	
    private static void updateAccountBalance(Connection connection) {
    	BigDecimal newBalance=OutputAmount(connection);
    try {
        String updateBalanceSQL = "UPDATE accounts SET balance = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceSQL)) {
            preparedStatement.setBigDecimal(1, newBalance);
            preparedStatement.setString(2, loggedInUser);

            int rowsAffected = preparedStatement.executeUpdate();

            }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static BigDecimal OutputAmount(Connection connection){
	BigDecimal totalAmount=BigDecimal.ZERO;
	try {
			String selectTotalAmountSQL = "SELECT SUM(amount) FROM transactions WHERE username=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(selectTotalAmountSQL)) {
				preparedStatement.setString(1, loggedInUser);
				ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
					totalAmount = resultSet.getBigDecimal(1);

					/*// 厳密な価格表示
					NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
					String formattedTotalAmount = currencyFormat.format(totalAmount);

					System.out.println("Total Amount: " + formattedTotalAmount);*/
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalAmount;
}
	private static void displayTotalAmount(Connection connection) {
		try {
			String selectTotalAmountSQL = "SELECT SUM(amount) FROM transactions WHERE username=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(selectTotalAmountSQL)) {
				preparedStatement.setString(1, loggedInUser);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					BigDecimal totalAmount = resultSet.getBigDecimal(1);

					// 厳密な価格表示
					NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
					String formattedTotalAmount = currencyFormat.format(totalAmount);

					System.out.println("Total Amount: " + formattedTotalAmount);
				}
				InputUtils.input(" >> ");
				clearConsole();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 履歴をファイルに出力
	 */
private static void displayTransactionHistory(Connection connection) {
    try {
        String selectTransactionHistorySQL = "SELECT * FROM transactions WHERE username=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTransactionHistorySQL)) {
            preparedStatement.setString(1, loggedInUser);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Transaction History:");

            String userDirectory = userData();
            if (userDirectory.isEmpty()) {
                System.out.println("Error: Unable to create data folder.");
                return;
            }

            // ファイルパスを指定
            String filePath = userDirectory + "transaction_history.txt";

            // BufferedWriterを使ってファイルに追記
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String type = resultSet.getString("type");
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    String description = resultSet.getString("description");
                    String date = resultSet.getString("date");

                    // コンソールに表示
                    System.out.println("ID: " + id + ", Type: " + type + ", Amount: " + formatAmount(amount)
                            + ", Description: " + description + ", Date: " + date);

                    // ファイルに追記
                    writer.write("ID: " + id + ", Type: " + type + ", Amount: " + formatAmount(amount)
                            + ", Description: " + description + ", Date: " + date);
                    writer.newLine();
                }
            }
            InputUtils.input(" >> ");
            clearConsole();
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
    }
}

	private static String formatAmount(BigDecimal amount) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		return decimalFormat.format(amount);
	}

	/**
	 * テキストファイル出力メソッド
	 * 
	 * @param タイプ、合計額、説明
	 */
	private static void outputToTextFile(String type, BigDecimal depositAmount, String description) {
		try {
			String folderPath = userData(); // ユーザーディレクトリ下のFinancialManagementフォルダ
			String filePath = folderPath + "transaction_history.txt";

			// ファイルに対する書き込み権限の確認
			File file = new File(filePath);
			if (!file.exists() && !file.createNewFile()) {
				System.err.println("Error: Cannot create the output file.");
				return;
			}

			if (!file.canWrite()) {
				System.err.println("Error: No write permission for the specified output file.");
				return;
			}

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
				writer.write("Type: " + type + ", Amount: " + formatAmount(depositAmount) + ", Description: "
						+ description + ", Date: " + getCurrentDate());
				writer.newLine();
			}

			System.out.println("Data written to file: " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 現在日時取得メソッド
	 */
	private static String getCurrentDate() {
		java.util.Date utilDate = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return sqlDate.toString();
	}

	/**
	 * ログイン画面
	 */
	private static void displayLoginMenu() {
		clearConsole();
		System.out.println("===== Login Menu =====");
		System.out.println("1. Login");
		System.out.println("2. Create New User");
		System.out.println("3. Exit");
		System.out.print("Select an option: ");
	}

	/**
	 * ログイン画面
	 * 
	 * @return Boolian ログインに成功した場合はtrue
	 */
	private static boolean loginScreen(Connection connection, Scanner scanner) {
		System.out.println("===== Login =====");
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		String password = InputUtils.InputPassword("Enter password: ");
		boolean out = false;
		if (login(connection, username, password)) {
			loggedInUser = username;
			clearConsole();
			out = true;
		} else {
			System.out.println("Login failed. Please try again.");
			out = false;
		}
		return out;
	}

// New Account
	private static void createNewUserScreen(Connection connection, Scanner scanner) {
		System.out.println("===== Create New User =====");
		System.out.print("Enter new username: ");
		String newUsername = scanner.next();
		System.out.print("Enter new password: ");
		String newPassword = InputUtils.InputPassword("");
		System.out.print("Enter account number: ");
		String newAccountNumber = scanner.next();
		String description = InputUtils.input(" description >> ");
		if (createUser(connection, newUsername, newPassword, newAccountNumber, description)) {
			try {
				depositTransaction(connection, newUsername);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			System.out.println("User created successfully. Please login.");
		} else {
			System.out.println("User creation failed. Please try again.");
		}
	}

	/**
	 * ユーザー作成用メソッド
	 * 
	 * @param Sqlite接続変数 ユーザー名 パスワード 口座番号
	 */
	private static boolean createUser(Connection connection, String username, String password, String accountNumber,
			String accountDescription) {
		try {
			String insertUserSQL = "INSERT INTO users (username, password, account_number, account_description) VALUES (?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				preparedStatement.setString(3, accountNumber);
				preparedStatement.setString(4, accountDescription); // 新しい列 account_description を追加
				preparedStatement.executeUpdate();

				// 同時にaccountsテーブルにもアカウントを作成
				String insertAccountSQL = "INSERT INTO accounts (username, balance, description) VALUES (?, 0, ?)";
				try (PreparedStatement accountStatement = connection.prepareStatement(insertAccountSQL)) {
					accountStatement.setString(1, username);
					accountStatement.setString(2, accountDescription);
					accountStatement.executeUpdate();
				}

				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 口座情報の一覧メソッド
	 * 
	 * @param SQLiteの接続変数
	 */
	private static void displayAccountInformation(Connection connection) {
		try {
			String selectAccountInfoSQL = "SELECT username, account_number, account_description FROM users WHERE username=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(selectAccountInfoSQL)) {
				preparedStatement.setString(1, loggedInUser);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					String accountNumber = resultSet.getString("account_number");
					String accountDescription = resultSet.getString("account_description");

					System.out.println("Account Information:");
					System.out.println("Username: " + loggedInUser);
					System.out.println("Account Number: " + accountNumber);
					System.out.println("Account Description: " + accountDescription);
				}
				InputUtils.input(" >> ");
				clearConsole();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String userData() {
		// ユーザーディレクトリを取得
		String userDirectory = System.getProperty("user.home");

		// 作成するフォルダ名
		String folderName = "FinancialManagement";

		// ユーザーディレクトリ下に新しいフォルダを作成
		String folderPath = userDirectory + File.separator + folderName;
		File folder = new File(folderPath);

		// フォルダが存在しない場合は作成
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

	/**
	 * 画面初期化メソッド
	 */
	public static void clearConsole() {
		try {
			String os = System.getProperty("os.name").toLowerCase();

			// Check the operating system and execute the appropriate command
			ProcessBuilder processBuilder;
			if (os.contains("win")) {
				// For Windows
				processBuilder = new ProcessBuilder("cmd", "/c", "cls");
			} else {
				// For Unix-like systems (Linux, macOS)
				processBuilder = new ProcessBuilder("clear");
			}

			Process process = processBuilder.inheritIO().start();
			process.waitFor(); // Wait for the process to complete

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void displayMenuOptions(String menuTitle) {
		updateAccountBalance(DebitCardManager.connect());
		System.out.println("Login successful. Welcome, " + loggedInUser + "!");
		System.out.println("\n (C) Innovation Craft");
		System.out.println("===== " + menuTitle + " =====");
		// Display menu options
		System.out.println("1. 入金設定 (Deposit)");
		System.out.println("2. 出金設定 (Withdrawal)");
		System.out.println("3. 送金設定 (Transfer)");
		System.out.println("4. 合計金額を表示 (Display Total Amount)");
		System.out.println("5. 取引履歴の表示 (Display Transaction History)");
		System.out.println("6. ログアウト (Logout)");
		System.out.println("7. アカウント情報の表示 (Display Account Information)");
		System.out.println("8. Card DATA");
		System.out.println("9. ALL DATA Export");
		System.out.println("10. 終了 (Exit)");
		System.out.print("Select an option: ");
	}

	private static int getChoiceFromUser(Scanner scanner) {
		int choice = 0;
		try {
			System.out.print("Select an option: ");
			String line = scanner.nextLine(); // 改行文字をクリア
			choice = Integer.parseInt(line.trim()); // 文字列から整数に変換
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid integer.");
		}
		return choice;
	}

	private static void depositTransaction(Connection connection, String Username) throws IOException {
		Scanner scanner = new Scanner(System.in); // 新しいScannerオブジェクトを作成

		try {
			System.out.print("Enter deposit amount: ");
			BigDecimal amount = scanner.nextBigDecimal();
			scanner.nextLine(); // Consume newline

			System.out.print("Enter description: ");
			String description = scanner.nextLine();

			// Insert deposit transaction into the database
			String insertTransactionSQL = "INSERT INTO transactions (username, type, amount, description, date) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
				preparedStatement.setString(1, Username);
				preparedStatement.setString(2, "Deposit");
				preparedStatement.setBigDecimal(3, amount);
				preparedStatement.setString(4, description);
				preparedStatement.setString(5, getCurrentDate());
				preparedStatement.executeUpdate();
				System.out.println("Deposit completed.");

				// Output to text file
				outputToTextFile("Deposit", amount, description);
			}
			InputUtils.input(" >> ");
			clearConsole();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// scanner.close(); // メソッド終了時にScannerをクローズ
		}
	}

	private static BigDecimal getCurrentBalance(Connection connection, String username) throws SQLException {
		String selectBalanceSQL = "SELECT balance FROM accounts WHERE username=?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(selectBalanceSQL)) {
			preparedStatement.setString(1, username);
			return getBigDecimalResult(preparedStatement);
		}
	}

	private static void updateBalance(Connection connection, String username, BigDecimal newBalance)
			throws SQLException {
		String updateBalanceSQL = "UPDATE accounts SET balance=? WHERE username=?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceSQL)) {
			preparedStatement.setBigDecimal(1, newBalance);
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();
		}
	}

	private static BigDecimal getBigDecimalResult(PreparedStatement preparedStatement) throws SQLException {
		try (ResultSet resultSet = preparedStatement.executeQuery()) {
			if (resultSet.next()) {
				return resultSet.getBigDecimal(1);
			} else {
				throw new SQLException("User not found in the accounts table");
			}
		}
	}
}
