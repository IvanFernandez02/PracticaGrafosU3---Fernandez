package com.controller.Dao.servicies;

import com.controller.Dao.PuntoEncuentroDao;
import com.controller.tda.list.LinkedList;
import com.models.PuntoEncuentro;

public class PuntoEncuentroServices {
    private PuntoEncuentroDao obj;

    // Constructor
    public PuntoEncuentroServices() {
        obj = new PuntoEncuentroDao();
    }

    // Método para guardar un parque
    public Boolean save() throws Exception {
        return obj.save();
    }

    // Método para actualizar un parque
    public Boolean update() throws Exception {
        return obj.update();
    }

    // Método para listar todos los puntoencuentro
    public LinkedList<PuntoEncuentro> listAll() {
        return obj.getListAll();
    }

    // Método para obtener un parque
    public PuntoEncuentro getPuntoEncuentro() {
        return obj.getPuntoEncuentro();
    }

    // Método para establecer un parque
    public void setPuntoEncuentro(PuntoEncuentro puntoencuentro) {
        obj.setPuntoEncuentro(puntoencuentro);
    }

    // Método para obtener un parque por ID
    public PuntoEncuentro get(Integer id) throws Exception {
        return obj.get(id);
    }

    // Método para eliminar un parque
    public Boolean delete(Integer id) throws Exception {
        return obj.delete(id);
    }

    // Método para calcular el camino corto entre dos puntoencuentro usando el algoritmo seleccionado
    public String calcularCaminoCorto(int inicio, int fin, int algoritmo) throws Exception {
        return obj.caminoCorto(inicio, fin, algoritmo);
    }
}
