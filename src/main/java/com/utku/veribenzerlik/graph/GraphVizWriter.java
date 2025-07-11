package com.utku.veribenzerlik.graph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GraphVizWriter {

    /**
     * DOT dosyası yazar
     */
    public static void writeDotFile(String filename, List<String[]> edges) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("graph Similarities {\n");
            fw.write("    node [shape=box, style=filled, fillcolor=lightblue, fontsize=12];\n");
            fw.write("    edge [fontsize=10];\n");
            fw.write("    layout=neato;\n");
            fw.write("    overlap=false;\n");
            fw.write("    splines=true;\n");
            
            // Eğer hiç edge yoksa, en azından düğümleri göster
            if (edges.isEmpty()) {
                fw.write("    \"Yas\" [fillcolor=lightgreen];\n");
                fw.write("    \"Maas\" [fillcolor=lightcoral];\n");
                fw.write("    \"CalismaSuresi\" [fillcolor=lightyellow];\n");
                fw.write("    \"PerformansPuani\" [fillcolor=lightpink];\n");
                fw.write("    \"VerilenGorevSayisi\" [fillcolor=lightcyan];\n");
                fw.write("    label=\"Veri Kolonları (Benzerlik bulunamadı)\";\n");
            } else {
                for (String[] edge : edges) {
                    String node1 = edge[0];
                    String node2 = edge[1];
                    double weight = Double.parseDouble(edge[2]);
                    
                    // Renk kodlaması ve kalınlık
                    String color = weight > 0 ? "\"#00AA00\"" : "\"#AA0000\"";
                    String style = Math.abs(weight) > 0.5 ? "bold" : "normal";
                    double penwidth = Math.abs(weight) * 5 + 1;
                    
                    fw.write(String.format("    \"%s\" -- \"%s\" [label=\"%.3f\", color=%s, style=%s, penwidth=%.1f];\n", 
                        node1, node2, weight, color, style, penwidth));
                }
            }
            fw.write("}\n");
        }
    }
    
    /**
     * DOT dosyasından PNG görsel oluşturur (GraphViz kurulu olması gerekir)
     */
    public static void generateGraphImage(String dotFilename, String outputFilename) {
        try {
            MutableGraph graph = new Parser().read(new File(dotFilename));
            Graphviz.fromGraph(graph)
                   .width(1200)
                   .render(Format.PNG)
                   .toFile(new File(outputFilename));
            System.out.println("✓ Graf görseli oluşturuldu: " + outputFilename);
        } catch (Exception e) {
            System.err.println("Graf görsel oluşturma hatası (GraphViz kurulu değil olabilir): " + e.getMessage());
            System.out.println("ℹ DOT dosyasını manuel olarak GraphViz ile işleyebilirsiniz: " + dotFilename);
        }
    }
    
    /**
     * Benzerlik matrisi için heatmap tarzı DOT dosyası oluşturur
     */
    public static void writeHeatmapDot(String filename, String[] columns, double[][] similarityMatrix) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("digraph SimilarityHeatmap {\n");
            fw.write("    rankdir=LR;\n");
            fw.write("    node [shape=box, style=filled, fontsize=10];\n");
            fw.write("    edge [fontsize=8];\n");
            fw.write("    layout=dot;\n");
            
            // Düğümleri renk kodlayarak oluştur
            for (int i = 0; i < columns.length; i++) {
                String[] colors = {"lightblue", "lightgreen", "lightcoral", "lightyellow", "lightpink"};
                String color = colors[i % colors.length];
                fw.write(String.format("    \"%s\" [fillcolor=%s, label=\"%s\"];\n", 
                    columns[i], color, columns[i]));
            }
            
            // Kenarları oluştur (benzerlik değerlerine göre)
            for (int i = 0; i < columns.length; i++) {
                for (int j = i + 1; j < columns.length; j++) {
                    double similarity = similarityMatrix[i][j];
                    if (Math.abs(similarity) > 0.05) { // Çok düşük değerleri filtrele
                        String color = similarity > 0 ? "\"#00AA00\"" : "\"#AA0000\"";
                        double penwidth = Math.abs(similarity) * 8 + 1;
                        String style = Math.abs(similarity) > 0.5 ? "bold" : "solid";
                        
                        fw.write(String.format("    \"%s\" -> \"%s\" [label=\"%.3f\", color=%s, penwidth=%.1f, style=%s];\n",
                            columns[i], columns[j], similarity, color, penwidth, style));
                        
                        // Çift yönlü bağlantı
                        fw.write(String.format("    \"%s\" -> \"%s\" [label=\"%.3f\", color=%s, penwidth=%.1f, style=%s];\n",
                            columns[j], columns[i], similarity, color, penwidth, style));
                    }
                }
            }
            
            fw.write("    label=\"Veri Kolonları Benzerlik Matrisi\";\n");
            fw.write("    labelloc=\"t\";\n");
            fw.write("}\n");
        }
    }
}
