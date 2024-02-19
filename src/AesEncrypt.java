import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

public class AesEncrypt {

    private static final String KEY_FILE_PATH = createData()+"encryption_key.key";

    public static String Decrypt(String filePath) throws IOException, NoSuchAlgorithmException, Exception {
    String decryptedText = "";
    SecretKey secretKey = null;

    try {
        // テキストファイルの内容を読み込む
        String ciphertext = readFile(filePath);

        secretKey = getOrCreateAESKey();

        // テキストをAESで復号化
        decryptedText = decryptAES(ciphertext, secretKey);

        // 復号化されたテキストをファイルに書き込む
        writeFile(filePath, decryptedText);
        System.out.println("Text file Decrypted successfully.");
    } catch (Exception e) {
        // 例外を捕捉してログに出力など、適切に処理する
        e.printStackTrace();
        throw new Exception("Error in Decrypt", e);
    }

    return decryptedText;
}


    public static void Encrypt(String filePath){
        try {
            // テキストファイルの内容を読み込む
            String plaintext = readFile(filePath);

            // AES秘密鍵の取得または生成
            SecretKey secretKey = getOrCreateAESKey();
            // テキストをAESで暗号化
            String encryptedText = encryptAES(plaintext, secretKey);
            // 暗号化されたテキストをファイルに書き込む
            writeFile(filePath, encryptedText);
            System.out.println("Text file encrypted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
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

private static SecretKey getOrCreateAESKey() throws IOException, NoSuchAlgorithmException, Exception {
    Path keyFilePath = Paths.get(KEY_FILE_PATH);
    SecretKey secretKey = null;

    try {
        if (Files.exists(keyFilePath)) {
            // ファイルが存在する場合はファイルから秘密鍵を読み込む
            byte[] keyBytes = Files.readAllBytes(keyFilePath);
            secretKey = new SecretKeySpec(keyBytes, "AES");
        } else {
            // ファイルが存在しない場合は新しい秘密鍵を生成し、ファイルに保存する
            secretKey = generateAESKey();
            byte[] keyBytes = secretKey.getEncoded();
            Files.write(keyFilePath, keyBytes);
        }
    } catch (Exception e) {
        // 例外を捕捉してログに出力など、適切に処理する
        e.printStackTrace();
        throw new Exception("Error in getOrCreateAESKey", e);
    }

    return secretKey;
}

    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        try{
        keyGenerator.init(128);
        }catch( Exception e){
            e.printStackTrace();
        }
        return keyGenerator.generateKey();
    }

    private static String encryptAES(String plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptAES(String ciphertext, SecretKey secretKey) {
    try {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Base64デコード
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
            | BadPaddingException | IllegalBlockSizeException e) {
        e.printStackTrace();
        return "";  // エラー時は空文字列を返すか、適切なエラー処理を行う
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return "";  // エラー時は空文字列を返すか、適切なエラー処理を行う
    }
}
}
