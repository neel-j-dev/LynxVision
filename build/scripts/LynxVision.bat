@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  LynxVision startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and LYNX_VISION_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\LynxVision-2020.2.2.jar;%APP_HOME%\lib\cameraserver.jar;%APP_HOME%\lib\wpiHal.jar;%APP_HOME%\lib\wpilibj.jar;%APP_HOME%\lib\wpiutil.jar;%APP_HOME%\lib\javafx-gradle-plugin-8.8.2.jar;%APP_HOME%\lib\ini4j-0.5.1.jar;%APP_HOME%\lib\cscore-java-2020.2.2.jar;%APP_HOME%\lib\opencv-java-3.4.7-2.jar;%APP_HOME%\lib\ntcore-java-2020.2.2.jar;%APP_HOME%\lib\wpiutil-java-2020.2.2.jar;%APP_HOME%\lib\api-2020.2.2.jar;%APP_HOME%\lib\javafx-fxml-12-win.jar;%APP_HOME%\lib\javafx-fxml-12-linux.jar;%APP_HOME%\lib\javafx-controls-12-win.jar;%APP_HOME%\lib\javafx-controls-12.jar;%APP_HOME%\lib\javafx-controls-12-linux.jar;%APP_HOME%\lib\javafx-graphics-12-win.jar;%APP_HOME%\lib\javafx-graphics-12.jar;%APP_HOME%\lib\javafx-graphics-12-linux.jar;%APP_HOME%\lib\javafx-base-12-win.jar;%APP_HOME%\lib\javafx-base-12.jar;%APP_HOME%\lib\javafx-base-12-linux.jar;%APP_HOME%\lib\hal-java-2020.2.2.jar;%APP_HOME%\lib\wpilibj-java-2020.2.2.jar;%APP_HOME%\lib\cscore-jni-2020.2.2-windowsx86-64.jar;%APP_HOME%\lib\opencv-jni-3.4.7-2-windowsx86-64.jar;%APP_HOME%\lib\ntcore-jni-2020.2.2-windowsx86.jar;%APP_HOME%\lib\ntcore-jni-2020.2.2-windowsx86-64.jar;%APP_HOME%\lib\ntcore-jni-2020.2.2-linuxx86-64.jar;%APP_HOME%\lib\ntcore-jni-2020.2.2-osxx86-64.jar;%APP_HOME%\lib\asm-all-5.1.jar;%APP_HOME%\lib\guava-21.0.jar;%APP_HOME%\lib\gson-2.8.2.jar;%APP_HOME%\lib\easybind-1.0.3.jar;%APP_HOME%\lib\controlsfx-9.0.0.jar;%APP_HOME%\lib\javafxsvg-1.2.1.jar;%APP_HOME%\lib\Medusa-7.9.jar;%APP_HOME%\lib\jfoenix-9.0.8.jar;%APP_HOME%\lib\java-semver-0.9.0.jar;%APP_HOME%\lib\batik-transcoder-1.8.jar;%APP_HOME%\lib\xmlgraphics-commons-2.1.jar;%APP_HOME%\lib\batik-bridge-1.8.jar;%APP_HOME%\lib\batik-script-1.8.jar;%APP_HOME%\lib\batik-anim-1.8.jar;%APP_HOME%\lib\batik-gvt-1.8.jar;%APP_HOME%\lib\batik-svggen-1.8.jar;%APP_HOME%\lib\batik-svg-dom-1.8.jar;%APP_HOME%\lib\batik-parser-1.8.jar;%APP_HOME%\lib\batik-awt-util-1.8.jar;%APP_HOME%\lib\batik-dom-1.8.jar;%APP_HOME%\lib\batik-xml-1.8.jar;%APP_HOME%\lib\batik-css-1.8.jar;%APP_HOME%\lib\batik-util-1.8.jar;%APP_HOME%\lib\batik-ext-1.8.jar;%APP_HOME%\lib\xalan-2.7.0.jar;%APP_HOME%\lib\xml-apis-ext-1.3.04.jar;%APP_HOME%\lib\commons-io-1.3.1.jar;%APP_HOME%\lib\commons-logging-1.0.4.jar

@rem Execute LynxVision
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %LYNX_VISION_OPTS%  -classpath "%CLASSPATH%" LynxVision %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable LYNX_VISION_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%LYNX_VISION_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
