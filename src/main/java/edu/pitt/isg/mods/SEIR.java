///
/// Contents: SEIR curve
/// Author:   John Aronis
/// Date:     May 2016
///
package edu.pitt.isg.mods;
import java.util.ArrayList ;

public class SEIR {

  private double MINIMUM_INFECTIOUS = 1.0 ;

  private int S, E, I, R, startDay ;
  private double R0, latentPeriod, infectiousPeriod, totalInfected ;
  private ArrayList<Double> infectious ;

  public SEIR(int S, int E, int I, int R, double R0, double latentPeriod, double infectiousPeriod, int startDay) {
    this.S = S ;
    this.E = E ;
    this.I = I ;
    this.R = R ;
    this.R0 = R0 ;
    this.latentPeriod = latentPeriod ;
    this.infectiousPeriod = infectiousPeriod ;
    this.startDay = startDay ;
    computeSEIR() ;
  }

  private void computeSEIR() {
    this.infectious = new ArrayList<Double>() ;
    double Population, S_current, E_current, I_current, R_current, S_next, E_next, I_next, R_next, f, r, D, lambda, beta ;   
    Population = S + E + I + R ;
    S_current = S ;
    E_current = E ;
    I_current = I ;
    R_current = R ;
    f = 1.0 / latentPeriod ;
    r = 1.0 / infectiousPeriod ;
    D = infectiousPeriod ;
    beta = R0/(Population*D) ;
    totalInfected = I ;
    do {
      infectious.add(I_current) ;
      lambda = beta*I_current ;
      S_next = (S_current - lambda*S_current) ;
      E_next = (E_current + lambda*S_current - f*E_current) ;
      I_next = (I_current + f*E_current - r*I_current) ;
      R_next = (R_current + r*I_current) ;
      S_current = S_next ;
      E_current = E_next ;
      I_current = I_next ;
      R_current = R_next ;
      totalInfected += f*E_current ;
    } while (I_current>MINIMUM_INFECTIOUS) ;
  }

  public int population() { return (S+E+I+R) ; }

  public int S() { return S ; }

  public int E() { return E ; }

  public int I() { return I ; }

  public int R() { return R ; }

  public double R0() { return R0 ; }

  public double latentPeriod() { return latentPeriod ; }

  public double infectiousPeriod() { return infectiousPeriod ; }

  public int startDay() { return startDay ; }

  public int duration() { return infectious.size() ; }

  public double infectious(int day) {
    if ((startDay<=day) && (day<(startDay+duration()))) return infectious.get(day-startDay) ; else return 0 ;
  }

  public double totalInfected() { return totalInfected ; }

  public int peakDay() {
    int peakDay=0 ;
    double peakInfectious = 0.0 ;
    for (int day=0 ; day<infectious.size() ; day++) {
      if ( infectious.get(day)>infectious.get(peakDay) ) { peakDay = day ; peakInfectious = infectious.get(peakDay) ; }
    }
    return (startDay+peakDay) ;
  }

  public double peakInfectious() { return infectious(peakDay()) ; }

  public String toString() {
    return startDay() + "/" + peakDay() + "/" + (int)peakInfectious() + "/" + duration()
           + " S=" + S + " E=" + E + " I=" + I + " R=" + R + " R0=" + R0 + " Lat=" + latentPeriod + " Inf=" + infectiousPeriod ;
  }

}

/// End-of-File
