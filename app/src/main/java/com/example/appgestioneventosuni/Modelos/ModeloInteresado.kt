package com.example.appgestioneventosuni.Modelos

class ModeloInteresado {
    var idRegistro: String = ""
    var uidUsuario: String = ""
    var nombreUsuario: String = ""
    var emailUsuario: String = ""
    var idElemento: String = ""
    var nombreElemento: String = ""
    var tipoElemento: String = ""
    var fechaRegistro: String = ""
    var tiempo: Long = 0

    constructor()

    constructor(
        idRegistro: String,
        uidUsuario: String,
        nombreUsuario: String,
        emailUsuario: String,
        idElemento: String,
        nombreElemento: String,
        tipoElemento: String,
        fechaRegistro: String,
        tiempo: Long
    ) {
        this.idRegistro = idRegistro
        this.uidUsuario = uidUsuario
        this.nombreUsuario = nombreUsuario
        this.emailUsuario = emailUsuario
        this.idElemento = idElemento
        this.nombreElemento = nombreElemento
        this.tipoElemento = tipoElemento
        this.fechaRegistro = fechaRegistro
        this.tiempo = tiempo
    }
}
