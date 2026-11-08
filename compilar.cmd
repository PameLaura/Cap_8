@echo off
setlocal
rem Cap_8 usa JDK 21. Android Studio incluye OpenJDK 21.0.9 en esta carpeta.
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"
echo JDK usado para compilar Cap_8
"%JAVA_HOME%\bin\java.exe" -version
call gradlew.bat --version
if errorlevel 1 exit /b %errorlevel%
call gradlew.bat :app:assembleDebug --offline
exit /b %errorlevel%
