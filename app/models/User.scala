package models
import be.objectify.deadbolt.core.models.Subject
import play.libs.Scala

import play.api.libs.json._

case class User(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String, login: String, password: String) extends Subject {
  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(List(new SecurityRole("foo"),
                      new SecurityRole("bar")))
  }

  def getPermissions: java.util.List[UserPermission] = {
    Scala.asJava(List(new UserPermission("printers.edit")))
  }

  def getIdentifier: String = nombre
}

object User {
  implicit val UserFormat = Json.format[User]
}