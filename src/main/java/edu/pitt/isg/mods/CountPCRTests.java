package edu.pitt.isg.mods;

public class CountPCRTests {

  public static void main(String[] args) {

    int total = 0 ;
    Data data = new Data("/home/aronis/MODS/AC_SLC_Data/AC_2009.data") ;
//    Data data = new Data("/home/aronis/MODS/AC_SLC_Data/SLC_2010.data") ;

    for (int d=0 ; d<365 ; d++) {
total += data.numberOfPositiveTests(d) ;
System.out.println( total ) ;
    }

  }

}

