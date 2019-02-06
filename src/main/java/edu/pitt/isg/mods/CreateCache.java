///
/// Contents: Create cache and write to file.
/// Author:   John Aronis
/// Date:     November 2015
///
package edu.pitt.isg.mods;
import java.io.* ;

public class CreateCache {

  public static void main(String[] args) {

    String dataFile, cacheFile ;
    Data data ;
    Cache cache1, cache2 ;
    int resolution ;

    dataFile  = args[0] + ".data" ;
    cacheFile = args[0] + "_" + args[1] + ".cache" ;
    resolution = Integer.parseInt(args[1]) ;

    System.out.println("Reading data from file...") ;
    data = new Data(dataFile) ;

    System.out.println("Creating cache...") ;
    cache1 = new Cache(data,resolution) ;
    cache1.writeToFile(cacheFile) ;

  }

}

/// End-of-File

