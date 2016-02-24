package models

import play.api.libs.json._

case class Transaccion(id: Long, fecha: String, descripcion: String)

object Transaccion {
  implicit val transaccionFormat = Json.format[Transaccion]
}