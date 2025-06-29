@echo off
echo Compilando Sistema de Deteccion de Caidas - Interfaz Java
echo ========================================================

REM Crear directorio bin si no existe
if not exist "bin" mkdir bin

REM Buscar el driver de SQLite en la carpeta lib
set "SQLITEJAR="
for %%f in (lib\sqlite-jdbc*.jar) do (
    set "SQLITEJAR=%%f"
    goto :found
)
:found

if "%SQLITEJAR%"=="" (
    echo Error: No se encontro el driver sqlite-jdbc en la carpeta lib
    echo Descargalo de: https://github.com/xerial/sqlite-jdbc/releases
    pause
    exit /b 1
)

REM Compilar todos los archivos Java con el driver en el classpath
echo Compilando archivos Java...
javac -cp "lib/jakarta.mail-2.0.1.jar;%SQLITEJAR%;bin" -d bin src/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilacion exitosa!
    echo Los archivos .class se encuentran en el directorio 'bin'
    echo.
    echo Para ejecutar la aplicacion:
    echo   java -cp "%SQLITEJAR%;bin" PanelMonitoreo
    echo.
) else (
    echo.
    echo Error en la compilacion!
    echo Verifique que Java 17+ este instalado y en el PATH
    echo.
)

pause 