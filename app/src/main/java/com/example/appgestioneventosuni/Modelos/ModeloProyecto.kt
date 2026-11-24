package com.example.appgestioneventosuni.Modelos

class ModeloProyecto {
    var id: String = ""
    var nombreProyecto: String = ""
    var categoria: String = ""
    var descripcion: String = ""
    var responsables: String = ""
    var fechaPublicacion: String = ""
    var uidAutor: String = ""
    var tiempo: Long = 0

    constructor()

    constructor(
        id: String,
        nombreProyecto: String,
        categoria: String,
        descripcion: String,
        responsables: String,
        fechaPublicacion: String,
        uidAutor: String,
        tiempo: Long
    ) {
        this.id = id
        this.nombreProyecto = nombreProyecto
        this.categoria = categoria
        this.descripcion = descripcion
        this.responsables = responsables
        this.fechaPublicacion = fechaPublicacion
        this.uidAutor = uidAutor
        this.tiempo = tiempo
    }
}
