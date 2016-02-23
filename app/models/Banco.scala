package models

import play.api.libs.json._

case class Banco(id: Long, nombre: String, tipo: String)

object Banco {
  implicit val bancoFormat = Json.format[Banco]
}