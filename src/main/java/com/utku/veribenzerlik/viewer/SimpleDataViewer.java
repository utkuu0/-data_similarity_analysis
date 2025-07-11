package com.utku.veribenzerlik.viewer;

import java.sql.*;

/**
 * Basit veri görüntüleme aracı
 */
public class SimpleDataViewer {
    
    private static final String DB_URL = "jdbc:duckdb:veri_analiz.duckdb";
    
    public static void main(String[] args) {
        System.out.println("=== VERİTABANI VERİLERİ ===\n");
        
        showRecordCount();
        showFirstRecords();
        showStatistics();
        showSampleData();
    }
    
    private static void showRecordCount() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM records")) {
            
            if (rs.next()) {
                System.out.println("📊 Toplam Kayıt Sayısı: " + rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private static void showFirstRecords() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM records LIMIT 5")) {
            
            System.out.println("\n📋 İLK 5 KAYIT:");
            System.out.println("-".repeat(120));
            System.out.printf("%-6s %-10s %-10s %-12s %-12s %-12s %-10s %-15s\n",
                "ID", "TamSayi1", "TamSayi2", "GaussTam1", "GaussReel1", "GaussReel2", "GaussDate1", "Binary1");
            System.out.println("-".repeat(120));
            
            while (rs.next()) {
                String binary = rs.getString("Binary1");
                String shortBinary = binary.length() > 12 ? binary.substring(0, 12) + "..." : binary;
                
                System.out.printf("%-6d %-10d %-10d %-12d %-12.4f %-12.4f %-10d %-15s\n",
                    rs.getInt("ID"),
                    rs.getInt("Tamsayi1"),
                    rs.getInt("Tamsayi2"),
                    rs.getInt("GaussianTamsayi1"),
                    rs.getDouble("GaussianReelSayi1"),
                    rs.getDouble("GaussianReelSayi2"),
                    rs.getInt("GaussianDate1"),
                    shortBinary
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private static void showStatistics() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n📈 İSTATİSTİKLER:");
            System.out.println("-".repeat(80));
            System.out.printf("%-15s %-12s %-12s %-12s %-12s\n", "Kolon", "Ortalama", "Minimum", "Maksimum", "Std Sapma");
            System.out.println("-".repeat(80));
            
            String[] columns = {"Tamsayi1", "Tamsayi2", "GaussianTamsayi1", 
                               "GaussianReelSayi1", "GaussianReelSayi2", "GaussianDate1"};
            
            for (String col : columns) {
                ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT AVG(%s) as avg_val, MIN(%s) as min_val, MAX(%s) as max_val, " +
                    "STDDEV(%s) as std_val FROM records", col, col, col, col));
                
                if (rs.next()) {
                    System.out.printf("%-15s %-12.2f %-12.2f %-12.2f %-12.2f\n",
                        col,
                        rs.getDouble("avg_val"),
                        rs.getDouble("min_val"),
                        rs.getDouble("max_val"),
                        rs.getDouble("std_val"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private static void showSampleData() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM records WHERE ID IN (1, 1000, 10000, 100000, 500000, 1000000)")) {
            
            System.out.println("\n🎲 ÖRNEK VERİLER (Farklı ID'lerden):");
            System.out.println("-".repeat(140));
            System.out.printf("%-8s %-10s %-10s %-12s %-15s %-15s %-12s %-20s %-20s\n",
                "ID", "TamSayi1", "TamSayi2", "GaussTam1", "GaussReel1", "GaussReel2", "GaussDate1", "GaussDate2", "GaussDate3");
            System.out.println("-".repeat(140));
            
            while (rs.next()) {
                System.out.printf("%-8d %-10d %-10d %-12d %-15.4f %-15.4f %-12d %-20s %-20s\n",
                    rs.getInt("ID"),
                    rs.getInt("Tamsayi1"),
                    rs.getInt("Tamsayi2"),
                    rs.getInt("GaussianTamsayi1"),
                    rs.getDouble("GaussianReelSayi1"),
                    rs.getDouble("GaussianReelSayi2"),
                    rs.getInt("GaussianDate1"),
                    rs.getTimestamp("GaussianDate2"),
                    rs.getTimestamp("GaussianDate3")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }
}
