package com.utku.veribenzerlik.generator;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.utku.veribenzerlik.model.Record;

public class DataGenerator {
    private static final int NUM_ROWS = 1_000_000;
    private static final Random random = new Random();

    // Dışarıdan erişilebilir veri üretim fonksiyonu
    public static List<Record> generateRecords() {
        List<Record> records = new ArrayList<>(NUM_ROWS);

        LocalDate today = LocalDate.now();
        LocalDate baseDate = LocalDate.of(1990, 1, 1);

        for (int i = 1; i <= NUM_ROWS; i++) {
            int ID = i;

            int tamsayi1 = random.nextInt(10_000_001);
            int tamsayi2 = random.nextInt(20_000_001) - 10_000_000;

            int gaussianTamsayi1 = (int) Math.round(random.nextGaussian() * 1000);

            double gaussianReelSayi1 = roundRandomDecimal(random.nextGaussian() * 1000, 5);
            double gaussianReelSayi2 = roundRandomDecimal(0.5 + random.nextGaussian(), 5);

            double gaussianDate1Val = random.nextGaussian() * Math.sqrt(10_000);
            LocalDate gaussianDate1Date = today.plusDays((long) gaussianDate1Val);
            int gaussianDate1 = gaussianDate1Date.getDayOfMonth();

            double gaussianDate2Val = random.nextGaussian() * Math.sqrt(1000);
            LocalDateTime gaussianDate2 = baseDate.atStartOfDay().plusSeconds((long) gaussianDate2Val);

            double gaussianDate3Val = random.nextGaussian() * Math.sqrt(10);
            LocalDateTime gaussianDate3 = LocalDateTime.now().plusSeconds((long) gaussianDate3Val);

            int length = 2 + random.nextInt(49); // 2 to 50
            StringBuilder binary1Builder = new StringBuilder();
            for (int j = 0; j < length; j++) {
                int val = (int) Math.round(128 + random.nextGaussian() * 10);
                val = Math.max(0, Math.min(255, val));
                binary1Builder.append(String.format("%02X", val));
            }
            String binary1 = binary1Builder.toString();

            Record rec = new Record(ID, tamsayi1, tamsayi2, gaussianTamsayi1,
                    gaussianReelSayi1, gaussianReelSayi2,
                    gaussianDate1, gaussianDate2, gaussianDate3, binary1);
            records.add(rec);

            if (i % 100_000 == 0) {
                System.out.println(i + " kayıt oluşturuldu.");
            }
        }

        return records;
    }

    private static double roundRandomDecimal(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }

    public static void main(String[] args) {
        List<Record> records = generateRecords();
        System.out.println("Toplam kayıt üretildi: " + records.size());
        // Örneğin burada DuckDBWriter.write(records); çağrılabilir.
    }
}
