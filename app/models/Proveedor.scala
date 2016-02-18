package models

import play.api.libs.json._

case class Proveedor(id: Long, nombre: String, cuenta: String)

object Proveedor {
  implicit val proveedorFormat = Json.format[Proveedor]	
}