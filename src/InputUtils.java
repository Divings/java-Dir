import java.util.Scanner;

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

    // 他のユーティリティメソッドを追加できます

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

    // 他のユーティリティメソッドを追加できます

    public static void main(String[] args) {
        // テストコード
        String userInput = InputUtils.input("何か入力してください: ");
        System.out.println("入力した文字列: " + userInput);

        int userIntInput = InputUtils.inputInt("整数を入力してください: ");
        System.out.println("入力した整数: " + userIntInput);

        double userDoubleInput = InputUtils.inputDouble("小数を入力してください: ");
        System.out.println("入力した小数: " + userDoubleInput);
    }
}
