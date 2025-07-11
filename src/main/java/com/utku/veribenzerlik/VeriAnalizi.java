package com.utku.veribenzerlik;

import com.utku.veribenzerlik.database.DuckDBWriter;
import com.utku.veribenzerlik.generator.DataGenerator;
import com.utku.veribenzerlik.graph.GraphVizWriter;
import com.utku.veribenzerlik.model.Record;
import com.utku.veribenzerlik.similatiry.SimilarityCalculator;
import com.utku.veribenzerlik.similatiry.SimilarityCalculator.SimilarityResult;

import java.util.List;
import java.util.Map;

/**
 * Ana uygulama sınıfı - Tüm veri analizi süreçlerini yönetir
 */
public class VeriAnalizi {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== VERİ BENZERLİK ANALİZİ BAŞLADI ===");
            
            // 1. Veri üretimi
            System.out.println("\n1. Veri üretimi başlıyor...");
            List<Record> records = DataGenerator.generateRecords();
            System.out.println("✓ " + records.size() + " kayıt başarıyla üretildi");
            
            // 2. DuckDB'ye kaydetme
            System.out.println("\n2. Veriler DuckDB'ye kaydediliyor...");
            DuckDBWriter.write(records);
            System.out.println("✓ Veriler DuckDB'ye başarıyla kaydedildi");
            
            // 3. Benzerlik analizi
            System.out.println("\n3. Benzerlik analizi yapılıyor...");
            Map<String, double[]> columnData = SimilarityCalculator.extractNumericColumns(records);
            
            // Her kolon için benzerlik hesaplama
            System.out.println("\n=== KOLON BENZERLİK ANALİZİ ===");
            for (String column : columnData.keySet()) {
                analyzeColumnSimilarity(column, columnData.get(column));
            }
            
            // 2'li, 3'lü kolon karşılaştırmaları
            System.out.println("\n=== 2'Lİ KOLON KARŞILAŞTIRMALARI ===");
            List<SimilarityResult> pairwiseResults = SimilarityCalculator.calculatePairwiseSimilarities(columnData);
            for (SimilarityResult result : pairwiseResults) {
                System.out.println(result);
            }
            
            System.out.println("\n=== 3'LÜ KOLON KARŞILAŞTIRMALARI ===");
            List<SimilarityResult> tripleResults = SimilarityCalculator.calculateTripleSimilarities(columnData);
            for (SimilarityResult result : tripleResults) {
                System.out.println(result);
            }
            
            // Tüm kolonların hepsi için benzerlik analizi
            System.out.println("\n=== TÜM KOLONLAR İÇİN BENZERLİK ANALİZİ ===");
            analyzeAllColumnsSimilarity(columnData);
            
            // 4. Graph oluşturma ve kaydetme
            System.out.println("\n4. Benzerlik grafikleri oluşturuluyor...");
            createSimilarityGraphs(pairwiseResults, columnData);
            System.out.println("✓ Benzerlik grafikleri GraphViz formatında kaydedildi");
            
