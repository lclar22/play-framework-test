package models

import be.objectify.deadbolt.scala.models.Subject

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */
class UserSecurity(val userName: String) extends Subject {
	var roles_var: List[SecurityRole] = List[SecurityRole]()
	override def roles: List[SecurityRole] = {
		if (identifier == "admin") {
			return List(SecurityRole("admin"))
		}
		return List(SecurityRole("foo"), SecurityRole("bar"))
	}

	override def permissions: List[UserPermission] = 
	List(UserPermission("printers.edit"))

	override def identifier: String = userName
}
