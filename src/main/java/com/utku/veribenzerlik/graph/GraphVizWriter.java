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
            fw.write("    node [shape=box, style=filled, fillcolor=lightblue];\n");
            fw.write("    edge [fontsize=10];\n");
            
            for (String[] edge : edges) {
                String node1 = edge[0];
                String node2 = edge[1];
                double weight = Double.parseDouble(edge[2]);
                
                // Renk kodlaması: pozitif yeşil, negatif kırmızı
                String color = weight > 0 ? "green" : "red";
                String style = Math.abs(weight) > 0.7 ? "bold" : "normal";
                
                fw.write(String.format("    \"%s\" -- \"%s\" [label=\"%.3f\", color=%s, style=%s, penwidth=%.1f];\n", 
                    node1, node2, weight, color, style, Math.abs(weight) * 3 + 1));
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
            fw.write("    rankdir=TB;\n");
            fw.write("    node [shape=box, style=filled];\n");
            
            // Düğümleri oluştur
            for (int i = 0; i < columns.length; i++) {
                fw.write(String.format("    \"%s\" [fillcolor=lightgray];\n", columns[i]));
            }
            
            // Kenarları oluştur (benzerlik değerlerine göre)
            for (int i = 0; i < columns.length; i++) {
                for (int j = i + 1; j < columns.length; j++) {
                    double similarity = similarityMatrix[i][j];
                    if (Math.abs(similarity) > 0.1) { // Çok düşük değerleri filtrele
                        String color = similarity > 0 ? "green" : "red";
                        double alpha = Math.abs(similarity);
                        
                        fw.write(String.format("    \"%s\" -> \"%s\" [label=\"%.3f\", color=%s, penwidth=%.1f];\n",
                            columns[i], columns[j], similarity, color, alpha * 5 + 1));
                    }
                }
            }
            
            fw.write("}\n");
        }
    }
}
