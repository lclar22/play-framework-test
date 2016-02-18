package models

import play.api.libs.json._

case class Productor(id: Long, nombre: String, carnet: Int, asociacion: Int)

object Productor {
  implicit val productorFormat = Json.format[Productor]	
}