///
/// Contents: Model NI-ILI based on influenza model and PCR tests.
/// Author:   John Aronis
/// Date:     April 2016
///
package edu.pitt.isg.mods;

public class Niili {

  public static double    A                 = 3.0 ;
  public static double    B                 = 10.0 ;
  public static int       RESOLUTION        = 100 ;
  public static double    MAX_INFLUENZA     = 0.80 ;
  public static int       REPORT_INCREMENT  = 30 ;
  public static boolean   VERBOSE           = true ;
  public static boolean   DEBUG             = false ;

  private Influenza       INFLUENZA ;
  private double          THETA ;
  private double          C ;
  private Cache           CACHE ;
  private double[]        EXPECTED_NIILI ;

  public Niili(Influenza influenza, double theta, double c, Cache cache) {
    INFLUENZA = influenza ;
    THETA = theta ;
    C = c ;
    CACHE = cache ;
    EXPECTED_NIILI = new double[CACHE.numberOfDays()] ;
    computeNiiliExpectedValues() ;
  }

  private double logOfProbabilityOfTests(int positive, int negative, double influenzaFractionOfILI) {
    if (DEBUG) {
      if (positive<0 || negative<0) { System.out.println("WARNING: logOfProbabilityOfTests illegal number of tests") ; }
      if (influenzaFractionOfILI<=0.0 || influenzaFractionOfILI>=1.0) { System.out.println("WARNING: logOfProbabilityOfTests illegal influenzaFractionOfILI") ; }
    }
    double niiliFractionOfILI, probabilityPositive, probabilityNegative ;
    niiliFractionOfILI = 1.0-influenzaFractionOfILI ;
    probabilityPositive = (C*influenzaFractionOfILI) / ((C*influenzaFractionOfILI)+niiliFractionOfILI) ;
    probabilityNegative = 1.0-probabilityPositive ;
    return logOfCombinations(positive+negative,positive) + Misc.log(probabilityPositive)*positive + Misc.log(probabilityNegative)*negative ;
  }

  private double logOfScoreOfN(int n, int positive, int negative, double numberOfERPatients, double numberOfERInfluenzaPatients, double logOfPreviousScoreOfN) {
    if (DEBUG) {
      if (n<1 || n>RESOLUTION) { System.out.println("WARNING: logOfScoreOfN illegal n") ; }
      if (positive<0 || negative<0) { System.out.println("WARNING: logOfScoreOfN illegal number of tests") ; }
      if (numberOfERPatients<=0 || numberOfERInfluenzaPatients<=0) { System.out.println("WARNING: logOfScoreOfN illegal number of patients") ; }
      if (numberOfERInfluenzaPatients>=numberOfERPatients) { System.out.println("WARNING: logOfScoreOfN too many influenza patients") ; }
      if (logOfPreviousScoreOfN>0.0) { System.out.println("WARNING: logOfScoreOfN illegal logOfPreviousScoreOfN") ; }
    }
    double numberOfERNonInfluenzaPatients, numberOfERNiiliPatients, influenzaFractionOfILI ;
    numberOfERNonInfluenzaPatients = numberOfERPatients-numberOfERInfluenzaPatients ;
    numberOfERNiiliPatients = ((double)n/(double)RESOLUTION)*numberOfERNonInfluenzaPatients ;
    influenzaFractionOfILI = Math.min(numberOfERInfluenzaPatients/(numberOfERInfluenzaPatients+numberOfERNiiliPatients),MAX_INFLUENZA) ;
    return logOfProbabilityOfTests(positive, negative, influenzaFractionOfILI) + logOfPreviousScoreOfN ;
  }

  private double[] logOfInitialProbabilityOfN() {
    double[] result = new double[RESOLUTION+1] ;
    double x ;
    int change ;
    x = 1.0 / ( RESOLUTION/A + RESOLUTION/B - RESOLUTION/(A*B) ) ;
    change = (int)(RESOLUTION/A) ;
    for (int n=1 ; n<change ; n++) { result[n] = Misc.log(x) ; }
    for (int n=change ; n<=RESOLUTION ; n++) { result[n] = Misc.log(x/B) ; }
    return result ;
  }

  private void computeNiiliExpectedValues() {
    int positive, negative ;
    double numberOfERPatients, numberOfERInfluenzaPatients ;
    double[] logOfPreviousProbabilityOfN, scoresOfN ;
    logOfPreviousProbabilityOfN = logOfInitialProbabilityOfN() ;
    for (int day=0 ; day<CACHE.numberOfDays() ; day++) {
      positive = CACHE.numberOfPositiveTests(day) ;
      negative = CACHE.numberOfNegativeTests(day) ;
      numberOfERPatients = CACHE.numberOfPatients(day) ;
      numberOfERInfluenzaPatients = THETA*INFLUENZA.infectious(day) ;
      // First compute expected NI-ILI from prior:
      for (int n=1 ; n<=RESOLUTION ; n++) {
        EXPECTED_NIILI[day] += ((double)n/(double)RESOLUTION) * Misc.exp(logOfPreviousProbabilityOfN[n]) ;
      }
      EXPECTED_NIILI[day] *= numberOfERPatients / (numberOfERPatients-numberOfERInfluenzaPatients) ;
      // Then update:
      scoresOfN = new double[RESOLUTION+1] ;
      for (int n=1 ; n<=RESOLUTION ; n++) {
        scoresOfN[n] = logOfScoreOfN(n, positive, negative, numberOfERPatients, numberOfERInfluenzaPatients, logOfPreviousProbabilityOfN[n]) ;
      }
    } // day
  }

  public static double[] normalize(double[] logsOfProbabilities) {
    double total ;
    double[] result ;
    total = logsOfProbabilities[1] ;
    for (int i=2 ; i<logsOfProbabilities.length ; i++) { total = Misc.logOfSum(total,logsOfProbabilities[i]) ; }
    result = new double[logsOfProbabilities.length] ;
    for (int i=1 ; i<logsOfProbabilities.length ; i++) { result[i] = logsOfProbabilities[i]-total ; }
    return result ;
  }

  public double niiliP(int day) { return EXPECTED_NIILI[day] ; }

  private double round(double x) {  return ((int)(x*100))/100.0 ; }

  public String toString() {
    String result = "" ;
    for (int d=0 ; d<CACHE.numberOfDays() ; d+=REPORT_INCREMENT) { result = result + round(niiliP(d)) + " " ; }
    return result ;
  }

  // Some math stuff:

  private static double[] logOfFactorial ;

  private void initializeLogOfFactorial() {
    int max = 0 ;
    for (int d=0 ; d<CACHE.numberOfDays() ; d++) { if (CACHE.numberOfTests(d)>max) { max = CACHE.numberOfTests(d) ; } }
    logOfFactorial = new double[max+1] ;
    logOfFactorial[0] = Misc.log(1) ;
    for (int n=1 ; n<=max ; n++) { logOfFactorial[n] = logOfFactorial[n-1]+Misc.log(n) ; }
  }

  private double logOfCombinations(int n, int k) {
    if (logOfFactorial==null) initializeLogOfFactorial() ;
    return logOfFactorial[n]-(logOfFactorial[n-k]+logOfFactorial[k]) ;
  }

}

/// End-of-File
