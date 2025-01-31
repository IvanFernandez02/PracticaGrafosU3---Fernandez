package com.controller.Dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.controller.Dao.implement.AdapterDao;
import com.controller.tda.graph.GraphLabelNoDirect;
import com.controller.tda.graph.algoritmos.BusquedaAnchura;
import com.controller.tda.graph.algoritmos.BellmanFord;
import com.controller.tda.graph.algoritmos.Floyd;
import com.controller.tda.list.LinkedList;
import com.models.PuntoEncuentro;

public class PuntoEncuentroDao extends AdapterDao<PuntoEncuentro> {

    private PuntoEncuentro puntoencuentro;
    private LinkedList<PuntoEncuentro> listAll;
    private GraphLabelNoDirect<String> graph;
    private LinkedList<String> vertexName;
    private String name = "PuntoEncuentrografo.json";

    public GraphLabelNoDirect<String> creategraph() {
        if (vertexName == null) {
            vertexName = new LinkedList<>();
        }
        LinkedList<PuntoEncuentro> list = this.getListAll();
        if (!list.isEmpty()) {
            if (graph == null) {
                System.out.println("Grafo " + graph);
                graph = new GraphLabelNoDirect<>(list.getSize(), String.class);
            }
            
            PuntoEncuentro[] puntoencuentro = list.toArray();
            for (int i = 0; i < puntoencuentro.length; i++) {
                this.graph.labelsVertices(i + 1, puntoencuentro[i].getNombre());
                System.out.println("Vertices " + vertexName);

                vertexName.add(puntoencuentro[i].getNombre());
            }
            this.graph.drawGraph();
        }
        System.out.println("Grafo creado " + graph);
        return this.graph;
    }

    // Guardar el grafo en un archivo
    public void saveGraph() throws Exception {
        this.graph.saveGraphLabel(name);
    }
    
   
    public JsonArray obtainWeights() throws Exception {
        if (graph == null) {
            creategraph();
        }
    
        if (graph.existsFile(name)) {
            graph.cargarModelosDesdeDao();
            graph.loadGraph(name);
    
            JsonArray graphData = graph.obtainWeights();
            System.out.println("Modelo de vis,js " + graphData);
            return graphData; 
        } else {
            throw new Exception("El archivo de grafo no existe.");
        }
    }
    
    public JsonObject getGraphData() throws Exception {
        if (graph == null) {
            creategraph();
        }
    
        if (graph.existsFile(name)) {
            graph.cargarModelosDesdeDao();
            graph.loadGraph(name);
    
            JsonObject graphData = graph.getVisGraphData(); 
            System.out.println("Modelo de vis,js " + graphData);
            return graphData; 
        } else {
            throw new Exception("El archivo de grafo no existe.");
        }
    }
    
    public GraphLabelNoDirect<String> crearuniosnes() throws Exception {
        if (graph == null) {
            creategraph();
        }
        if (graph.existsFile(name)) {
            graph.cargarModelosDesdeDao();
            graph.loadGraphWithRandomEdges(name);
            System.out.println("Modelo asociado al grafo: " + name);
        } else {
            throw new Exception("El archivo de grafo no existe.");
        }
        saveGraph();
        return graph;
    }

    public String busquedaanchura(int inicio) throws Exception {
    if (graph == null) {
        throw new Exception("El grafo no existe");
    }

    BusquedaAnchura busquedaanchuraAlgoritmo = new BusquedaAnchura(graph, inicio);

    String recorrido = busquedaanchuraAlgoritmo.recorrerGrafo();
    return recorrido;
}

    public String caminoCorto(int inicio, int fin, int algoritmo) throws Exception {
    if (graph == null) {
        throw new Exception("Grafo no existe");
    }

    System.out.println("Calculando camino corto desde " + inicio + " hasta " + fin);

    String camino = "";

    if (algoritmo == 1) { 
        Floyd floydWarshall = new Floyd(graph, inicio, fin);
        camino = floydWarshall.caminoCorto(); 
    } else { 
        BellmanFord bellmanFord = new BellmanFord(graph, inicio, fin);
        camino = bellmanFord.caminoCorto(); 
    }

    System.out.println("Camino corto calculado: " + camino);	
    return camino;

}

    
    public PuntoEncuentroDao() {
        super(PuntoEncuentro.class);
    }

    public PuntoEncuentro getPuntoEncuentro() {
        if (puntoencuentro == null) {
            puntoencuentro = new PuntoEncuentro();
        }
        return this.puntoencuentro;
    }

    public void setPuntoEncuentro(PuntoEncuentro puntoencuentro) {
        this.puntoencuentro = puntoencuentro;
    }

    public LinkedList<PuntoEncuentro> getListAll() {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        return this.listAll;
    }

    public Boolean save() throws Exception {
        Integer id = getListAll().getSize() + 1;
        getPuntoEncuentro().setid(id);
        persist(getPuntoEncuentro());
        return true;
    }

    public Boolean update() throws Exception {
        this.merge(getPuntoEncuentro(), getPuntoEncuentro().getid() - 1);
        this.listAll = listAll();
        return true;
    }

    public Boolean delete(Integer id) throws Exception {
        for (int i = 0; i < getListAll().getSize(); i++) {
            PuntoEncuentro pro = getListAll().get(i);
            if (pro.getid().equals(id)) {
                getListAll().delete(i);
                return true;
            }
        }
        throw new Exception("Proyecto no encontrado con ID: " + id);
    }
}
