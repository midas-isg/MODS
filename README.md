## mods Java Project
Contents: Count Multiple Outbreak Detection System
Author:   John Aronis, University of Pittsburgh

This is the source code for the Count Multiple Outbreak Detection System (CMODS).

This project uses the Maven project management tool.  Please install Maven
before continuing to read this document.  Maven can be downloaded from
https://maven.apache.org/download.cgi.

To run the program, first build the executable jar file by executing the command:
    mvn package

Once the package is built, run with:
    java -jar target/cmods-1.0-SNAPSHOT-jar-with-dependencies.jar <file> <column> <startYear> <startWeek> 
<numberOfWeeks> <predictionYear> <predictionWeek> <population> <theta> <models>

For instance:
    java -jar target/cmods-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/ILINet.csv ILITOTAL 2009 25 52 2009 32 300000000 0.10 100000

runs the system on the data file ILINet.csv, using column ILITOTAL,
with 52 weeks of data starting at MMWR 25 in 2009, and a prediction on
MMWR 32 of 2009, assuming population 300000000, 10% of ILI patients go
to a physician, and search over 1000000 models.

The subdirectory src/test/resources contains a single csv file from the CDC
(https://gis.cdc.gov/grasp/fluview/fluportaldashboard.html) with
ILINet data.
