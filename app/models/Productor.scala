package models

import play.api.libs.json._

case class Productor(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long)

object Productor {
  implicit val productorFormat = Json.format[Productor]	
}
