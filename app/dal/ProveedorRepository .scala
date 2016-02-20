package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Proveedor

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProveedorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProveedoresTable(tag: Tag) extends Table[Proveedor](tag, "proveedores") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nombre = column[String]("nombre")
    def telefono = column[Int]("telefono")
    def direccion = column[String]("direccion")
    def contacto = column[String]("contacto")
    def cuenta = column[Long]("cuenta")
    def * = (id, nombre, telefono, direccion, contacto, cuenta) <> ((Proveedor.apply _).tupled, Proveedor.unapply)
  }

  private val proveedores = TableQuery[ProveedoresTable]

  def create(nombre: String, telefono: Int, direccion: String, contacto: String, cuenta: Long): Future[Proveedor] = db.run {
    (proveedores.map(p => (p.nombre, p.telefono, p.direccion, p.contacto, p.cuenta))
      returning proveedores.map(_.id)
      into ((nameAge, id) => Proveedor(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))
    ) += (nombre, telefono, direccion, contacto, cuenta)
  }

  def list(): Future[Seq[Proveedor]] = db.run {
    proveedores.result
  }
}
