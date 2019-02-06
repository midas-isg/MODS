///
/// Contents: Cache of log-likelihoods.
/// Author:   John Aronis
/// Date:     April 2016
///
package edu.pitt.isg.mods;
import java.io.* ;
import java.util.StringTokenizer ;
import java.util.ArrayList ;

public class Cache implements Serializable {

  private double MIN_INFLUENZA_PROBABILITY = 0.000001 ;

  private int resolution ;
  private int numberOfDays ;
  private String date[] ;
  private int[] numberOfPatients ;
  private double[] expectedInfluenza ;
  private double[] expectedNiili ;
  private double[] expectedOther ;
  private int[] numberOfPositiveTests ;
  private int[] numberOfNegativeTests ;
  private int[] numberOfMissingTests ;
  private ArrayList<double[][]> dataLogLikelihoods ;

  public Cache(Data data, int resolution) {
    this.resolution = resolution ;
    this.numberOfDays = data.numberOfDays() ;
    computeDates(data) ;
    computeNumbersOfPatients(data) ;
    computeExpectedValues(data) ;
    computeNumbersOfTests(data) ;
    computeDataLogLikelihoods(data,resolution) ;
  }

  private void computeDates(Data data) {
    date = new String[numberOfDays] ;
    for (int d=0 ; d<numberOfDays ; d++) { date[d] = data.date(d) ; }
  }

  private void computeNumbersOfPatients(Data data) {
    numberOfPatients = new int[numberOfDays] ;
    for (int d=0 ; d<numberOfDays ; d++) { numberOfPatients[d] = data.numberOfPatients(d) ; }
  }

  private void computeExpectedValues(Data data) {
    expectedInfluenza = new double[numberOfDays] ;
    expectedNiili = new double[numberOfDays] ;
    expectedOther = new double[numberOfDays] ;
    for (int d=0 ; d<numberOfDays ; d++) {
      expectedInfluenza[d] = data.expectedInfluenza(d) ;
      expectedNiili[d] = data.expectedNiili(d) ;
      expectedOther[d] = data.expectedOther(d) ;
    }
  }

  private void computeNumbersOfTests(Data data) {
    numberOfPositiveTests = new int[numberOfDays] ;
    numberOfNegativeTests = new int[numberOfDays] ;
    numberOfMissingTests = new int[numberOfDays] ;
    for (int d=0 ; d<numberOfDays ; d++) {
      numberOfPositiveTests[d] = data.numberOfPositiveTests(d) ;
      numberOfNegativeTests[d] = data.numberOfNegativeTests(d) ;
      numberOfMissingTests[d] = data.numberOfMissingTests(d) ;
    }
  }

  private double intToProbability(int n) { return (double)n/(double)resolution ; }
  private int probabilityToInt(double p) { return (int)(p*(double)resolution) ; }

  private void computeDataLogLikelihoods(Data data, int resolution) {
    dataLogLikelihoods = new ArrayList<double[][]>() ;
    double[][] todaysLogLikelihoods ;
    System.out.print("Computing LL for each day: ") ;
    for (int day=0 ; day<data.numberOfDays() ; day++) {
      System.out.print(day + " ") ;
      todaysLogLikelihoods = new double[resolution+1][resolution+1] ;
      for (int i=0 ; i<=resolution ; i++) { for (int n=0 ; n<=resolution ; n++) {
        todaysLogLikelihoods[i][n] = data.dataLL(day,Math.max(intToProbability(i),MIN_INFLUENZA_PROBABILITY),intToProbability(n)) ;
      }}
      dataLogLikelihoods.add(todaysLogLikelihoods) ;
    }
    System.out.println() ;
  }

  public int resolution() { return resolution ; }

  public int numberOfDays() { return numberOfDays ; }

  public String date(int day) { return date[day] ; }

  public int numberOfPatients(int day) { return numberOfPatients[day] ; }

  public double expectedInfluenza(int day) { return expectedInfluenza[day] ; }

  public double expectedNiili(int day) { return expectedNiili[day] ; }

  public double expectedOther(int day) { return expectedOther[day] ; }

  public int numberOfPositiveTests(int day) { return numberOfPositiveTests[day] ; }

  public int numberOfNegativeTests(int day) { return numberOfNegativeTests[day] ; }

  public int numberOfMissingTests(int day) { return numberOfMissingTests[day] ; }

  public int numberOfTests(int day) {
    return numberOfPositiveTests(day)+numberOfNegativeTests(day)+numberOfMissingTests(day) ;
  }

  public double dataLL(int day, double influenzaP, double niiliP) {
   return dataLogLikelihoods.get(day)[probabilityToInt(influenzaP)][probabilityToInt(niiliP)] ;
  }

  public void writeToFile(String fileName) {
    try {
      FileOutputStream outStream = new FileOutputStream(fileName) ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream) ;
      objectOutputStream.writeObject(this) ;
      objectOutputStream.close() ;
    } catch (Exception e) { System.out.println("Could not write cache.") ; }
  }

  public static Cache readFromFile(String fileName) {
    Cache cache = null ;
    try {
      FileInputStream inStream = new FileInputStream(fileName) ;
      ObjectInputStream objectInputStream = new ObjectInputStream(inStream) ;
      cache = (Cache)objectInputStream.readObject() ;
      objectInputStream.close() ;
    } catch (Exception e) { System.out.println("Could not read cache.") ; }
    return cache ;
  }

}

/// End-of-File
