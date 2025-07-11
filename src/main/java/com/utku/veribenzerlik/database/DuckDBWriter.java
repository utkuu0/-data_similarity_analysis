package com.utku.veribenzerlik.database;

import com.utku.veribenzerlik.model.Record;

import java.sql.*;
import java.util.List;

public class DuckDBWriter {

    private static final String DB_URL = "jdbc:duckdb:veri_analiz.duckdb";

    public static void write(List<Record> records) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                // Tabloyu oluştur (eğer yoksa)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS records (
                        ID INTEGER,
                        Tamsayi1 INTEGER,
                        Tamsayi2 INTEGER,
                        GaussianTamsayi1 INTEGER,
                        GaussianReelSayi1 DOUBLE,
                        GaussianReelSayi2 DOUBLE,
                        GaussianDate1 INTEGER,
                        GaussianDate2 TIMESTAMP,
                        GaussianDate3 TIMESTAMP,
                        Binary1 VARCHAR
                    );
                """);
            }

            String sql = """
                INSERT INTO records (
                    ID, Tamsayi1, Tamsayi2, GaussianTamsayi1,
                    GaussianReelSayi1, GaussianReelSayi2,
                    GaussianDate1, GaussianDate2, GaussianDate3, Binary1
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int counter = 0;

                for (Record r : records) {
                    ps.setInt(1, r.ID);
                    ps.setInt(2, r.TamSayi1);
                    ps.setInt(3, r.TamSayi2);
                    ps.setInt(4, r.GaussianTamSayi1);
                    ps.setDouble(5, r.GaussianReelSayi1);
                    ps.setDouble(6, r.GaussianReelSayi2);
                    ps.setInt(7, r.GaussianDate1);
                    ps.setTimestamp(8, Timestamp.valueOf(r.GaussianDate2));
                    ps.setTimestamp(9, Timestamp.valueOf(r.GaussianDate3));
                    ps.setString(10, r.Binary1);

                    ps.executeUpdate(); // Batch yerine tek tek execute
                    counter++;

                    if (counter % 1_000 == 0) {
                        System.out.println(counter + " kayıt DuckDB'ye yazıldı.");
                    }
                }

                System.out.println("Veritabanına yazma tamamlandı. Toplam: " + counter);
            }
        }
    }
}
