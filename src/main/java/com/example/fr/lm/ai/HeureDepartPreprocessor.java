package com.example.fr.lm.ai;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Préprocesseur CSV :
 *   – Lit le fichier source contenant la colonne "heure_depart" (format "HH: mm")
 *   – Remplace cette colonne par "heure_decimal" (ex : "08:30" → 8.5)
 *   – Écrit le résultat dans un nouveau fichier CSV
 */
public class HeureDepartPreprocessor {

    private static final String COL_SOURCE = "heure_depart";
    private static final String COL_TARGET = "heure_decimal";
    private static final String SEPARATOR  = ",";

    /**
     * Convertit le fichier CSV source et écrit le résultat dans le fichier de sortie.
     *
     * @param input  chemin du fichier source  (ex: livraison_retards_dataset.csv)
     * @param output chemin du fichier converti (ex : livraison_retards_dataset_converted.csv)
     */
    public static void convertPreprocessor(Path input, Path output) {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(input.toFile()));
            PrintWriter   writer  = new PrintWriter(new FileWriter(output.toFile()))
        ) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IllegalArgumentException("Fichier CSV vide : " + input);

            // ── Traitement de l'en-tête ──────────────────────────────────────
            String[] headers = headerLine.split(SEPARATOR);
            int heureIndex = -1;
            List<String> newHeaders = new ArrayList<>();

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equals(COL_SOURCE)) {
                    heureIndex = i;
                    newHeaders.add(COL_TARGET);   // remplacement du nom de colonne
                } else {
                    newHeaders.add(headers[i].trim());
                }
            }

            if (heureIndex == -1) {
                throw new IllegalArgumentException(
                    "Colonne '" + COL_SOURCE + "' introuvable dans : " + input);
            }

            writer.println(String.join(SEPARATOR, newHeaders));

            // ── Traitement des lignes de données ─────────────────────────────
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(SEPARATOR);
                List<String> newCols = new ArrayList<>();

                for (int i = 0; i < cols.length; i++) {
                    if (i == heureIndex) {
                        newCols.add(String.valueOf(toDecimal(cols[i].trim())));
                    } else {
                        newCols.add(cols[i].trim());
                    }
                }

                writer.println(String.join(SEPARATOR, newCols));
            }

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la conversion du fichier CSV", e);
        }
    }

    /**
     * Convertit une heure au format "HH: mm" en nombre décimal.
     * Ex : "08:30" → 8.5  |  "14:45" → 14.75
     */
    static double toDecimal(String hhmm) {
        String[] parts = hhmm.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Format d'heure invalide : " + hhmm);
        }
        int heures  = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return heures + (minutes / 60.0);
    }
}