import java.util.Scanner;
import java.io.Console;

/**
 * 各種入力機能を提供するメソッド
 * 
 */

public class InputUtils {

    /**
     * ユーザーに入力を求めるメソッド
     *
     * @param prompt 入力を促すテキスト
     * @return 入力した文字列
     */
    public static String input(String prompt) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print(prompt);
            return scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 何らかのエラーが発生した場合は空文字列を返すなど、適切な処理を行う
        }
    }

    /**
    *パスワード入力用メソッド
    * @param pronpt 入力を促すメソッド
    * @return 入力したパスワード
    *  
    */
    public static String InputPassword(String prompt) {
        Console console = System.console();
        if (console == null) {
            throw new RuntimeException("Console not available.");
        }

        char[] passwordArray = console.readPassword(prompt);
        return new String(passwordArray);
    }
    
    public static int inputInt(String prompt) {
        try {
            String userInput = input(prompt);
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("整数を入力してください。デフォルト値 0 を返します。");
            return 0;
        }
    }

    public static double inputDouble(String prompt) {
        try {
            String userInput = input(prompt);
            return Double.parseDouble(userInput);
        } catch (NumberFormatException e) {
            System.out.println("数値を入力してください。デフォルト値 0.0 を返します。");
            return 0.0;
        }
    }
}
