package com.example.appgestioneventosuni.Modelos

class ModeloEvento {
    var id: String = ""
    var nombreEvento: String = ""
    var imagenUrl: String = ""
    var descripcion: String = ""
    var fechaHora: String = ""
    var lugar: String = ""
    var uidAutor: String = ""
    var tiempo: Long = 0

    constructor()

    constructor(
        id: String,
        nombreEvento: String,
        imagenUrl: String,
        descripcion: String,
        fechaHora: String,
        lugar: String,
        uidAutor: String,
        tiempo: Long
    ) {
        this.id = id
        this.nombreEvento = nombreEvento
        this.imagenUrl = imagenUrl
        this.descripcion = descripcion
        this.fechaHora = fechaHora
        this.lugar = lugar
        this.uidAutor = uidAutor
        this.tiempo = tiempo
    }
}
