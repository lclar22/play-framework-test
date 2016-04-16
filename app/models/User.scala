package models

import play.api.libs.json._

case class User(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String)

object User {
  implicit val UserFormat = Json.format[User]
}