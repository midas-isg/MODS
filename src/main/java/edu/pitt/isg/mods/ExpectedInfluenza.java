package edu.pitt.isg.mods;

public class ExpectedInfluenza {

  public static void main(String[] args) {

    Data data = new Data("/home/aronis/MODS/AC_SLC_Data/AC_2009.data") ;

    for (int d=0 ; d<data.numberOfDays() ; d++) {
      System.out.println( (int)data.expectedInfluenza(d) ) ;
    }

  }

}

