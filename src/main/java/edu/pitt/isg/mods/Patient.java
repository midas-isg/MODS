///
/// Contents: Patient class.
/// Author:   John Aronis
/// Date:     April 2016
///
package edu.pitt.isg.mods;
public class Patient {

  private String id, dateAdmitted, test ;
  private double influenzaLL, niiliLL, otherLL, influenzaP, niiliP, otherP ;

  public Patient(String id, String dateAdmitted, double influenzaLL, double niiliLL, double otherLL, double influenzaP, double niiliP, double otherP, String test) {
    this.id = id ;
    this.dateAdmitted = dateAdmitted ;
    this.influenzaLL = influenzaLL ;
    this.niiliLL = niiliLL ;
    this.otherLL = otherLL ;
    this.influenzaP = influenzaP ;
    this.niiliP = niiliP ;
    this.otherP = otherP ;
    this.test = test ;
  }

  public String id() { return id ; }

  public String dateAdmitted() { return dateAdmitted ; }

  public double influenzaLL() { return influenzaLL ; }

  public double niiliLL() { return niiliLL ; }

  public double otherLL() { return otherLL ; }

  public double influenzaP() { return influenzaP ; }

  public double niiliP() { return niiliP ; }

  public double otherP() { return otherP ; }

  public String test() { return test ; }

  public boolean positiveTest() { return test.equals("POSITIVE") ; }

  public boolean negativeTest() { return test.equals("NEGATIVE") ; }

  public boolean missingTest() { return test.equals("MISSING") ; }

  public String toString() { return id ; }

}

/// End-of-File
