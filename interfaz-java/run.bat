@echo off
echo Ejecutando Sistema de Deteccion de Caidas - Interfaz Java
echo =========================================================

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

REM Verificar si existe el directorio bin
if not exist "bin" (
    echo Error: Directorio 'bin' no encontrado
    echo Ejecute primero: compile.bat
    pause
    exit /b 1
)

REM Verificar si existen archivos .class
if not exist "bin\PanelMonitoreo.class" (
    echo Error: Archivos .class no encontrados
    echo Ejecute primero: compile.bat
    pause
    exit /b 1
)

echo Iniciando aplicacion...
echo.
java -cp "lib/jakarta.mail-2.0.1.jar;%SQLITEJAR%;bin" PanelMonitoreo

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error al ejecutar la aplicacion!
    echo Verifique que Java 17+ este instalado y en el PATH
    echo.
)

pause 