package models

import play.api.libs.json._

case class Veterinario(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String)

object Veterinario {
  implicit val veterinarioFormat = Json.format[Veterinario]
}