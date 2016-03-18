package models

import play.api.libs.json._

case class RequestModel(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)

object RequestModel {
  implicit val RequestModelFormat = Json.format[RequestModel]
}