package models

import play.api.libs.json._

case class Transaction(id: Long, date: String, type_1: String, description: String)

object Transaction {
  implicit val transactionFormat = Json.format[Transaction]
}