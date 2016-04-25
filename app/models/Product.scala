package models

import play.api.libs.json._

case class Product(id: Long, nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long, currentAmount: Int)

object Product {
  implicit val insumoFormat = Json.format[Product]
}