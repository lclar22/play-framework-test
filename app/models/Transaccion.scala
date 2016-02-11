package models

import play.api.libs.json._

case class Transaccion(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Transaccion {
  implicit val transaccionFormat = Json.format[Transaccion]
}