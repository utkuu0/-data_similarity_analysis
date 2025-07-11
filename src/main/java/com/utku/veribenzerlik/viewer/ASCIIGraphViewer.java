package com.utku.veribenzerlik.viewer;

import java.util.Map;

public class ASCIIGraphViewer {
    
    /**
     * Benzerlik matrisini ASCII formatında konsolda gösterir
     */
    public static void displaySimilarityMatrix(Map<String, double[]> columnData) {
        String[] columns = columnData.keySet().toArray(new String[0]);
        double[][] matrix = new double[columns.length][columns.length];
        
        // Benzerlik matrisini hesapla
        for (int i = 0; i < columns.length; i++) {
            for (int j = 0; j < columns.length; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0;
                } else {
                    matrix[i][j] = calculateCorrelation(
                        columnData.get(columns[i]), 
                        columnData.get(columns[j])
                    );
                }
            }
        }
        
        System.out.println("\n=== ASCİİ BENZERLIK GRAF (Korelasyonlar) ===");
        System.out.println("Açıklama: + = pozitif korelasyon, - = negatif korelasyon, = = eşit (nötr)");
        System.out.println("Çizgi kalınlığı = korelasyon gücü");
        
        for (int i = 0; i < columns.length; i++) {
            for (int j = i + 1; j < columns.length; j++) {
                double corr = matrix[i][j];
                String connection = getConnectionSymbol(corr);
                String strength = getStrengthIndicator(Math.abs(corr));
                
                System.out.printf("%-20s %s%-8s%s %-20s (r=%.4f)%n", 
                    columns[i], 
                    connection, 
                    strength, 
                    connection, 
                    columns[j], 
                    corr);
            }
        }
        
        System.out.println("\n=== GÖRSEL LEGEND ===");
        System.out.println("++++++++  : Çok güçlü pozitif korelasyon (r > 0.8)");
        System.out.println("++++++    : Güçlü pozitif korelasyon (r > 0.6)");
        System.out.println("+++       : Orta pozitif korelasyon (r > 0.3)");
        System.out.println("++        : Zayıf pozitif korelasyon (r > 0.1)");
        System.out.println("===       : Nötr / çok zayıf (|r| <= 0.1)");
        System.out.println("--        : Zayıf negatif korelasyon (r < -0.1)");
        System.out.println("---       : Orta negatif korelasyon (r < -0.3)");
        System.out.println("------    : Güçlü negatif korelasyon (r < -0.6)");
        System.out.println("--------  : Çok güçlü negatif korelasyon (r < -0.8)");
    }
    
    /**
     * Bağlantı simgesini döndürür (pozitif/negatif/nötr)
     */
    private static String getConnectionSymbol(double correlation) {
        if (correlation > 0.1) return "+";
        else if (correlation < -0.1) return "-";
        else return "=";
    }
    
    /**
     * Korelasyon gücünü gösteren göstergeyi döndürür
     */
    private static String getStrengthIndicator(double absCorr) {
        if (absCorr > 0.8) return "++++++++";
        else if (absCorr > 0.6) return "++++++";
        else if (absCorr > 0.3) return "+++";
        else if (absCorr > 0.1) return "++";
        else return "===";
    }
    
    /**
     * Pearson korelasyonu hesaplar
     */
    private static double calculateCorrelation(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }
        
        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        
        return denominator == 0 ? 0 : numerator / denominator;
    }
}
