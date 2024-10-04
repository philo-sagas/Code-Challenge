@echo off & setlocal enabledelayedexpansion

set SERVICE_NAME=PingService
set JAR_NAME="ping-0.0.1-SNAPSHOT.jar"
set MAIN_NAME="com.philo.challenge.ping.PingApplication"
set VM_ARGS_PERM_SIZE="MaxPermSize"
set VM_ARGS_METASPACE_SIZE="MaxMetaspaceSize"
set JAVA_8_VERSION="180"

if "%1" NEQ "" set SERVICE_NAME=%SERVICE_NAME%-%1

title %SERVICE_NAME%

@REM set jvm args by different java version
for /f tokens^=2-4^ delims^=.-_+^" %%j in ('java -fullversion 2^>^&1') do set "JAVA_VERSION=%%j%%k%%l"
set VM_ARGS=%VM_ARGS_PERM_SIZE%
if "%JAVA_VERSION%" GEQ %JAVA_8_VERSION% set VM_ARGS=%VM_ARGS_METASPACE_SIZE%

java -Xms64m -Xmx1024m -XX:%VM_ARGS%=64M -DServerName=%SERVICE_NAME% -jar %JAR_NAME% %MAIN_NAME%
pause
