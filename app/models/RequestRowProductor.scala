package models

import play.api.libs.json._
/**
	This class is to manage the relation of the product with the productor
*/
case class RequestRowProductor(id: Long, requestRowId: Long, productId: Long, productorId: Long, quantity: Int, precio: Double, paid: Double, status: String)

object RequestRowProductor {
  implicit val RequestRowProductorFormat = Json.format[RequestRowProductor]
}