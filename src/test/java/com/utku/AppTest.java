package com.utku;

import static org.junit.Assert.*;

import com.utku.veribenzerlik.generator.DataGenerator;
import com.utku.veribenzerlik.model.Record;
import com.utku.veribenzerlik.similatiry.SimilarityCalculator;
import com.utku.veribenzerlik.similatiry.SimilarityCalculator.SimilarityResult;

import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Veri benzerlik analizi için unit testler
 */
public class AppTest {
    
    @Test
    public void testDataGeneration() {
        List<Record> records = DataGenerator.generateRecords();
        assertNotNull("Kayıt listesi null olmamalı", records);
        assertEquals("1 milyon kayıt üretilmeli", 1_000_000, records.size());
        
        // İlk kayıt kontrolü
        Record firstRecord = records.get(0);
        assertEquals("İlk kayıtın ID'si 1 olmalı", 1, firstRecord.ID);
        assertNotNull("Binary1 alanı null olmamalı", firstRecord.Binary1);
        assertTrue("Binary1 uzunluğu 4-100 karakter arası olmalı", 
                  firstRecord.Binary1.length() >= 4 && firstRecord.Binary1.length() <= 100);
    }
    
    @Test
    public void testSimilarityCalculation() {
        // Test verisi oluştur
        List<Record> testRecords = DataGenerator.generateRecords();
        
        // Sadece ilk 1000 kayıtla test yap (performans için)
        testRecords = testRecords.subList(0, 1000);
        
        Map<String, double[]> columnData = SimilarityCalculator.extractNumericColumns(testRecords);
        
        // Kolon sayısı kontrolü
        assertEquals("6 sayısal kolon olmalı", 6, columnData.size());
        
        // Her kolonun veri uzunluğu kontrolü
        for (double[] data : columnData.values()) {
            assertEquals("Her kolonda 1000 veri olmalı", 1000, data.length);
        }
        
        // 2'li benzerlik hesaplama testi
        List<SimilarityResult> pairwiseResults = SimilarityCalculator.calculatePairwiseSimilarities(columnData);
        assertEquals("15 adet 2'li karşılaştırma olmalı (6 kolon için C(6,2))", 15, pairwiseResults.size());
        
        // 3'lü benzerlik hesaplama testi
        List<SimilarityResult> tripleResults = SimilarityCalculator.calculateTripleSimilarities(columnData);
        assertEquals("20 adet 3'lü karşılaştırma olmalı (6 kolon için C(6,3))", 20, tripleResults.size());
    }
    
    @Test
    public void testPearsonCorrelation() {
        // Test verisi: mükemmel pozitif korelasyon
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 6, 8, 10};
        
        double correlation = SimilarityCalculator.pearsonCorrelation(x, y);
        assertEquals("Mükemmel pozitif korelasyon 1.0 olmalı", 1.0, correlation, 0.0001);
        
        // Test verisi: negatif korelasyon
        double[] z = {5, 4, 3, 2, 1};
        double negativeCorrelation = SimilarityCalculator.pearsonCorrelation(x, z);
        assertEquals("Mükemmel negatif korelasyon -1.0 olmalı", -1.0, negativeCorrelation, 0.0001);
        
        // Test verisi: korelasyon yok
        double[] w = {1, 1, 1, 1, 1};
        double zeroCorrelation = SimilarityCalculator.pearsonCorrelation(x, w);
        assertEquals("Sabit değerlerle korelasyon 0 olmalı", 0.0, zeroCorrelation, 0.0001);
    }
    
    @Test
    public void testRecordDataTypes() {
        List<Record> records = DataGenerator.generateRecords();
        Record sample = records.get(0);
        
        // Veri tipi kontrolleri
        assertTrue("TamSayi1 0-10M arası olmalı", sample.TamSayi1 >= 0 && sample.TamSayi1 <= 10_000_000);
        assertTrue("TamSayi2 -10M ile +10M arası olmalı", sample.TamSayi2 >= -10_000_000 && sample.TamSayi2 <= 10_000_000);
        assertTrue("GaussianDate1 1-31 arası olmalı", sample.GaussianDate1 >= 1 && sample.GaussianDate1 <= 31);
        assertNotNull("GaussianDate2 null olmamalı", sample.GaussianDate2);
        assertNotNull("GaussianDate3 null olmamalı", sample.GaussianDate3);
    }
}
