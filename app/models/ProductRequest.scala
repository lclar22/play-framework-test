package models

import play.api.libs.json._

case class ProductRequest(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)

object ProductRequest {
  implicit val ProductRequestFormat = Json.format[ProductRequest]
}