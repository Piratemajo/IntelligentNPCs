package es.srjavierdev.intelligentNPCs.ai;



import es.srjavierdev.intelligentNPCs.memory.Interaction;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import es.srjavierdev.intelligentNPCs.IntelligentNPCs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeuralNetwork {
    private MultiLayerNetwork network;
    private Vocabulary vocabulary;
    private String personality;

    public void initialize(String personality) {
        this.personality = personality;

        // Configuración de la red neuronal
        int inputSize = 256;  // Tamaño del vector de entrada
        int hiddenSize = 128; // Neuronas en capa oculta
        int outputSize = 256; // Tamaño del vector de salida

        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.001))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(inputSize)
                        .nOut(hiddenSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(hiddenSize)
                        .nOut(hiddenSize)
                        .activation(Activation.RELU)
                        .build())
                .build();

        this.network = new MultiLayerNetwork(config);
        this.network.init();

        // Inicializar vocabulario
        this.vocabulary = new Vocabulary();
        vocabulary.loadForPersonality(personality);

        // Cargar modelo pre-entrenado si existe
        loadModel();
    }


    public String generateResponse(String input, String expectedOutput ,ArrayList<Interaction> context, int reputation) {
        // Convertir contexto a lista de pares clave-valor
        List<Map.Entry<String, String>> contextList = new ArrayList<>();
        for (Interaction interaction : context) {
            contextList.add(Map.entry(interaction.getInput(), interaction.getResponse()));
        }

        // Codificar entrada y contexto
        INDArray inputVector = encodeInput(input, contextList, reputation);

        // Generar respuesta
        INDArray outputVector = network.output(inputVector);
        return vocabulary.decode(outputVector);
    }

    public void train(String input, String expectedOutput, List<Map.Entry<String, String>> context, int reputation) {
        INDArray inputVector = encodeInput(input, context, reputation);
        INDArray outputVector = vocabulary.encode(expectedOutput);

        DataSet dataSet = new DataSet(inputVector, outputVector);
        network.fit(dataSet);

        // Guardar el modelo después del entrenamiento
        saveModel();
    }

    private INDArray encodeInput(String input, List<Map.Entry<String, String>> context, int reputation) {
        // Codificar entrada actual
        INDArray inputEncoding = vocabulary.encode(input);

        // Codificar contexto
        INDArray contextEncoding = Nd4j.zeros(inputEncoding.shape());
        for (Map.Entry<String, String> entry : context) {
            contextEncoding.addi(vocabulary.encode(entry.getKey() + " " + entry.getValue()));
        }
        if (!context.isEmpty()) {
            contextEncoding.divi(context.size());
        }

        // Codificar reputación (normalizada entre -1 y 1)
        INDArray reputationEncoding = Nd4j.scalar(reputation / 100.0);

        // Concatenar todo
        return Nd4j.hstack(inputEncoding, contextEncoding, reputationEncoding);
    }

    private void saveModel() {
        try {
            File modelFile = new File(IntelligentNPCs.getInstance().getDataFolder(),
                    "models/" + personality + ".model");
            ModelSerializer.writeModel(network, modelFile, true);
        } catch (IOException e) {
            IntelligentNPCs.getInstance().getLogger().warning("Error guardando modelo: " + e.getMessage());
        }
    }

    private void loadModel() {
        File modelFile = new File(IntelligentNPCs.getInstance().getDataFolder(),
                "models/" + personality + ".model");
        if (modelFile.exists()) {
            try {
                this.network = ModelSerializer.restoreMultiLayerNetwork(modelFile);
            } catch (IOException e) {
                IntelligentNPCs.getInstance().getLogger().warning("Error cargando modelo: " + e.getMessage());
            }
        }
    }

    // Clase interna para manejo de vocabulario
    private static class Vocabulary {
  
        // Implementación completa del manejo de vocabulario
        public void loadForPersonality(String personality) {
            // Cargar vocabulario específico de la personalidad 
            // Aquí se cargaría el vocabulario desde un archivo o base de datos
            System.out.println("Cargando vocabulario para la personalidad: " + personality);
            
        }

        public INDArray encode(String text) {
            // Convertir texto a vector
            return Nd4j.rand(1, 256); // Implementación simplificada
        }

        public String decode(INDArray vector) {
            return "Respuesta generada por la red neuronal";
        }
    }
}