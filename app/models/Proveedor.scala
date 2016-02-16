package models

import play.api.libs.json._

case class Proveedor(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Proveedor {
  implicit val proveedorFormat = Json.format[Proveedor]
}