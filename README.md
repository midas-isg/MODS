## Multiple Outbreak Detection System (MODS).
*Author*:   John Aronis, University of Pittsburgh

This project uses the Maven project management tool.  Please install Maven
before continuing to read this document.  Maven can be downloaded from
https://maven.apache.org/download.cgi.

To run MODS, first build the executable jar file by executing the command:

    `mvn package`

Once the package is built, run with:
  java -jar target/mods-1.0-SNAPSHOT-jar-with-dependencies.jar <data> <models> <start>

where <data> is the (full or relative path name to) the input file,
<models> is the number of models to test, and <start> is the day to
start making predictions (where the first day of the data file is day 0).
