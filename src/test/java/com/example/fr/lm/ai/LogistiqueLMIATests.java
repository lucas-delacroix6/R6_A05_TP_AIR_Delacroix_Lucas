package com.example.fr.lm.ai;

import org.junit.jupiter.api.*;
import org.tribuo.*;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.evaluation.LabelEvaluation;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.classification.sgd.linear.LogisticRegressionTrainer;
import org.tribuo.data.columnar.FieldProcessor;
import org.tribuo.data.columnar.RowProcessor;
import org.tribuo.data.columnar.processors.field.DoubleFieldProcessor;
import org.tribuo.data.columnar.processors.field.IdentityProcessor;
import org.tribuo.data.columnar.processors.response.FieldResponseProcessor;
import org.tribuo.data.csv.CSVDataSource;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.impl.ArrayExample;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogistiqueLMIATests {
    private static final String fileName = "livraison_retards_dataset.csv";
    private static final String newFileName = "livraison_retards_dataset_converted.csv";
    private static final String modelFile = "livraison_regressor.ser";
    private static final Path input = Paths.get("src", "main", "resources", fileName);
    private static final Path output = Paths.get("src", "main", "resources", newFileName);
    private static final Path MODEL_PATH = Paths.get("src", "main", "resources", modelFile);
    private static LabelFactory labelFactory;
    private static LinkedHashMap<String, FieldProcessor> fieldProcessors;

    @BeforeAll
    public static void setUp() {
        labelFactory = new LabelFactory(); // Définir le label factory
        fieldProcessors = new LinkedHashMap<>(); // Définir les extracteurs de colonnes
        configFile(); // encodage des données
    }
    @AfterAll
    public static void tearDown() { // Nettoyage des ressources
        if (output.toFile().exists()) {
            boolean deleted = output.toFile().delete();
            assertTrue(deleted, "Le fichier converti doit être supprimé après les tests");
        }
        if (MODEL_PATH.toFile().exists()) {
            boolean deleted = MODEL_PATH.toFile().delete();
            assertTrue(deleted, "Le fichier du modèle doit être supprimé après les tests");
        }
    }

    private static void configFile() {

        // nouveau champ calculé prétraité
        fieldProcessors.put("heure_decimal", new DoubleFieldProcessor("heure_decimal"));

        // distance_km est déjà numérique
        fieldProcessors.put("distance_km", new DoubleFieldProcessor("distance_km"));

        // pluie, jour_semaine, vehicule_type = colonnes catégorielles
        fieldProcessors.put("pluie", new IdentityProcessor("pluie"));
        fieldProcessors.put("jour_semaine", new IdentityProcessor("jour_semaine"));
        fieldProcessors.put("vehicule_type", new IdentityProcessor("vehicule_type"));

        // Le processeur de la colonne de sortie (retard)
        FieldResponseProcessor<Label> responseProcessor =
                new FieldResponseProcessor<>("retard", "non", labelFactory);

        // Création du RowProcessor avec les éléments définis
        rowProcessor = new RowProcessor<>(responseProcessor, fieldProcessors);
    }

    @Test
    @Order(1)
    void prepareDatasets() {
        // Convertir heure_depart en format numérique
        HeureDepartPreprocessor.convertPreprocessor(input, output);
        assertTrue(output.toFile().exists(), "Le fichier converti doit exister");
        assertTrue(output.toFile().length() > 0, "Le fichier converti ne doit pas être vide");
    }

    private static RowProcessor<Label> rowProcessor;
    private static CSVDataSource<Label> dataSource;
    private static MutableDataset<Label> train;
    private static MutableDataset<Label> test;
    private static Model<Label> model;

    @Test
    @Order(2)
    void loadDatasets() throws IOException {
        // Charger le fichier CSV fourni (livraison_retards_dataset.csv)
        dataSource = new CSVDataSource<>(
                Paths.get("src", "main", "resources", newFileName),
                rowProcessor,
                true // skip header
        );
        assertNotNull(dataSource, "La source de données ne doit pas être null");
        assertFalse(dataSource.toString().isEmpty(), "La source de données doit contenir des données");
    }

    @Test
    @Order(3)
    void splitTrainTest() { // Split train/test → Utiliser 80% des données pour l’entraînement, 20% pour les tests
        var splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
        train = new MutableDataset<>(splitter.getTrain());
        test = new MutableDataset<>(splitter.getTest());
    }
    @Test
    @Order(4)
    void training() { // Entraînement du modèle
        var trainer = new LogisticRegressionTrainer();
        model = trainer.train(train);
    }
    @Test
    @Order(5)
    void evaluator() { // Évaluation => Calculer l’accuracy, matrice de confusion, f1-score
        var evaluator = new LabelEvaluator();
        LabelEvaluation evaluation = evaluator.evaluate(model, test);
        System.out.println("Résultats :");
        System.out.println(evaluation.toString());
    }
    @Test
    @Order(6)
    void saveModel() throws Exception { // Sauvegarde du modèle
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(MODEL_PATH.toFile()))) {
            objectOutputStream.writeObject(model);
        }
    }

    @Test
    @Order(7)
    void predictor() throws Exception {
        File modelFile = MODEL_PATH.toFile();
        Model<Label> loadedModel;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(modelFile))) {
            loadedModel = (Model<Label>) objectInputStream.readObject();
        }

        System.out.println("Features attendues par le modèle :");
        loadedModel.getFeatureIDMap().forEach(name -> System.out.println(" - " + name));

        Example<Label> example = new ArrayExample<>(new Label("non"));
        // l’exemple doit avoir les mêmes noms de features que ceux générés par le modèle (FieldProcessor)
        example.add(new Feature("distance_km@value", 120.0));
        example.add(new Feature("heure_decimal@value", 8.0));
        example.add(new Feature("pluie@non", 0.0));
        example.add(new Feature("jour_semaine@mercredi", 2.0));
        example.add(new Feature("vehicule_type@camionnette", 1.0));

        // === Prédiction sur l’exemple ===
        Prediction<Label> prediction = loadedModel.predict(example);
        System.out.println("🎯 Prédiction : " + prediction.getOutput());
    }

}

