package edu.pitt.isg.mods;

public class ExpectedILI {

  public static void main(String[] args) {

      Data data = new Data("/home/aronis/MODS/AC_SLC_Data/AC_2009.data") ;
//          Data data = new Data("/home/aronis/MODS/AC_SLC_Data/SLC_2010.data") ;

    for (int d=0 ; d<data.numberOfDays() ; d++) {
//      System.out.println( d + " " + data.expectedInfluenza(d) + data.expectedNiili(d) ) ;
      System.out.println( d + " " + data.numberOfPatients(d) ) ;
    }

  }

}

