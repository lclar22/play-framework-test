package models

import play.api.libs.json._

case class Insumo(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Insumo {
  implicit val insumoFormat = Json.format[Insumo]
}