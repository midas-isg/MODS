///
/// Contents: Data structure to hold one year of data.
/// Author:   John Aronis
/// Date:     April 2016
///
package edu.pitt.isg.mods;
import java.io.* ;
import java.util.StringTokenizer ;
import java.util.ArrayList ;

public class Data {

  private ArrayList<ArrayList<Patient>> data ;
  private ArrayList<String> dates ;

  public Data(String fileName) {
    this.data = new ArrayList<ArrayList<Patient>>() ;
    this.dates = new ArrayList<String>() ;
    readDataFromFile(fileName) ;
  }

  private void readDataFromFile(String fileName) {
    ArrayList<Patient> today = null ;
    try {
      FileReader fileReader = new FileReader(fileName) ;
      BufferedReader inputFile = new BufferedReader(fileReader) ;
      String line ;
      StringTokenizer tokenizer ;
      String id, dateAdmitted, test ;
      double influenzaLL, niiliLL, otherLL, influenzaP, niiliP, otherP ;
      String previousDate = "foo" ;
      inputFile.readLine() ;
      while ( (line=inputFile.readLine()) != null ) {
        tokenizer = new StringTokenizer(line) ;
        id = tokenizer.nextToken() ;
        dateAdmitted = tokenizer.nextToken() ;
        influenzaLL = Double.parseDouble(tokenizer.nextToken()) ;
        niiliLL = Double.parseDouble(tokenizer.nextToken()) ;
        otherLL = Double.parseDouble(tokenizer.nextToken()) ;
        influenzaP = Double.parseDouble(tokenizer.nextToken()) ;
        niiliP = Double.parseDouble(tokenizer.nextToken()) ;
        otherP = Double.parseDouble(tokenizer.nextToken()) ;
        test = tokenizer.nextToken() ;
        if (!dateAdmitted.equals(previousDate)) {
          today = new ArrayList<Patient>() ; data.add(today) ; previousDate=dateAdmitted ; dates.add(dateAdmitted) ;
        }
        today.add(new Patient(id,dateAdmitted,influenzaLL,niiliLL,otherLL,influenzaP,niiliP,otherP,test)) ;
      }
      inputFile.close() ;
    } catch (IOException e) {} ;
  }

  public int numberOfDays() { return data.size() ; }

  public String date(int day) { return dates.get(day) ; }

  public int numberOfPatients(int day) { return data.get(day).size() ; }

  public Patient patient(int day, int patient) { return data.get(day).get(patient) ; }

  public double expectedInfluenza(int day) {
    double result = 0.0 ;
    for (Patient p : data.get(day)) { result += p.influenzaP() ; }
    return result ;
  }

  public double expectedNiili(int day) {
    double result = 0.0 ;
    for (Patient p : data.get(day)) { result += p.niiliP() ; }
    return result ;
  }

  public double expectedOther(int day) {
    double result = 0.0 ;
    for (Patient p : data.get(day)) { result += p.otherP() ; }
    return result ;
  }

  public int numberOfPositiveTests(int day) {
    int result = 0 ;
    for (int p=0 ; p<numberOfPatients(day) ; p++) { if (patient(day,p).positiveTest()) result++ ; }
    return result ;
  }

  public int numberOfNegativeTests(int day) {
    int result = 0 ;
    for (int p=0 ; p<numberOfPatients(day) ; p++) { if (patient(day,p).negativeTest()) result++ ; }
    return result ;
  }

  public int numberOfMissingTests(int day) {
    int result = 0 ;
    for (int p=0 ; p<numberOfPatients(day) ; p++) { if (patient(day,p).missingTest()) result++ ; }
    return result ;
  }

  public int numberOfTests(int day) {
    return numberOfPositiveTests(day)+numberOfNegativeTests(day)+numberOfMissingTests(day) ;
  }

  public double dataLL(int day, double influenzaP, double niiliP) {
    double influenzaLP, niiliLP, otherLP, result ;
    if (influenzaP+niiliP > 1.0) { return -Double.MAX_VALUE ; }
    influenzaLP = Misc.log(influenzaP) ;
    niiliLP = Misc.log(niiliP) ;
    otherLP = Misc.log(1.0-(influenzaP+niiliP)) ;
    result = 0.0 ;
    for (Patient patient : data.get(day)) {
      result += Misc.logOfSum(Misc.logOfProduct(patient.influenzaLL(),influenzaLP), Misc.logOfProduct(patient.niiliLL(),niiliLP), Misc.logOfProduct(patient.otherLL(),otherLP)) ;
    }
    return result ;
  }

}

/// End-of-File
