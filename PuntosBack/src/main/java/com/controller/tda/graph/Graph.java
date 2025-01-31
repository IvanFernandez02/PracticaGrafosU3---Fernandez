package com.controller.tda.graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.controller.Dao.PuntoEncuentroDao;
import com.controller.tda.list.LinkedList;
import com.controller.tda.list.ListEmptyException;
import com.models.PuntoEncuentro;

import static java.lang.Math.*;

public abstract class Graph {

    // Ruta para guardar el archivo
    public static String filePath = "data/";
    private Map<Integer, PuntoEncuentro> vertexModels = new HashMap<>();
    public abstract Integer nro_vertices();
    public abstract Integer nro_edges();
    public abstract Boolean is_edges(Integer v1, Integer v2) throws Exception;
    public abstract Float wieght_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2, Float weight) throws Exception;
    public abstract LinkedList<Adyecencia> adyecencias(Integer v1);

    // Método para obtener el ID de un vértice
    public Integer getVertex(Integer label) throws Exception {
        return label;
    }

    @Override
    public String toString() {
        StringBuilder grafo = new StringBuilder();
        try {
            for (int i = 1; i <= this.nro_vertices(); i++) {
                grafo.append("Vertice: ").append(i).append("\n");
                LinkedList<Adyecencia> lista = this.adyecencias(i);
                if (!lista.isEmpty()) {
                    Adyecencia[] ady = lista.toArray();
                    for (Adyecencia a : ady) {
                        grafo.append("ady: V").append(a.getdestination())
                                .append(" weight: ").append(a.getweight()).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grafo.toString();
    }

    public void saveGraphLabel(String filename) throws Exception {
        JsonArray graphArray = new JsonArray();
        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject vertexObject = new JsonObject();
            vertexObject.addProperty("labelId", this.getVertex(i));

            JsonArray destinationsArray = new JsonArray();
            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);
            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject destinationObject = new JsonObject();
                    destinationObject.addProperty("from", this.getVertex(i));
                    destinationObject.addProperty("to", adj.getdestination());
                    destinationsArray.add(destinationObject);
                }
            }
            vertexObject.add("destinations", destinationsArray);
            graphArray.add(vertexObject);
        }

        Gson gson = new Gson();
        String json = gson.toJson(graphArray);

        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (FileWriter fileWriter = new FileWriter(filePath + filename)) {
            fileWriter.write(json);
        }
    }

    public void cargarModelosDesdeDao() throws ListEmptyException {
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
        LinkedList<PuntoEncuentro> puntoencuentroList = puntoencuentroDao.getListAll();

        for (int i = 0; i < puntoencuentroList.getSize(); i++) {
            PuntoEncuentro puntoencuentro = puntoencuentroList.get(i);
            vertexModels.put(puntoencuentro.getid(), puntoencuentro);
        }
    }

    public void loadGraph(String filename) throws Exception {

        try (FileReader fileReader = new FileReader(filePath + filename)) {
            Gson gson = new Gson();
            JsonArray graphArray = gson.fromJson(fileReader, JsonArray.class);

            for (JsonElement vertexElement : graphArray) {
                JsonObject vertexObject = vertexElement.getAsJsonObject();

                Integer labelId = vertexObject.get("labelId").getAsInt();

                PuntoEncuentro model = vertexModels.get(labelId);

                if (model == null) {
                    continue;
                }

                this.addVertexWithModel(labelId, model);

                JsonArray destinationsArray = vertexObject.getAsJsonArray("destinations");

                for (JsonElement destinationElement : destinationsArray) {
                    JsonObject destinationObject = destinationElement.getAsJsonObject();

                    Integer from = destinationObject.get("from").getAsInt();
                    Integer to = destinationObject.get("to").getAsInt();

                    PuntoEncuentro modelFrom = vertexModels.get(from);
                    PuntoEncuentro modelTo = vertexModels.get(to);

                    if (modelFrom == null || modelTo == null) {
                    } else {

                        Float weight = (float) calcularDistancia(modelFrom, modelTo);

                        this.add_edge(from, to, weight);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Borrar Adyacencias para crear nuevas
    public void clearEdges() {
        for (int i = 1; i <= this.nro_vertices(); i++) {
            this.adyecencias(i).reset();
        }
    }

    // Crear las nuevas adyacencias de manera aleatoria
    public void loadGraphWithRandomEdges(String filename) throws Exception {
        loadGraph(filename);
        cargarModelosDesdeDao();
        clearEdges();
        Random random = new Random();
        for (int i = 1; i <= this.nro_vertices(); i++) {
            LinkedList<Adyecencia> existingEdges = this.adyecencias(i);
            int connectionsCount = existingEdges.getSize();

            while (connectionsCount < 3) {
                // Generamos un vértice aleatorio
                int randomVertex = random.nextInt(this.nro_vertices()) + 1;
                while (randomVertex == i || is_edges(i, randomVertex)) {
                    randomVertex = random.nextInt(this.nro_vertices()) + 1;
                }
                PuntoEncuentro modelFrom = vertexModels.get(i);
                PuntoEncuentro modelTo = vertexModels.get(randomVertex);

                float weight = (float) calcularDistancia(modelFrom, modelTo);
                add_edge(i, randomVertex, weight);
                connectionsCount++;
            }
        }
        saveGraphLabel(filename);
    }

    public void addVertexWithModel(Integer vertexId, PuntoEncuentro model) {
        vertexModels.put(vertexId, model);
    }

    public JsonArray obtainWeights() throws Exception {
        JsonArray result = new JsonArray();

        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject vertexInfo = new JsonObject();
            PuntoEncuentro model = vertexModels.get(i);
            if (model != null) {
                vertexInfo.addProperty("name", model.getNombre());
            }
            vertexInfo.addProperty("labelId", this.getVertex(i));
            JsonArray destinations = new JsonArray();
            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);

            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject destinationInfo = new JsonObject();
                    destinationInfo.addProperty("from", this.getVertex(i));
                    destinationInfo.addProperty("to", adj.getdestination());
                    destinationInfo.addProperty("weight", adj.getweight());
                    destinations.add(destinationInfo);
                }
            }

            vertexInfo.add("destinations", destinations);
            result.add(vertexInfo);
        }

        return result;
    }

    public boolean existsFile(String filename) {
        File file = new File(filePath + filename);
        return file.exists();
    }

    public static double calcularDistancia(PuntoEncuentro puntoencuentro1, PuntoEncuentro puntoencuentro2) {
        if (puntoencuentro1.getLatitud() == null || puntoencuentro1.getLongitud() == null ||
                puntoencuentro2.getLatitud() == null || puntoencuentro2.getLongitud() == null) {
            return Double.NaN;
        }
        double lat1 = toRadians(puntoencuentro1.getLatitud().doubleValue());
        double lon1 = toRadians(puntoencuentro1.getLongitud().doubleValue());
        double lat2 = toRadians(puntoencuentro2.getLatitud().doubleValue());
        double lon2 = toRadians(puntoencuentro2.getLongitud().doubleValue());

        // Fórmula de Haversine
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double R = 6371000.0;
        double distancia = R * c;

        return Math.round((distancia / 1000) * 100.0) / 100.0;
    }

    // Método principal para guardar el grafo
    public void guardarGrafo() {
        try {
            String filename = "puntoEncuentrografo.json";
            saveGraphLabel(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obtener los datos del grafo en formato Vis.js
    public JsonObject getVisGraphData() throws Exception {
        JsonObject visGraph = new JsonObject();

        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();

        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject node = new JsonObject();
            PuntoEncuentro model = vertexModels.get(i);
            if (model != null) {
                node.addProperty("name", model.getNombre());
            }
            node.addProperty("id", i);
            node.addProperty("label", "V" + i);

            node.addProperty("color", "#EA7913"); // Color de los nodos Naranjas
            nodes.add(node);

            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);
            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject edge = new JsonObject();
                    edge.addProperty("from", i);
                    edge.addProperty("to", adj.getdestination());
                    edge.addProperty("weight", adj.getweight());

                    edge.addProperty("color", "#3B83BD"); // Color de las aristas Azules
                    edges.add(edge);
                }
            }
        }

        // Añadir nodos y aristas al objeto principal
        visGraph.add("nodes", nodes);
        visGraph.add("edges", edges);

        return visGraph;
    }

    protected abstract Float getWeight2(int nodo, int i);

}
