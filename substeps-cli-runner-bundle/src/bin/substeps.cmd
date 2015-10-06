@ECHO OFF
SETLOCAL
SET PATH=%PATH%;%cd%\drivers
REM java "%*%" -classpath "properties/;lib/*" "uk.co.baconi.substeps.cli.SubstepsCommandLineRunner"
substeps.exe "%*%"
ENDLOCAL
PAUSE