            System.out.println("\n=== VERİ BENZERLİK ANALİZİ TAMAMLANDI ===");
            
        } catch (Exception e) {
            System.err.println("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tek kolon için benzerlik analizi
     */
    private static void analyzeColumnSimilarity(String columnName, double[] data) {
        // Temel istatistikler
        double mean = calculateMean(data);
        double variance = calculateVariance(data, mean);
        double stdDev = Math.sqrt(variance);
        
        System.out.printf("%s: Ortalama=%.4f, Varyans=%.4f, Std Sapma=%.4f%n", 
                         columnName, mean, variance, stdDev);
    }
    
    /**
     * Tüm kolonlar arası benzerlik matrisi
     */
    private static void analyzeAllColumnsSimilarity(Map<String, double[]> columnData) {
        String[] columns = columnData.keySet().toArray(new String[0]);
        
        // Benzerlik matrisi başlığı yazdır
        System.out.print("         ");
        for (String col : columns) {
            System.out.printf("%12s", col.substring(0, Math.min(8, col.length())));
        }
        System.out.println();
        
        // Her satır için benzerlik değerlerini hesapla
        for (int i = 0; i < columns.length; i++) {
            System.out.printf("%8s ", columns[i].substring(0, Math.min(8, columns[i].length())));
            for (int j = 0; j < columns.length; j++) {
                if (i == j) {
                    System.out.printf("%12.4f", 1.0000); // Kendisiyle korelasyon 1
                } else {
                    double correlation = SimilarityCalculator.pearsonCorrelation(
                        columnData.get(columns[i]), 
                        columnData.get(columns[j])
                    );
                    System.out.printf("%12.4f", correlation);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * GraphViz formatında benzerlik grafikleri oluştur
     */
    private static void createSimilarityGraphs(List<SimilarityResult> results, Map<String, double[]> columnData) {
        try {
            // Yüksek benzerlikli bağlantılar için graf
            java.util.List<String[]> highSimilarityEdges = new java.util.ArrayList<>();
            
            for (SimilarityResult result : results) {
                // Benzerlik değeri 0.5'ten yüksek olanları al
                if (Math.abs(result.score) > 0.5) {
                    String[] edge = {
                        result.columnsInvolved.get(0),
                        result.columnsInvolved.get(1),
                        String.valueOf(result.score)
                    };
                    highSimilarityEdges.add(edge);
                }
            }
            
            // Yüksek benzerlik grafiği
            GraphVizWriter.writeDotFile("high_similarity_graph.dot", highSimilarityEdges);
            System.out.println("✓ Yüksek benzerlik grafiği: high_similarity_graph.dot");
            
            // PNG görsel oluşturmaya çalış
            GraphVizWriter.generateGraphImage("high_similarity_graph.dot", "high_similarity_graph.png");
            
            // Tüm benzerlikler grafiği
            java.util.List<String[]> allEdges = new java.util.ArrayList<>();
            for (SimilarityResult result : results) {
                String[] edge = {
                    result.columnsInvolved.get(0),
                    result.columnsInvolved.get(1),
                    String.valueOf(result.score)
                };
                allEdges.add(edge);
            }
            
            GraphVizWriter.writeDotFile("all_similarities_graph.dot", allEdges);
            System.out.println("✓ Tüm benzerlikler grafiği: all_similarities_graph.dot");
            GraphVizWriter.generateGraphImage("all_similarities_graph.dot", "all_similarities_graph.png");
            
            // Benzerlik matrisi heatmap'i
            createSimilarityHeatmap(columnData);
            
        } catch (Exception e) {
            System.err.println("Graf oluşturma hatası: " + e.getMessage());
        }
    }
    
    /**
     * Benzerlik matrisi heatmap'i oluştur
     */
    private static void createSimilarityHeatmap(Map<String, double[]> columnData) {
        try {
            String[] columns = columnData.keySet().toArray(new String[0]);
            double[][] matrix = new double[columns.length][columns.length];
            
            // Benzerlik matrisini doldur
            for (int i = 0; i < columns.length; i++) {
                for (int j = 0; j < columns.length; j++) {
                    if (i == j) {
                        matrix[i][j] = 1.0;
                    } else {
                        matrix[i][j] = SimilarityCalculator.pearsonCorrelation(
                            columnData.get(columns[i]), 
                            columnData.get(columns[j])
                        );
                    }
                }
            }
            
            GraphVizWriter.writeHeatmapDot("similarity_heatmap.dot", columns, matrix);
            System.out.println("✓ Benzerlik heatmap'i: similarity_heatmap.dot");
            GraphVizWriter.generateGraphImage("similarity_heatmap.dot", "similarity_heatmap.png");
            
        } catch (Exception e) {
            System.err.println("Heatmap oluşturma hatası: " + e.getMessage());
        }
    }
    
    // Yardımcı matematiksel fonksiyonlar
    private static double calculateMean(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }
    
    private static double calculateVariance(double[] data, double mean) {
        double sumSquaredDiffs = 0;
        for (double value : data) {
            double diff = value - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / data.length;
    }
}
