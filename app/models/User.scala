package models
import be.objectify.deadbolt.scala.models.Subject

import play.api.libs.json._

case class User(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String) extends Subject {
  override def roles: List[SecurityRole] = List(SecurityRole("foo"), SecurityRole("bar"))

  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = nombre
}

object User {
  implicit val UserFormat = Json.format[User]
}