///
/// Contents: Compute probabilities and expected values.
/// Author:   John Aronis
/// Date:     May 2016
///
package edu.pitt.isg.mods;
public class Predictions {

  public static boolean VERBOSE       = true ;

  private int POPULATION ;
  private int BASELINE ;
  private double MIN_THETA ;
  private double MAX_THETA ;
  private double C ;
  private double ZERO_PRIOR ;
  private double ONE_PRIOR ;
  private double TWO_PRIOR ;
  private int TODAY ;
  private int N ;
  private Cache CACHE ;
  private int YEAR ;
  private Influenza zeroModel ;

  public Influenza[] oneModels, twoModels ;
  public double oneDenominator, twoDenominator ;

  private static double FAIL = -Double.MAX_VALUE ;

  public Predictions(int population, int baseline, double min_theta, double max_theta, double c, double zeroPrior, double onePrior, double twoPrior, int today, int N, Cache cache) {
    this.POPULATION = population ;
    this.BASELINE = baseline ;
    this.MIN_THETA = min_theta ;
    this.MAX_THETA = max_theta ;
    this.C = c ;
    this.ZERO_PRIOR = zeroPrior ;
    this.ONE_PRIOR = onePrior ;
    this.TWO_PRIOR = twoPrior ;
    this.TODAY = today ;
    this.N = N ;
    this.CACHE = cache ;
    this.YEAR = cache.numberOfDays() ;
    createZeroModel() ;
    scoreZeroModel() ;
    oneModels = new Influenza[N] ;
    createOneModels() ;
    scoreOneModels() ;
    computeOneDemonimator() ;
    twoModels = new Influenza[N] ;
    createTwoModels() ;
    scoreTwoModels() ;
    computeTwoDemonimator() ;
  }

  private void createZeroModel() { zeroModel = Influenza.zero(YEAR, BASELINE, POPULATION) ; }

  private void scoreZeroModel() { zeroModel.computeScore( (MIN_THETA+MAX_THETA)/2.0 , new Niili(zeroModel, (MIN_THETA+MAX_THETA)/2.0 ,C,CACHE), TODAY, CACHE) ; }

  public Influenza zeroModel() { return zeroModel ; }

  private void createOneModels() {
    if ( VERBOSE ) System.out.print("CREATING ONE MODELS: ") ;
    for (int n=0 ; n<N ; n++) {
      if ( VERBOSE && n%(N/100)==0 ) System.out.print(".") ;
      oneModels[n] = Influenza.oneRandom(YEAR, BASELINE, POPULATION, TODAY) ;
    }
    if ( VERBOSE ) System.out.println() ;
  }

  public Influenza[] oneModels() { return oneModels ; }

  private void scoreOneModels() {
double THETA ;
    if ( VERBOSE ) System.out.print("SCORING ONE MODELS: ") ;
    for (int n=0 ; n<N ; n++) {
THETA = Misc.parameter(MIN_THETA,MAX_THETA) ;
      if ( VERBOSE && n%(N/100)==0 ) System.out.print(".") ;
      oneModels[n].computeScore(THETA, new Niili(oneModels[n],THETA,C,CACHE), TODAY, CACHE) ;
    }
    if ( VERBOSE ) System.out.println() ;
  }

