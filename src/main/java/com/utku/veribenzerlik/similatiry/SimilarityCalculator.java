package com.utku.veribenzerlik.similatiry;

import com.utku.veribenzerlik.model.Record;

import java.util.*;

public class SimilarityCalculator {

    // Sayısal kolon isimleri
    private static final String[] NUMERIC_COLUMNS = {
        "Tamsayi1", "Tamsayi2", "GaussianTamsayi1",
        "GaussianReelSayi1", "GaussianReelSayi2",
        "GaussianDate1"
        // Timestamp veya binary alanlar bu aşamada dışarıda
    };

    // Map: kolon ismi → double[] veri dizisi
    public static Map<String, double[]> extractNumericColumns(List<Record> records) {
        Map<String, double[]> columnMap = new HashMap<>();

        int size = records.size();

        double[] t1 = new double[size];
        double[] t2 = new double[size];
        double[] g1 = new double[size];
        double[] r1 = new double[size];
        double[] r2 = new double[size];
        double[] d1 = new double[size];

        for (int i = 0; i < size; i++) {
            Record r = records.get(i);
            t1[i] = r.TamSayi1;
            t2[i] = r.TamSayi2;
            g1[i] = r.GaussianTamSayi1;
            r1[i] = r.GaussianReelSayi1;
            r2[i] = r.GaussianReelSayi2;
            d1[i] = r.GaussianDate1;
        }

        columnMap.put("Tamsayi1", t1);
        columnMap.put("Tamsayi2", t2);
        columnMap.put("GaussianTamsayi1", g1);
        columnMap.put("GaussianReelSayi1", r1);
        columnMap.put("GaussianReelSayi2", r2);
        columnMap.put("GaussianDate1", d1);

        return columnMap;
    }

    public static List<SimilarityResult> calculatePairwiseSimilarities(Map<String, double[]> columnData) {
        List<SimilarityResult> results = new ArrayList<>();

        List<String> keys = new ArrayList<>(columnData.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                String colA = keys.get(i);
                String colB = keys.get(j);
                double[] dataA = columnData.get(colA);
                double[] dataB = columnData.get(colB);

                double sim = pearsonCorrelation(dataA, dataB);
                results.add(new SimilarityResult(List.of(colA, colB), sim));
            }
        }

        return results;
    }

    /**
     * 3'lü kolon karşılaştırmaları yapır
     */
    public static List<SimilarityResult> calculateTripleSimilarities(Map<String, double[]> columnData) {
        List<SimilarityResult> results = new ArrayList<>();

        List<String> keys = new ArrayList<>(columnData.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                for (int k = j + 1; k < keys.size(); k++) {
                    String colA = keys.get(i);
                    String colB = keys.get(j);
                    String colC = keys.get(k);
                    
                    double[] dataA = columnData.get(colA);
                    double[] dataB = columnData.get(colB);
                    double[] dataC = columnData.get(colC);

                    // 3'lü benzerlik için ortalama korelasyon hesabı
                    double simAB = pearsonCorrelation(dataA, dataB);
                    double simAC = pearsonCorrelation(dataA, dataC);
                    double simBC = pearsonCorrelation(dataB, dataC);
                    
                    double avgSimilarity = (simAB + simAC + simBC) / 3.0;
                    
                    results.add(new SimilarityResult(List.of(colA, colB, colC), avgSimilarity));
                }
            }
        }

        return results;
    }

    public static double pearsonCorrelation(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0, sumXY = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
            sumXY += x[i] * y[i];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        if (denominator == 0) return 0;
        return numerator / denominator;
    }

    // Result sınıfı (istersen ayrı bir dosyaya da alabiliriz)
    public static class SimilarityResult {
        public List<String> columnsInvolved;
        public double score;

        public SimilarityResult(List<String> columnsInvolved, double score) {
            this.columnsInvolved = columnsInvolved;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.join(" & ", columnsInvolved) + " → " + String.format("%.4f", score);
        }
    }
}
