@echo off
rem ---------------------------------------------------------------------------
rem Main Script for Softclicker
rem
rem Environment Variable Prequisites
rem
rem   SOFTCLICKER_HOME   Home of SOFTCLICKER installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------
:checkJava

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto checkServer

:noJavaHome
echo "You must set the JAVA_HOME variable before running SOFTCLICKER."
goto end

rem ----- Only set SOFTCLICKER_HOME if not already set ----------------------------
:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
if "%SOFTCLICKER_HOME%"=="" set SOFTCLICKER_HOME=%~sdp0..
SET curDrive=%cd:~0,1%
SET wsasDrive=%SOFTCLICKER_HOME:~0,1%
if not "%curDrive%" == "%wsasDrive%" %wsasDrive%:

rem find SOFTCLICKER_HOME if it does not exist due to either an invalid value passed
rem by the user or the %0 problem on Windows 9x
if not exist "%SOFTCLICKER_HOME%\bin\version.txt" goto noServerHome

set AXIS2_HOME=%SOFTCLICKER_HOME%
goto updateClasspath

:noServerHome
echo SOFTCLICKER_HOME is set incorrectly or SOFTCLICKER could not be located. Please set SOFTCLICKER_HOME.
goto end

rem ----- update classpath -----------------------------------------------------
:updateClasspath

setlocal EnableDelayedExpansion
cd %SOFTCLICKER_HOME%
set SOFTCLICKER_CLASSPATH=
FOR %%C in ("%SOFTCLICKER_HOME%\bin\*.jar") DO set SOFTCLICKER_CLASSPATH=!SOFTCLICKER_CLASSPATH!;".\bin\%%~nC%%~xC"

set SOFTCLICKER_CLASSPATH="%JAVA_HOME%\lib\tools.jar";%SOFTCLICKER_CLASSPATH%;

FOR %%D in ("%SOFTCLICKER_HOME%\lib\commons-lang*.jar") DO set SOFTCLICKER_CLASSPATH=!SOFTCLICKER_CLASSPATH!;".\lib\%%~nD%%~xD"

rem ----- Process the input command -------------------------------------------

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).


:setupArgs
if ""%1""=="""" goto doneStart

if ""%1""==""-run""     goto commandLifecycle
if ""%1""==""--run""    goto commandLifecycle
if ""%1""==""run""      goto commandLifecycle

if ""%1""==""-restart""  goto commandLifecycle
if ""%1""==""--restart"" goto commandLifecycle
if ""%1""==""restart""   goto commandLifecycle

if ""%1""==""debug""    goto commandDebug
if ""%1""==""-debug""   goto commandDebug
if ""%1""==""--debug""  goto commandDebug

if ""%1""==""version""   goto commandVersion
if ""%1""==""-version""  goto commandVersion
if ""%1""==""--version"" goto commandVersion

shift
goto setupArgs

rem ----- commandVersion -------------------------------------------------------
:commandVersion
shift
type "%SOFTCLICKER_HOME%\bin\version.txt"
goto end

rem ----- commandDebug ---------------------------------------------------------
:commandDebug
shift
set DEBUG_PORT=%1
if "%DEBUG_PORT%"=="" goto noDebugPort
if not "%JAVA_OPTS%"=="" echo Warning !!!. User specified JAVA_OPTS will be ignored, once you give the --debug option.
set JAVA_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%DEBUG_PORT%
echo Please start the remote debugging client to continue...
goto findJdk

:noDebugPort
echo Please specify the debug port after the --debug option
goto end

rem ----- commandLifecycle -----------------------------------------------------
:commandLifecycle
goto findJdk

:doneStart
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem ---------- Handle the SSL Issue with proper JDK version --------------------
rem find the version of the jdk
:findJdk

set CMD=RUN %*

:checkJdk17
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /r "1.[7|8]" >NUL
IF ERRORLEVEL 1 goto unknownJdk
goto jdk17

:unknownJdk
echo Starting Soft Clicker (in unsupported JDK)
echo [ERROR] SoftClicker is supported only on JDK 1.7 and 1.8
goto jdk17

:jdk17
goto runServer

rem ----------------- Execute The Requested Command ----------------------------

:runServer
cd %SOFTCLICKER_HOME%

rem ------------------ Remove tmp folder on startup -----------------------------
set TMP_DIR=%SOFTCLICKER_HOME%\tmp
rmdir "%TMP_DIR%" /s /q

rem ---------- Add jars to classpath ----------------

set SOFTCLICKER_CLASSPATH=.\lib;%SOFTCLICKER_CLASSPATH%

set JAVA_ENDORSED=".\lib\endorsed";"%JAVA_HOME%\jre\lib\endorsed";"%JAVA_HOME%\lib\endorsed"

set CMD_LINE_ARGS=-Xbootclasspath/a:%SOFTCLICKER_XBOOTCLASSPATH% -Xms256m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%SOFTCLICKER_HOME%\repository\logs\heap-dump.hprof" -classpath %SOFTCLICKER_CLASSPATH% %JAVA_OPTS% -Djava.endorsed.dirs=%JAVA_ENDORSED% -Dsoftclicker.home="%SOFTCLICKER_HOME%" -Dsoftclicker.server.standalone=true -Djava.command="%JAVA_HOME%\bin\java" -Djava.opts="%JAVA_OPTS%" -Djava.io.tmpdir="%SOFTCLICKER_HOME%\tmp" -Dfile.encoding=UTF8

:runJava
echo JAVA_HOME environment variable is set to %JAVA_HOME%
echo SOFTCLICKER_HOME environment variable is set to %SOFTCLICKER_HOME%
"%JAVA_HOME%\bin\java" %CMD_LINE_ARGS% org.softclicker.server.starup.Bootstrap %CMD%
if "%ERRORLEVEL%"=="121" goto runJava
:end
goto endlocal

:endlocal

:END