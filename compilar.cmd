@echo off
setlocal
rem Usa JAVA_HOME si ya esta configurado, o el JDK incluido en Android Studio.
rem El proyecto requiere JDK 21 para el daemon de Gradle.
if defined JAVA_HOME goto comprobarJava
set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
:comprobarJava
if exist "%JAVA_HOME%\bin\java.exe" goto compilar
echo Configura JAVA_HOME con la carpeta de tu JDK 21 y vuelve a ejecutar.
exit /b 1
:compilar
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"
echo JDK usado para compilar Cap_8
"%JAVA_HOME%\bin\java.exe" -version
call gradlew.bat --version
if errorlevel 1 exit /b %errorlevel%
call gradlew.bat :app:assembleDebug %*
exit /b %errorlevel%
