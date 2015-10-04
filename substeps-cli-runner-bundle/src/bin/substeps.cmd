@ECHO OFF
SETLOCAL
SET PATH=%PATH%;%cd%\drivers
REM java "%*%" -classpath "properties/;lib/*" "co.uk.baconi.substeps.cli.SubstepsCommandLineRunner"
substeps.exe "%*%"
ENDLOCAL
PAUSE