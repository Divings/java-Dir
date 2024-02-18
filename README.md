# 財務管理システム

このJavaプログラムは、入金、出金、振り込みなどの様々な入出金管理を行うことができるシンプルな財務管理ソフトウェアです。
アカウント情報や取引履歴の表示、データのエクスポート・インポートなどの機能も提供しています。 
(このソフトウェアは実際の銀行口座や銀行取引を行うものではありません)

## 特徴

- **ユーザー認証:** ユーザーはユーザー名とパスワードでログインできます。
- **取引処理:** 入出金管理のための仮想的な預金、引き出し、振り込みの取引をサポートしています。
- **残高表示:** ユーザーは総口座残高を確認できます。
- **取引履歴:** タイプ、金額、説明、日付などの詳細が含まれる取引履歴を表示できます。
- **データのエクスポート/インポート:** データのバックアップや復元のためにデータのエクスポートおよびインポートが可能です。

## コンパイル方法について
1. **コンパイルスクリプト:** Bat-Scriptフォルダ内に私が使用しているコンパイル+実行可能JARにパッケージ化したり、実行するバッチファイル等が配置されています。
(リポジトリのルートディレクトリにコピーして使用してください)
2. **必須ライブラリ:** libディレクトリ内のlib-list.txtを参照し、必須ライブラリや関連ファイルをlibフォルダにコピーしてください。
3. **MANIFESTファイルについて:** デフォルトの開発環境としてMANIFEST.MFファイルを用意してあります。追加のライブラリがありましたら、こちらを変更してください

## 使い方

1. **ログインまたは新しいユーザーの作成:** ログインオプションを選択するか、新しいユーザーアカウントを作成します。
2. **取引の実行:** 一度ログインすると、メニューオプションを使用して入出金情報の登録をしたり情報を表示したりできます。
3. **ログアウトまたは終了:** ログアウトオプションを選択してログアウトするか、プログラムを終了します。

## 開始方法

### 必要なもの

- インストールされたJava Development Kit (JDK)
- SQLiteデータベース
- lib-list.txtに記載の依存関係ライブラリ

### セットアップ

(windowsの場合を想定、そしてjava-JreやJDKは別途インストールしてください)
1. リポジトリをクローン:

   ```bash
   git clone https://github.com/Divings/java-Dir.git
2. binフォルダを作成します。

   ```bash
   mkdir bin
3. Comple.batを使用してコンパイルします。それか次のコマンドを使用してコンパイルしてJARファイルを作成してください

   ```bash
   javac -encoding UTF-8 -d bin src/*.java
   jar cfm FinancialManagementSystem.jar MANIFEST.MF -C bin .
   
   ```cmd
   Compile.bat

4. 実行する
   
   ```bash
   java -jar FinancialManagementSystem.jar

## 注意:
1. **注意:** transaction_history.txtはすべての入出金記録が記録されているため、ダミーデータを設定した場合以外は公開しない方が良いでしょう  


