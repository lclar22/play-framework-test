package models

import play.api.libs.json._

case class Insumo(id: Long, nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long)

object Insumo {
  implicit val insumoFormat = Json.format[Insumo]
}