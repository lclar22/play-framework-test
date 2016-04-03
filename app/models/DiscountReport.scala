package models

import play.api.libs.json._

case class DiscountReport(id: Long, startDate: String, endDate: String, status: String, total: Int)

object DiscountReport {
  implicit val DiscountReportFormat = Json.format[DiscountReport]
}