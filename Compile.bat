@echo off
mkdir bin
javac -encoding UTF-8 -d bin src/*.java
jar cfm FMC.jar MANIFEST.MF -C bin .
pause