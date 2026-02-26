package com.example.fr.lm.ai;

import org.junit.jupiter.api.*;
import org.tribuo.*;
import org.tribuo.classification.*;
import org.tribuo.classification.evaluation.*;
import org.tribuo.classification.sgd.linear.LogisticRegressionTrainer;
import org.tribuo.classification.dtree.CARTClassificationTrainer;
import org.tribuo.data.csv.CSVDataSource;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.impl.ArrayExample;
import org.tribuo.data.columnar.FieldProcessor;
import org.tribuo.data.columnar.RowProcessor;
import org.tribuo.data.columnar.processors.field.IdentityProcessor;
import org.tribuo.data.columnar.processors.response.FieldResponseProcessor;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 💡 SOLUTION – TP Pour aller plus loin
 * <p>
 * Objectif : prédire s'il pleut (pluie = oui/non)
 * à partir du jour de la semaine et du retard.
 * <p>
 * Features : jour_semaine, retard
 * Label : pluie
 * <p>
 * Modèles utilisés :
 * – LogisticRegressionTrainer (régression logistique).
 * – CARTClassificationTrainer (arbre de décision).
 * 
 * <p>
 * Prédiction finale :
 *   jour_semaine → vendredi
 *   retard → Oui
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogistiquePluieIATests {

    // ── Chemins ───────────────────────────────────────────────────────────────
    private static final String CSV_FILE  = "livraison_retards_dataset.csv";
    private static final String MODEL_LR  = "pluie_logistic_model.ser";
    private static final String MODEL_CART = "pluie_cart_model.ser";

    private static final Path INPUT      = Paths.get("src", "main", "resources", CSV_FILE);
    private static final Path MODEL_LR_PATH   = Paths.get("src", "main", "resources", MODEL_LR);
    private static final Path MODEL_CART_PATH = Paths.get("src", "main", "resources", MODEL_CART);

    // ── État partagé entre les tests ──────────────────────────────────────────
    private static LabelFactory labelFactory;
    private static LinkedHashMap<String, FieldProcessor> fieldProcessors;
    private static RowProcessor<Label> rowProcessor;
    private static CSVDataSource<Label> dataSource;
    private static MutableDataset<Label> train;
    private static MutableDataset<Label> test;
    private static Model<Label> lrModel;   // Logistic Regression
    private static Model<Label> cartModel; // CART Decision Tree

    // ── Initialisation ────────────────────────────────────────────────────────
    @BeforeAll
    public static void setUp() {
        labelFactory   = new LabelFactory();
        fieldProcessors = new LinkedHashMap<>();
        configFile();
    }

    /**
     * Encodage des features :
     *   – jour_semaine → catégorielle (IdentityProcessor)
     *   – retard → catégorielle (IdentityProcessor)
     * Label prédit : pluie
     */
    private static void configFile() {
        // Features retenues pour la prédiction de la pluie
        fieldProcessors.put("jour_semaine", new IdentityProcessor("jour_semaine"));
        fieldProcessors.put("retard",       new IdentityProcessor("retard"));

        // La colonne cible devient "pluie".
        FieldResponseProcessor<Label> responseProcessor =
                new FieldResponseProcessor<>("pluie", "non", labelFactory);

        rowProcessor = new RowProcessor<>(responseProcessor, fieldProcessors);
    }

    // ── Nettoyage ─────────────────────────────────────────────────────────────
    @AfterAll
    public static void tearDown() {
        deleteIfExists(MODEL_LR_PATH,   "modèle Logistic Regression");
        deleteIfExists(MODEL_CART_PATH, "modèle CART");
    }

    private static void deleteIfExists(Path path, String label) {
        if (path.toFile().exists()) {
            boolean deleted = path.toFile().delete();
            assertTrue(deleted, "Le fichier " + label + " doit être supprimé après les tests");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 1 – Chargement des données
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("Chargement du CSV (features : jour_semaine, retard  →  label : pluie)")
    void loadDatasets() {
        dataSource = new CSVDataSource<>(INPUT, rowProcessor, true);
        assertNotNull(dataSource, "La source de données ne doit pas être null");
        assertFalse(dataSource.toString().isEmpty(), "La source de données doit contenir des données");
        System.out.println("Données chargées depuis : " + INPUT.toAbsolutePath());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 2 – Division Train / Test  (80 % / 20 %)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    @DisplayName("Division Train/Test 80-20")
    void splitTrainTest() {
        var splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
        train = new MutableDataset<>(splitter.getTrain());
        test  = new MutableDataset<>(splitter.getTest());
        System.out.printf("Train : %d exemples  |  Test : %d exemples%n",
                train.size(), test.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 3 – Entraînement  Régression Logistique
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    @DisplayName("Entraînement – Régression Logistique")
    void trainingLogisticRegression() {
        var trainer = new LogisticRegressionTrainer();
        lrModel = trainer.train(train);
        assertNotNull(lrModel, "Le modèle LR ne doit pas être null");
        System.out.println("Modèle Logistic Regression entraîné.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 4 – Entraînement  Arbre de décision CART
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    @DisplayName("Entraînement – Arbre de décision CART")
    void trainingCART() {
        var trainer = new CARTClassificationTrainer();
        cartModel = trainer.train(train);
        assertNotNull(cartModel, "Le modèle CART ne doit pas être null");
        System.out.println("Modèle CART entraîné.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 5 – Évaluation Régression Logistique
    //          Accuracy | Matrice de confusion | F1-score
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(5)
    @DisplayName("Évaluation – Régression Logistique (accuracy, confusion, f1)")
    void evaluateLR() {
        var evaluator  = new LabelEvaluator();
        LabelEvaluation eval = evaluator.evaluate(lrModel, test);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("  RÉGRESSION LOGISTIQUE – Résultats sur le jeu de test");
        System.out.println("══════════════════════════════════════════════════");
        System.out.printf(" Accuracy  : %.4f%n", eval.accuracy());
        eval.getConfusionMatrix().getDomain().getDomain().forEach(label ->
                System.out.printf(" F1 (%s) : %.4f%n", label.getLabel(), eval.f1(label))
        );
        System.out.println();
        System.out.println(" Matrice de confusion :");
        System.out.println(eval.getConfusionMatrix().toString());
        System.out.println(eval);
        System.out.println("══════════════════════════════════════════════════");

        /*
         * Interprétation attendue :
         *  – Accuracy : % de prédictions correctes (oui/non pour pluie)
         *  – F1(oui)   : qualité de détection des jours de pluie
         *  – F1(non)   : qualité de détection des jours sans pluie
         *  – Matrice : TP/FP/TN/FN par classe
         *
         *  Comme seules 2 features (jour_semaine, retard) sont utilisées,
         *  l'accuracy attendue est modérée (~55-65%).
         *  Cela démontre l'importance du feature engineering.
         */
        assertTrue(eval.accuracy() > 0, "L'accuracy doit être positive");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 6 – Évaluation Arbre CART + arbre de probabilités
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(6)
    @DisplayName("Évaluation – CART + arbre de probabilités")
    void evaluateCART() {
        var evaluator  = new LabelEvaluator();
        LabelEvaluation eval = evaluator.evaluate(cartModel, test);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println(" CART – Résultats sur le jeu de test");
        System.out.println("══════════════════════════════════════════════════");
        System.out.printf(" Accuracy  : %.4f%n", eval.accuracy());
        eval.getConfusionMatrix().getDomain().getDomain().forEach(label ->
                System.out.printf(" F1 (%s) : %.4f%n", label.getLabel(), eval.f1(label))
        );
        System.out.println();
        System.out.println(" Matrice de confusion :");
        System.out.println(eval.getConfusionMatrix().toString());
        System.out.println(eval);
        System.out.println("══════════════════════════════════════════════════");

        // Arbre de probabilités (structure interne du CART)
        System.out.println("\nArbre de décision CART (provenance) :");
        System.out.println(cartModel.toString());

        /*
         * Interprétation de l'arbre CART :
         *  – L'arbre divise récursivement les données selon les features
         *  – Chaque nœud représente une règle de décision
         *  – Les feuilles donnent la classe prédite et les probabilités associées
         *  – Ex : SI jour_semaine=vendredi ET retard=oui ALORS pluie=oui (prob 0.70)
         *
         *  L'arbre permet de visualiser quels jours et quels retards
         *  sont les plus fortement corrélés à la pluie.
         */
        assertTrue(eval.accuracy() > 0, "L'accuracy CART doit être positive");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 7 – Sauvegarde des modèles
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(7)
    @DisplayName("Sauvegarde des modèles (.ser)")
    void saveModels() throws Exception {
        saveModel(lrModel,   MODEL_LR_PATH,   "Logistic Regression");
        saveModel(cartModel, MODEL_CART_PATH, "CART");
    }

    private void saveModel(Model<Label> model, Path path, String name) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(model);
        }
        assertTrue(path.toFile().exists(), "Le fichier " + name + " doit exister");
        System.out.printf("Modèle %s sauvegardé → %s%n", name, path.toAbsolutePath());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 8 – Prédiction sur l'échantillon demandé
    //          jour_semaine => Vendredi   |   retard => Oui
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(8)
    @DisplayName("Prédiction : jour_semaine=vendredi, retard=oui  →  pluie = ?")
    void predictor() throws Exception {

        // ── Chargement des modèles sauvegardés ──────────────────────────────
        Model<Label> loadedLR   = loadModel(MODEL_LR_PATH);
        Model<Label> loadedCART = loadModel(MODEL_CART_PATH);

        // ── Création de l'exemple à prédire ─────────────────────────────────
        /*
         *     Les noms de features DOIVENT correspondre exactement
         *     aux noms générés par les FieldProcessors :
         *       IdentityProcessor("jour_semaine") → "jour_semaine@vendredi"
         *       IdentityProcessor("retard")       → "retard@oui"
         */
        Example<Label> exemple = new ArrayExample<>(new Label("oui")); // label dummy
        exemple.add(new Feature("jour_semaine@vendredi", 1.0));
        exemple.add(new Feature("retard@oui",            1.0));

        // ── Prédictions ──────────────────────────────────────────────────────
        Prediction<Label> predLR   = loadedLR.predict(exemple);
        Prediction<Label> predCART = loadedCART.predict(exemple);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("PRÉDICTION FINALE");
        System.out.println(" Entrée : jour_semaine = vendredi  |  retard = oui");
        System.out.println("══════════════════════════════════════════════════");
        System.out.println(" Régression Logistique → pluie = " + predLR.getOutput().getLabel());
        System.out.println(" Arbre CART            → pluie = " + predCART.getOutput().getLabel());
        System.out.println();

        // Affichage des probabilités par classe
        predLR.getOutputScores().forEach((key, value) -> System.out.printf("   pluie=%-5s : %.4f%n",
                value.getLabel(), value.getScore()));
        System.out.println();
        System.out.println(" Probabilités (CART) :");
        predCART.getOutputScores().forEach((key, value) -> System.out.printf("   pluie=%-5s : %.4f%n",
                value.getLabel(), value.getScore()));
        System.out.println("══════════════════════════════════════════════════");

        /*
         * Interprétation :
         *  Un vendredi avec retard est statistiquement plus susceptible
         *  d'être un jour de pluie selon les patterns du dataset.
         *  Le modèle prédit la classe avec la probabilité la plus élevée.
         */
        assertNotNull(predLR.getOutput().getLabel(),   "LR  doit produire un résultat");
        assertNotNull(predCART.getOutput().getLabel(), "CART doit produire un résultat");
    }

    // ── Utilitaire ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Model<Label> loadModel(Path path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Model<Label>) ois.readObject();
        }
    }
}