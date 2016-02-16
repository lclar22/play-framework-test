package models

import play.api.libs.json._

case class Productor(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Productor {
  implicit val productorFormat = Json.format[Productor]
}