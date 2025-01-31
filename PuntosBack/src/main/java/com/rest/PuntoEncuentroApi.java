package com.rest;


import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.controller.Dao.servicies.PuntoEncuentroServices;
import com.controller.tda.graph.GraphLabelNoDirect;
import com.controller.tda.list.LinkedList;
import com.models.PuntoEncuentro;
import com.controller.Dao.PuntoEncuentroDao;
@Path("/puntoencuentro")
public class PuntoEncuentroApi {
    
    @Path("/lista")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPuntoEncuentro() {
        HashMap<String, Object> map = new HashMap<>();
        PuntoEncuentroServices ps = new PuntoEncuentroServices();
        map.put("msg", "Lista de Puntos de Encuentro");
        map.put("data", ps.listAll().toArray());
        if (ps.listAll().isEmpty()) {
            map.put("data", new Object[]{});
        }
        return Response.ok(map).build();		
    }


    @Path("/guardar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            PuntoEncuentroServices ps = new PuntoEncuentroServices();
            ps.getPuntoEncuentro().setNombre(map.get("nombre").toString());
            //es un double
            ps.getPuntoEncuentro().setLongitud(Double.parseDouble(map.get("longitud").toString()));
            ps.getPuntoEncuentro().setLatitud(Double.parseDouble(map.get("latitud").toString()));
            ps.save();

            res.put("msg", "Ok");
            res.put("data", "guardado exitoso");
            return Response.ok(res).build();

        } catch (Exception e) {
            res.put("msg", "Error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();

        }
    }

    @Path("/lista/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPuntoEncuentro(@PathParam("id") Integer id) {
        HashMap<String, Object> map = new HashMap<>();
        PuntoEncuentroServices ps = new PuntoEncuentroServices();
        try {
            ps.setPuntoEncuentro(ps.get(id));
        } catch (Exception e) {
            
        }
        map.put("msg", "punto encuentro");
        map.put("data", ps.getPuntoEncuentro());
        if(ps.getPuntoEncuentro().getid() == null){
            map.put("data", "no exiten datos de puntos de encuentro");
            return Response.status(Response.Status.NOT_FOUND).entity(map).build();
        }
        return Response.ok(map).build();
       }

    @Path("/actualizar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            PuntoEncuentroServices ps = new PuntoEncuentroServices();
            ps.getPuntoEncuentro().setid(Integer.parseInt(map.get("id").toString()));
            ps.getPuntoEncuentro().setNombre(map.get("nombre").toString());
            ps.getPuntoEncuentro().setLongitud(Double.parseDouble(map.get("longitud").toString()));
            ps.getPuntoEncuentro().setLatitud(Double.parseDouble(map.get("latitud").toString()));
            ps.update();
            res.put("msg", "Ok");
            res.put("data", "Actualizado exitoso");
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("msg", "Error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
    }


    @Path("/eliminar/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            PuntoEncuentroServices ps = new PuntoEncuentroServices();
            ps.setPuntoEncuentro(ps.get(id));
            ps.delete(id);
            res.put("msg", "Ok");
            res.put("data", "Eliminado exitoso");
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("msg", "Error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
    }

    @Path("/crear_grafo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response grafoVerAdmin() {
        HashMap<String, Object> res = new HashMap<>();
        try {
            PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
            LinkedList<PuntoEncuentro> listaPuntoEncuentro = puntoencuentroDao.getListAll();         
            puntoencuentroDao.creategraph();
            puntoencuentroDao.saveGraph(); 
            res.put("msg", "Grafo generado exitosamente");
            res.put("lista", listaPuntoEncuentro.toArray());
            return Response.ok(res).build();   
        } catch (Exception e) {
            res.put("msg", "Error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
    } 
@Path("/adyacencias_aleatorias")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response unionesgrafos() {
    HashMap<String, Object> res = new HashMap<>();
    try {
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
        GraphLabelNoDirect<String> graph = puntoencuentroDao.crearuniosnes();
        System.out.println(graph.toString());
        puntoencuentroDao.saveGraph();
        res.put("msg", "Grafo actualizado exitosamente");
        res.put("data", graph.toString());
        return Response.ok(res).build();
    } catch (Exception e) {
        res.put("msg", "Error");
        res.put("data", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
    }
}

@Path("/mapadegrafos")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getCompleteGraphData() {
    try {
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
        JsonObject graph = puntoencuentroDao.getGraphData();

        if (graph == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo obtener el grafo, el objeto está vacío").build();
        }

        System.out.println("Contenido: " + graph.getAsJsonObject());
        return Response.ok(graph.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
        // Manejo de errores
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}

@Path("/misgrafos")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getGraph() {
    HashMap<String, Object> res = new HashMap<>();
    try {
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
        JsonArray graph = puntoencuentroDao.obtainWeights();
        res.put("msg", "Grafo obtenido exitosamente");
        return Response.ok(graph.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
        res.put("msg", "Error");
        res.put("data", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
    }
}

@Path("/camino_corto/{inicio}/{fin}/{algoritmo}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response calcularCaminoCorto(@PathParam("inicio") int inicio, @PathParam("destino") int destino, @PathParam("algoritmo") int algoritmo) {
    HashMap<String, Object> res = new HashMap<>();
    try {
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();    
        JsonArray graph = puntoencuentroDao.obtainWeights();   
        String resultado = puntoencuentroDao.caminoCorto(inicio, destino, algoritmo);     
        res.put("msg", "Camino corto calculado exitosamente");
        res.put("resultado", resultado);
        return Response.ok(res).build();
    } catch (Exception e) {
        res.put("msg", "Error");
        res.put("data", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
    }
}

    @Path("/busquedaanchura/{inicio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response busquedaanchura(@PathParam("inicio") int inicio) throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        PuntoEncuentroDao puntoencuentroDao = new PuntoEncuentroDao();
        JsonArray graph = puntoencuentroDao.obtainWeights();
        String respuesta = puntoencuentroDao.busquedaanchura(inicio);


        try {
        res.put("respuesta", respuesta);
        return Response.ok(res).build();          
        } catch (Exception e) {
            res.put("msg", "Error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
    }





}
