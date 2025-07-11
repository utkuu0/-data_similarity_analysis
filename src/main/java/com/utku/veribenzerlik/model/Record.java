package com.utku.veribenzerlik.model;

import java.time.LocalDateTime;

public class Record {
    public int ID;
    public int TamSayi1;
    public int TamSayi2;
    public int GaussianTamSayi1;
    public double GaussianReelSayi1;
    public double GaussianReelSayi2;
    public int GaussianDate1;
    public LocalDateTime GaussianDate2;
    public LocalDateTime GaussianDate3;
    public String Binary1;


    public Record(int ID, int t1, int t2, int gT1, double gR1, double gR2, int gD1, LocalDateTime gD2, LocalDateTime gD3, String b1) {
        this.ID = ID;
        this.TamSayi1 = t1;
        this.TamSayi2 = t2;
        this.GaussianTamSayi1 = gT1;
        this.GaussianReelSayi1 = gR1;
        this.GaussianReelSayi2 = gR2;
        this.GaussianDate1 = gD1;
        this.GaussianDate2 = gD2;
        this.GaussianDate3 = gD3;
        this.Binary1 = b1;
    }
}
