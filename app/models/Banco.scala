package models

import play.api.libs.json._

case class Banco(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Banco {
  implicit val bancoFormat = Json.format[Banco]
}