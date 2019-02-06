///
/// Contents: Zero, Single, or Double Influenza Outbreak
/// Author:   John Aronis
/// Date:     May 2016
///
package edu.pitt.isg.mods;

public class Influenza {

  public static double  MIN_FRACTION_S          = 0.5 ;
  public static double  MAX_FRACTION_S          = 1.0 ;
  public static int     MIN_E                   = 0 ;
  public static int     MAX_E                   = 0 ;
  public static int     MIN_I                   = 1 ;
  public static int     MAX_I                   = 100 ;
  public static double  MIN_R0                  = 1.1 ;
  public static double  MAX_R0                  = 3.0 ;
  public static double  MIN_LATENT              = 1 ;
  public static double  MAX_LATENT              = 4 ;
  public static double  MIN_INFECTIOUS          = 1 ;
  public static double  MAX_INFECTIOUS          = 8 ;
  public static int     MIN_START_DAY           = 60 ;
  public static int     MAX_START_DAY           = 240 ;
  public static int     MIN_DURATION            = 60 ;
  public static int     MAX_DURATION            = 180 ;
  public static int     MIN_PEAK                = 1000 ;
  public static int     MAX_PEAK                = 10000 ;
  public static int     WINDOW                  = 25 ;

  private int length, baseline, population ;
  private SEIR firstInfluenza, secondInfluenza ;
  private double score ;
  private static double FAIL = -Double.MAX_VALUE ;

  public Influenza(int length, int baseline, int population, SEIR firstInfluenza, SEIR secondInfluenza) {
    this.length = length ;
    this.baseline = baseline ;
    this.population = population ;
    this.firstInfluenza = firstInfluenza ;
    this.secondInfluenza = secondInfluenza ;
    this.score = 0.0 ;
  }

  public int length() { return length ; }

  public int baseline() { return baseline ; }

  public int population() { return population ; }

  public double infectiousFirstInfluenza(int day) { return firstInfluenza.infectious(day) ; }

  public double infectiousSecondInfluenza(int day) { return secondInfluenza.infectious(day) ; }

  public double infectious(int day) {
    return baseline+(firstInfluenza==null?0:firstInfluenza.infectious(day))+(secondInfluenza==null?0:secondInfluenza.infectious(day)) ;
  }

  public double totalInfected() {
    return (firstInfluenza==null?0:firstInfluenza.totalInfected())+(secondInfluenza==null?0:secondInfluenza.totalInfected()) ;
  }

  public void computeScore(double theta, Niili niili, int evaluationPeriod, Cache cache) {
    if ( firstInfluenza!=null && secondInfluenza!=null ) {
      if ( firstInfluenza.startDay()>(evaluationPeriod-WINDOW) ) { this.score = FAIL ; return ; }
      if ( secondInfluenza.startDay()>(evaluationPeriod-WINDOW) ) { this.score = FAIL ; return ; }
      if ( Math.abs(firstInfluenza.peakDay()-secondInfluenza.peakDay()) < WINDOW ) { this.score = FAIL ; return ; }
    }
    double result, influenzaP, niiliP ;
    result = 0.0 ;
    for (int day=1 ; day<evaluationPeriod ; day++) {
      niiliP = niili.niiliP(day) ;
      if ( theta*this.infectious(day) + niiliP*cache.numberOfPatients(day) > cache.numberOfPatients(day) ) { this.score = FAIL ; return ; }
      influenzaP = ( theta*this.infectious(day) ) / cache.numberOfPatients(day) ;
      result += cache.dataLL(day,influenzaP,niiliP) ;
    }
    this.score = result ;
  }

  public double score() { return this.score ; }

  public static Influenza zero(int length, int baseline, int population) {
    return new Influenza(length,baseline,population,null,null) ;
  }

  private static boolean legal(SEIR seir) {
    if (seir.duration()<MIN_DURATION) return false ;
    if (seir.duration()>MAX_DURATION) return false ;
    if (seir.peakInfectious()<MIN_PEAK) return false ;
    if (seir.peakInfectious()>MAX_PEAK) return false ;
    return true ;
  }

  public static Influenza oneRandom(int length, int baseline, int population, int today) {
    int startDay, S, E, I ;
    double R0, latentPeriod, infectiousPeriod ;
    SEIR firstInfluenza ;
    Influenza result ;
    do {
        S = (int)(Misc.parameter(MIN_FRACTION_S,MAX_FRACTION_S)*population) ;
        E = Misc.parameter(MIN_E,MAX_E) ;
        I = Misc.parameter(MIN_I,MAX_I) ;
        R0 = Misc.parameter(MIN_R0,MAX_R0) ;
        latentPeriod = Misc.parameter(MIN_LATENT,MAX_LATENT) ;
        infectiousPeriod = Misc.parameter(MIN_INFECTIOUS,MAX_INFECTIOUS) ;
        startDay = Misc.parameter(MIN_START_DAY,Math.min(MAX_START_DAY,today)) ;
        firstInfluenza = new SEIR(S, E, I, population-(S+E+I),R0, latentPeriod, infectiousPeriod, startDay) ;
    } while (!legal(firstInfluenza)) ;
    result = new Influenza(length, baseline, population, firstInfluenza, null) ;
    return result ;
  }

  public static Influenza twoRandom(int length, int baseline, int population, int today) {
    int startDay, S, E, I ;
    double R0, latentPeriod, infectiousPeriod ;
    SEIR firstInfluenza, secondInfluenza ;
    Influenza result ;
    do {
        S = (int)(Misc.parameter(MIN_FRACTION_S,MAX_FRACTION_S)*population) ;
        E = Misc.parameter(MIN_E,MAX_E) ;
        I = Misc.parameter(MIN_I,MAX_I) ;
        R0 = Misc.parameter(MIN_R0,MAX_R0) ;
        latentPeriod = Misc.parameter(MIN_LATENT,MAX_LATENT) ;
        infectiousPeriod = Misc.parameter(MIN_INFECTIOUS,MAX_INFECTIOUS) ;
        startDay = Misc.parameter(MIN_START_DAY,Math.min(MAX_START_DAY,today)) ;
        firstInfluenza = new SEIR(S, E, I, population-(S+E+I),R0, latentPeriod, infectiousPeriod, startDay) ;
    } while (!legal(firstInfluenza)) ;
    do {
        S = (int)(Misc.parameter(MIN_FRACTION_S,MAX_FRACTION_S)*population) ;
        E = Misc.parameter(MIN_E,MAX_E) ;
        I = Misc.parameter(MIN_I,MAX_I) ;
        R0 = Misc.parameter(MIN_R0,MAX_R0) ;
        latentPeriod = Misc.parameter(MIN_LATENT,MAX_LATENT) ;
        infectiousPeriod = Misc.parameter(MIN_INFECTIOUS,MAX_INFECTIOUS) ;
        startDay = Misc.parameter(MIN_START_DAY,Math.min(MAX_START_DAY,today)) ;
        secondInfluenza = new SEIR(S, E, I, population-(S+E+I),R0, latentPeriod, infectiousPeriod, startDay) ;
    } while (!legal(secondInfluenza)) ;
    result = new Influenza(length, baseline, population, firstInfluenza, secondInfluenza) ;
    return result ;
  }

  public void print() {
    System.out.println( "Baseline:         " + baseline ) ;
    System.out.println( "First Influenza:  " + firstInfluenza ) ;
    System.out.println( "Second Influenza: " + secondInfluenza ) ;
  }

}

/// End-of-File

