package com.models;

public class PuntoEncuentro {
    private Integer id;
    private String nombre;
    private Double latitud;
    private Double longitud;

    public PuntoEncuentro(Integer id, String nombre, Double longitud, Double latitud) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getid() {
        return this.id;
    }

    public void setid(Integer idPuntoEncuentro) {
        this.id = idPuntoEncuentro;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLongitud() {
        return this.longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return this.latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public PuntoEncuentro() {
    }

}
