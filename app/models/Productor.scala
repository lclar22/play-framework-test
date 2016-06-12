package models

import play.api.libs.json._

case class Productor (
											id: Long, nombre: String, carnet: Int, telefono: Int,
											direccion: String, account: String, module: Long, 
											moduleName: String, asociacionName: String, totalDebt: Double,
											numberPayment: Int, position: String
					 					 )

object Productor {
  implicit val productorFormat = Json.format[Productor]	
}
