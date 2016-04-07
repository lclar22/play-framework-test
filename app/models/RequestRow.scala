package models

import play.api.libs.json._

case class RequestRow(id: Long, requestId: Long, productId: Long, productorId: Long, quantity: Int, cost: Int, paid: Int, status: String)

object RequestRow {
  implicit val RequestRowFormat = Json.format[RequestRow]
}