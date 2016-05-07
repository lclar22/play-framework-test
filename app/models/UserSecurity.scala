package models

import play.libs.Scala
import be.objectify.deadbolt.core.models.Subject

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

class UserSecurity(val userName: String) extends Subject
{
  var rol = "veterinario";

  var rolesList: Map[String, List[SecurityRole]] = Map("veterinario" -> List(new SecurityRole("product"), new SecurityRole("user"),
                                                                              new SecurityRole("productor"), new SecurityRole("foo"))
                                                      )

  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(rolesList(rol))
  }

  def getPermissions: java.util.List[UserPermission] = {
    Scala.asJava(List(new UserPermission("printers.edit")))
  }

  def getIdentifier: String = userName
}
