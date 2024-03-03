@echo off
setlocal

rem ターゲットディレクトリのパスを設定
set "targetDirectory=%USERPROFILE%\script-jar"

rem コピー元ディレクトリのパスを設定
set "sourceDirectory=指定のディレクトリのパス"

rem ターゲットディレクトリが存在しない場合は作成
if not exist "%targetDirectory%" (
    mkdir "%targetDirectory%"
)

rem コピー元ディレクトリの内容をターゲットディレクトリにコピー（構造を維持）
xcopy "%sourceDirectory%\*" "%targetDirectory%\" /E /I /H /K

echo "コピーが完了しました。"
