package com.utku.veribenzerlik.viewer;

import java.sql.*;
import java.util.Scanner;

/**
 * Veritabanındaki verileri görüntülemek için yardımcı sınıf
 */
public class DataViewer {
    
    private static final String DB_URL = "jdbc:duckdb:veri_analiz.duckdb";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== VERİ GÖRÜNTÜLEME MENÜSÜ ===");
            System.out.println("1. İlk 10 kaydı göster");
            System.out.println("2. Rastgele 10 kayıt göster");
            System.out.println("3. İstatistiksel özet");
            System.out.println("4. Belirli ID'deki kaydı göster");
            System.out.println("5. Kolon bazında min/max değerler");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz (0-5): ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    showFirstRecords();
                    break;
                case 2:
                    showRandomRecords();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 4:
                    System.out.print("Görmek istediğiniz ID: ");
                    int id = scanner.nextInt();
                    showRecordById(id);
                    break;
                case 5:
                    showMinMaxValues();
                    break;
                case 0:
                    System.out.println("Çıkış yapılıyor...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }
    
    private static void showFirstRecords() {
        String sql = "SELECT * FROM records LIMIT 10";
        executeQuery(sql, "İLK 10 KAYIT");
    }
    
    private static void showRandomRecords() {
        String sql = "SELECT * FROM records ORDER BY RANDOM() LIMIT 10";
        executeQuery(sql, "RASTGELE 10 KAYIT");
    }
    
    private static void showRecordById(int id) {
        String sql = "SELECT * FROM records WHERE ID = " + id;
        executeQuery(sql, "ID=" + id + " KAYDI");
    }
    
    private static void showStatistics() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n=== İSTATİSTİKSEL ÖZET ===");
            
            // Toplam kayıt sayısı
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM records");
            if (rs.next()) {
                System.out.println("Toplam Kayıt Sayısı: " + rs.getInt("total"));
            }
            
            // Her sayısal kolon için istatistik
            String[] columns = {"Tamsayi1", "Tamsayi2", "GaussianTamsayi1", 
                               "GaussianReelSayi1", "GaussianReelSayi2", "GaussianDate1"};
            
            for (String col : columns) {
                rs = stmt.executeQuery(String.format(
                    "SELECT AVG(%s) as ortalama, MIN(%s) as minimum, MAX(%s) as maksimum, " +
                    "STDDEV(%s) as std_sapma FROM records", col, col, col, col));
                
                if (rs.next()) {
                    System.out.printf("\n%s:\n", col);
                    System.out.printf("  Ortalama: %.4f\n", rs.getDouble("ortalama"));
                    System.out.printf("  Minimum:  %.4f\n", rs.getDouble("minimum"));
                    System.out.printf("  Maksimum: %.4f\n", rs.getDouble("maksimum"));
                    System.out.printf("  Std Sapma: %.4f\n", rs.getDouble("std_sapma"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private static void showMinMaxValues() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n=== MİN/MAX DEĞERLER ===");
            
            String[] columns = {"Tamsayi1", "Tamsayi2", "GaussianTamsayi1", 
                               "GaussianReelSayi1", "GaussianReelSayi2", "GaussianDate1"};
            
            for (String col : columns) {
                ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT MIN(%s) as min_val, MAX(%s) as max_val FROM records", col, col));
                
                if (rs.next()) {
                    System.out.printf("%-20s: Min=%-15.4f Max=%-15.4f\n", 
                        col, rs.getDouble("min_val"), rs.getDouble("max_val"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private static void executeQuery(String sql, String title) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== " + title + " ===");
            
            // Başlık satırı
            System.out.printf("%-8s %-12s %-12s %-15s %-15s %-15s %-12s %-20s %-20s %-20s\n",
                "ID", "TamSayi1", "TamSayi2", "GaussianTam1", "GaussianReel1", "GaussianReel2", 
                "GaussianDate1", "GaussianDate2", "GaussianDate3", "Binary1");
            
            System.out.println("-".repeat(180));
            
            // Veri satırları
            while (rs.next()) {
                System.out.printf("%-8d %-12d %-12d %-15d %-15.5f %-15.5f %-12d %-20s %-20s %-20s\n",
                    rs.getInt("ID"),
                    rs.getInt("Tamsayi1"),
                    rs.getInt("Tamsayi2"),
                    rs.getInt("GaussianTamsayi1"),
                    rs.getDouble("GaussianReelSayi1"),
                    rs.getDouble("GaussianReelSayi2"),
                    rs.getInt("GaussianDate1"),
                    rs.getTimestamp("GaussianDate2"),
                    rs.getTimestamp("GaussianDate3"),
                    rs.getString("Binary1").substring(0, Math.min(18, rs.getString("Binary1").length())) + "..."
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
}
