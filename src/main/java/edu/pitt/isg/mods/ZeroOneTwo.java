///
/// Contents: Print probability of zero, one, or two outbreaks.
/// Author:   John Aronis
/// Date:     February 2017
///
package edu.pitt.isg.mods;
public class ZeroOneTwo {

  public static int     POPULATION ;
  public static int     BASELINE_INFLUENZA ;
  public static double  MIN_THETA ;
  public static double  MAX_THETA ;
  public static double  C ;
  public static double  ZERO_PRIOR ;
  public static double  ONE_PRIOR ;
  public static double  TWO_PRIOR ;
  public static String  CACHE_FILE ;
  public static Cache   CACHE ;
  public static Niili   NIILI ;
  public static int     NUMBER_OF_MODELS ;
  public static int[]   EVALUATION_PERIODS ;
  public static int     START_DAY ;
  public static boolean VERBOSE = true ;
  public static double  FAIL = -Double.MAX_VALUE ;

  public static void main(String[] args) {
    Influenza model, bestModel ;
    double score, bestScore, x ;
    Predictions P ;

    System.out.println("========== PARAMETERS ==============================================================================") ;
    setParameters(args) ;
    System.out.println("POPULATION:         " + POPULATION) ;
    System.out.println("BASELINE_INFLUENZA: " + BASELINE_INFLUENZA) ;
    System.out.println("MIN_THETA:          " + MIN_THETA) ;
    System.out.println("MAX_THETA:          " + MAX_THETA) ;
    System.out.println("C:                  " + C) ;
    System.out.println("ZERO_PRIOR:         " + ZERO_PRIOR) ;
    System.out.println("ONE_PRIOR:          " + ONE_PRIOR) ;
    System.out.println("TWO_PRIOR:          " + TWO_PRIOR) ;
    System.out.println("CACHE_FILE:         " + CACHE_FILE) ;
    System.out.println("NUMBER_OF_MODELS:   " + NUMBER_OF_MODELS) ;
    System.out.println("START_DAY:          " + START_DAY) ;

    for (int TODAY : EVALUATION_PERIODS) {
      if (TODAY < START_DAY) continue ;

      System.out.println("====================================================================================================") ;
      System.out.println("TODAY: " + TODAY + " " + CACHE.date(TODAY)) ;
      System.out.println("Creating and scoring models...") ;

      P = new Predictions(POPULATION, BASELINE_INFLUENZA, MIN_THETA, MAX_THETA, C, ZERO_PRIOR, ONE_PRIOR, TWO_PRIOR, TODAY, NUMBER_OF_MODELS, CACHE) ;

      System.out.println("---------- PROBABILITIES ------------------------") ;
      System.out.println("Probability of zero: " + P.probZeroGivenData() + " " + TODAY + " " + CACHE.date(TODAY)) ;
      System.out.println("Probability of one:  " + P.probOneGivenData()  + " " + TODAY + " " + CACHE.date(TODAY)) ;
      System.out.println("Probability of two:  " + P.probTwoGivenData()  + " " + TODAY + " " + CACHE.date(TODAY)) ;

    }

  } // main

  private static void setParameters(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: MODS <data> <models> <start>") ;
      return ;
    }
    if (args[0].equals("AC_2009")) {
      POPULATION         = 1200000 ;
      BASELINE_INFLUENZA = 1 ;
//      MIN_THETA          = 0.009 * 0.62 ;
//      MAX_THETA          = 0.011 * 0.62 ;
      MIN_THETA          = 0.005 * 0.62 ;
      MAX_THETA          = 0.020 * 0.62 ;
      C                  = 2.19217491369390103567 ;
      CACHE_FILE         = "../AC_SLC_Data/AC_2009_100.cache" ;
//      CACHE_FILE         = "../AC_SLC_Data/AC_2009_400.cache" ;
      CACHE              = Cache.readFromFile(CACHE_FILE) ;
//      int[] FOO          = { 61, 92, 122, 153, 183, 214, 245, 273, 303 } ;
      int[] FOO          = { 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92 } ;
      EVALUATION_PERIODS = FOO ;
    }
    if (args[0].equals("SLC_2010")) {
      POPULATION         = 1032942 ;
      BASELINE_INFLUENZA = 1 ;
//      MIN_THETA          = 0.009 * 0.55 ;
//      MAX_THETA          = 0.011 * 0.55 ;
      MIN_THETA          = 0.005 * 0.55 ;
      MAX_THETA          = 0.020 * 0.55 ;
      C                  = 2.19217491369390103567 ;
      CACHE_FILE         = "../AC_SLC_Data/SLC_2010_100.cache" ;
//      CACHE_FILE         = "../AC_SLC_Data/SLC_2010_400.cache" ;
      CACHE              = Cache.readFromFile(CACHE_FILE) ;
//      int[] FOO          = { 61, 92, 122, 153, 183, 214, 245, 273, 303 } ;
      int[] FOO          = { 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239 } ;
      EVALUATION_PERIODS = FOO ;
    }
    ZERO_PRIOR         = 0.1 ;
    ONE_PRIOR          = 0.8 ;
    TWO_PRIOR          = 0.1 ;
    NUMBER_OF_MODELS   = Integer.parseInt(args[1]) ;
    START_DAY          = Integer.parseInt(args[2]) ;
  }

}

/// End-of-File
