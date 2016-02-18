package models

import play.api.libs.json._

case class Veterinario(id: Long, monto: Int, cuenta: Int, cliente: Int)

object Veterinario {
  implicit val veterinarioFormat = Json.format[Veterinario]
}