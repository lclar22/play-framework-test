package models

import play.api.libs.json._

case class Productor(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, account: Long, asociacion: Long, totalDebt: Long, numberPayment: Int, position: String)

object Productor {
  implicit val productorFormat = Json.format[Productor]	
}