  private void computeOneDemonimator() {
    oneDenominator = oneModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      oneDenominator = Misc.logOfSum(oneDenominator,oneModels[n].score()) ;
    }
  }

  public Influenza bestOneModel() {
    Influenza bestModel = oneModels[0] ;
    for (int n=1 ; n<N ; n++) {
      if ( oneModels[n].score()>bestModel.score() ) { bestModel = oneModels[n] ; }
    }
    return bestModel ;
  }

  private void createTwoModels() {
    if ( VERBOSE ) System.out.print("CREATING TWO MODELS: ") ;
    for (int n=0 ; n<N ; n++) {
      if ( VERBOSE && n%(N/100)==0 ) System.out.print(".") ;
      twoModels[n] = Influenza.twoRandom(YEAR, BASELINE, POPULATION, TODAY) ;
    }
    if ( VERBOSE ) System.out.println() ;
  }

  public Influenza[] twoModels() { return twoModels ; }

  private void scoreTwoModels() {
double THETA ;
    if ( VERBOSE ) System.out.print("SCORING TWO MODELS: ") ;
    for (int n=0 ; n<N ; n++) {
THETA = Misc.parameter(MIN_THETA,MAX_THETA) ;
      if ( VERBOSE && n%(N/100)==0 ) System.out.print(".") ;
      twoModels[n].computeScore(THETA, new Niili(twoModels[n],THETA,C,CACHE), TODAY, CACHE) ;
    }
    if ( VERBOSE ) System.out.println() ;
  }

  private void computeTwoDemonimator() {
    twoDenominator = twoModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      twoDenominator = Misc.logOfSum(twoDenominator,twoModels[n].score()) ;
    }
  }

  public Influenza bestTwoModel() {
    Influenza bestModel = twoModels[0] ;
    for (int n=1 ; n<N ; n++) {
      if ( twoModels[n].score()>bestModel.score() ) { bestModel = twoModels[n] ; }
    }
    return bestModel ;
  }

  private double logProbZero() { return Misc.log(ZERO_PRIOR + ((YEAR-TODAY)/(double)YEAR)*ONE_PRIOR + ((YEAR-TODAY)/(double)YEAR)*((YEAR-TODAY)/(double)YEAR)*TWO_PRIOR) ; }
  private double logProbOne() { return Misc.log((1.0-(Misc.exp(logProbZero())+Misc.exp(logProbTwo())))) ; }
  private double logProbTwo() { return Misc.log((TODAY/(double)YEAR)*TWO_PRIOR) ; }

  private double logProbDataGivenZero() { return zeroModel.score() ; }
  private double logProbDataGivenOne() { return -Misc.log((double)TODAY*N) + oneDenominator ; }
  private double logProbDataGivenTwo() { return -Misc.log((double)TODAY*TODAY*N) + twoDenominator ; }

  public double probZeroGivenData() {
    double numerator, denominator ;
    numerator = logProbDataGivenZero()+logProbZero() ;
    denominator = Misc.logOfSum(logProbDataGivenZero()+logProbZero(),logProbDataGivenOne()+logProbOne(),logProbDataGivenTwo()+logProbTwo()) ;
    return Misc.exp(numerator-denominator) ;
  }

  public double probOneGivenData() {
    double numerator, denominator ;
    numerator = logProbDataGivenOne()+logProbOne() ;
    denominator = Misc.logOfSum(logProbDataGivenZero()+logProbZero(),logProbDataGivenOne()+logProbOne(),logProbDataGivenTwo()+logProbTwo()) ;
    return Misc.exp(numerator-denominator) ;
  }

  public double probTwoGivenData() {
    double numerator, denominator ;
    numerator = logProbDataGivenTwo()+logProbTwo() ;
    denominator = Misc.logOfSum(logProbDataGivenZero()+logProbZero(),logProbDataGivenOne()+logProbOne(),logProbDataGivenTwo()+logProbTwo()) ;
    return Misc.exp(numerator-denominator) ;
  }

  private double expectedInfectiousGivenOne(int day) {
    double result ;
    result = Misc.log(oneModels[0].infectious(day))+oneModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      result = Misc.logOfSum(result,Misc.log(oneModels[n].infectious(day))+oneModels[n].score()) ;
    }
    return Misc.exp(result-oneDenominator) ;
  }

  private double expectedInfectiousGivenTwo(int day) {
    double result ;
    result = Misc.log(twoModels[0].infectious(day))+twoModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      result = Misc.logOfSum(result,Misc.log(twoModels[n].infectious(day))+twoModels[n].score()) ;
    }
    return Misc.exp(result-twoDenominator) ;
  }

  public double[] expectedInfectious() {
    double[] result = new double[YEAR] ;
    for (int day=0 ; day<YEAR ; day++) {
      result[day] += expectedInfectiousGivenOne(day)*probOneGivenData() ;
      result[day] += expectedInfectiousGivenTwo(day)*probTwoGivenData() ;
    }
    return result ;
  }

  private double expectedTotalInfectedGivenOne() {
    double result ;
    result = Misc.log(oneModels[0].totalInfected())+oneModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      result = Misc.logOfSum(result,Misc.log(oneModels[n].totalInfected())+oneModels[n].score()) ;
    }
    return Misc.exp(result-oneDenominator) ;
  }

  private double expectedTotalInfectedGivenTwo() {
    double result ;
    result = Misc.log(twoModels[0].totalInfected())+twoModels[0].score() ;
    for (int n=1 ; n<N ; n++) {
      result = Misc.logOfSum(result,Misc.log(twoModels[n].totalInfected())+twoModels[n].score()) ;
    }
    return Misc.exp(result-twoDenominator) ;
  }

  public double expectedTotalInfected() {
    double result = 0.0 ;
    result += expectedTotalInfectedGivenOne()*probOneGivenData() ;
    result += expectedTotalInfectedGivenTwo()*probTwoGivenData() ;
    return result ;
  }

}

/// End-of-File

