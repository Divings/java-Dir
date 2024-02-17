@echo off
javac -encoding UTF-8 -d bin src/*.java
jar cfm FinancialManagementSystem.jar MANIFEST.MF -C bin .
pause