# 財務管理システム

このJavaプログラムは、入金、出金、振り込みなどの様々な入出金管理を行うことができるシンプルな財務管理ソフトウェアです。
アカウント情報や取引履歴の表示、データのエクスポート・インポートなどの機能も提供しています。 <br>
<strong>(このソフトウェアは実際の銀行口座や銀行取引を行うものではありません)</strong>

## セキュリティについて
**注意:**
ソフトウェア内で取り扱うデータには機密情報が含まれます。
ユーザーはデータを公開せず、適切なセキュリティ対策を講じるようにしてください。

## 特徴

- **ユーザー認証:** ユーザーはユーザー名とパスワードでログインできます。
- **取引処理:** 入出金管理のための仮想的な預金、引き出し、振り込みの取引をサポートしています。
- **残高表示:** ユーザーは総口座残高を確認できます。
- **取引履歴:** タイプ、金額、説明、日付などの詳細が含まれる取引履歴を表示できます。

## コンパイル方法について
1. コンパイルスクリプト: リポジトリのルートディレクトリにあるCompile.batを使用してコンパイルし、実行可能なJARファイルにパッケージ化できます。
2. 必須ライブラリ: libディレクトリ内のlib-list.txtを参照し、必要なライブラリや関連ファイルをlibフォルダにコピーしてください。
3. MANIFESTファイルについて: 開発環境に応じてMANIFEST.MFファイルを調整してください。

## 使い方

1. **ログインまたは新しいユーザーの作成:** ログインオプションを選択するか、新しいユーザーアカウントを作成します。
2. **取引の実行:** 一度ログインすると、メニューオプションを使用して入出金情報の登録をしたり情報を表示したりできます。
3. **ログアウトまたは終了:** ログアウトオプションを選択してログアウトするか、プログラムを終了します。

## 最新リリース 
[最新リリース](https://github.com/Divings/java-Dir/releases/latest)

## 開始方法

### 必要なもの

- インストールされたJava Development Kit (JDK)
- SQLiteデータベース
- lib-list.txtに記載の依存関係ライブラリ<br>
(ライブラリは一部のJDKに対応していないものがある可能性があります)

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
1. **注意:** transaction_history.txtはすべての入出金記録が記録されているため、ダミーデータを設定した場合以外は公開しないでください 
2. mypay.db(ユーザーディレクトリに作成されます)ファイルは同様に重要な情報が含まれているため、第三者に公開しないでください
