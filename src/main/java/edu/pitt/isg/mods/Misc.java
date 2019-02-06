///
/// Contents: Miscellaneous math functions.
/// Author:   John Aronis
/// Date:     April 2016
///
package edu.pitt.isg.mods;

import java.util.Random ;

public class Misc {

  public static double log(double x) { return Math.log10(x) ; }

  public static double exp(double x) { return Math.pow(10,x) ; }

  public static double logOfProduct(double ... logX) {
    double result = 0.0 ;
    for (int i=0 ; i<logX.length ; i++) { result += logX[i] ; }
    return result ;
  }

  public static double logOfSum(double ... logX) {
    double result = logX[0] ;
    for (int i=1 ; i<logX.length ; i++) { result = logOfSum2(result,logX[i]) ; }
    return result ;
  }

  private static double logOfSum2(double logX, double logY) {
    final double MAX_EXP = -50000.0;
    double logYMinusLogX, temp;
    if (logY > logX) {
      temp = logX;
      logX = logY;
      logY = temp;
    }
    logYMinusLogX = logY - logX;
    if (logYMinusLogX < MAX_EXP) {
      return logX;
    } else {
      return Math.log10(1 + Math.pow(10, logYMinusLogX)) + logX;
    }
  }

  public static int parameter(int low, int high) {
    Random R = new Random() ;
    int range = (high-low)+1 ;
    return low + R.nextInt(range) ;
  }

  public static double parameter(double low, double high) {
    Random R = new Random() ;
    double range = (high-low) ;
    return low + R.nextDouble()*range ;
  }

  public static double round(double x) {  return ((int)(x*100))/100.0 ; }

  public static int MIN_EXPECTED_FLU = 500 ;
  public static int WINDOW = 5 ;

  public static double ma(int n, double[] values) {
    double total = 0.0 ;
    for (int nn=(n-WINDOW) ; nn<n ; nn++) { total += values[nn] ; }
    return total/WINDOW ;
  }

  public static String peaks(double[] values) {
    String result = "" ;
    for (int day=WINDOW ; day<(values.length-WINDOW) ; day++) {
      if (ma(day,values)>MIN_EXPECTED_FLU && ma(day,values)>ma(day-1,values) && ma(day,values)>ma(day+1,values)) {
        result = result + "Max-" + day + "/" + (int)ma(day,values) + "   " ;
      }
      if (ma(day,values)>MIN_EXPECTED_FLU && ma(day,values)<ma(day-1,values) && ma(day,values)<ma(day+1,values)) {
        result = result + "Min-" + day + "/" + (int)ma(day,values) + "   " ;
      }
    }
    return result ;
  }

}

/// End-of-File
