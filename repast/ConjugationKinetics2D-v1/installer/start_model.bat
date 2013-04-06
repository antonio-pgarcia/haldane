@echo off
TITLE ConjugationKinetics2D-v1

REM Repast Simphony Model Starter
REM By Michael J. North
REM Adapated by Antonio Prestes Garcia
REM 
REM Please note that the paths given below use
REM a unusual Linux-like notation. This is a
REM unfortunate requirement of the Java Plugin
REM framework application loader.

REM Note the Repast Simphony Directories.
set REPAST_SIMPHONY_ROOT=../repast.simphony/repast.simphony.runtime_2.0.1/
set REPAST_SIMPHONY_LIB=%REPAST_SIMPHONY_ROOT%lib/

REM Define the Core Repast Simphony Directories and JARs
SET CP=%CP%;%REPAST_SIMPHONY_ROOT%bin
SET CP=%CP%;%REPAST_SIMPHONY_LIB%saf.core.runtime.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%commons-logging-1.1.1.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%javassist-3.15.0.GA.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%jpf.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%jpf-boot.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%log4j-1.2.16.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%xpp3_min-1.1.4c.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%xstream-1.4.2.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%xmlpull-1.1.3.1.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%commons-cli-1.2.jar
SET CP=%CP%;../groovylib/groovy-all-1.8.4.jar

REM Change to the Default Repast Simphony Directory
CD ConjugationKinetics2D-v1

java -version 2>&1 | findstr "64-Bit" > NUL

if %ERRORLEVEL% == 0 (
	echo "64-Bit JVM"
	SET EXT=lib/ext64
) else (
	echo "32-Bit JVM"
	SET EXT=lib/ext32
)

REM Start the Model
START javaw -Djava.library.path=%EXT% -Djava.ext.dirs=%EXT% -Xss10M -Xmx1024M -cp %CP% repast.simphony.runtime.RepastMain ./ConjugationKinetics2D-v1.rs
