# 財務管理システム

このJavaプログラムは、入金、出金、振り込みなどの様々な入出金管理を行うことができるシンプルな財務管理ソフトウェアです。アカウント情報や取引履歴の表示、データのエクスポート・インポートなどの機能も提供しています。
(このソフトウェアは実際の銀行口座や銀行取引を行うものではありません)
## 特徴

- **ユーザー認証:** ユーザーはユーザー名とパスワードでログインできます。
- **取引処理:** 入出金管理のための仮想的な預金、引き出し、振り込みの取引をサポートしています。
- **残高表示:** ユーザーは総口座残高を確認できます。
- **取引履歴:** タイプ、金額、説明、日付などの詳細が含まれる取引履歴を表示できます。
- **データのエクスポート/インポート:** データのバックアップや復元のためにデータのエクスポートおよびインポートが可能です。

## 使い方

1. **ログインまたは新しいユーザーの作成:** ログインオプションを選択するか、新しいユーザーアカウントを作成します。
2. **取引の実行:** 一度ログインすると、メニューオプションを使用して取引を行ったり情報を表示したりできます。
3. **ログアウトまたは終了:** ログアウトオプションを選択してログアウトするか、プログラムを終了します。

## 開始方法

### 必要なもの

- インストールされたJava Development Kit (JDK)
- SQLiteデータベース

### セットアップ

1. リポジトリをクローン:

   ```bash
   git clone https://github.com/yourusername/FinancialManagementSystem.git