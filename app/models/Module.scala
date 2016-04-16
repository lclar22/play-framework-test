package models

import play.api.libs.json._

case class Module(id: Long, name: String, president: Long, description: String)

object Module {
  implicit val ModuleFormat = Json.format[Module]
}
