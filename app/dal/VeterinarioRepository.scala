package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Veterinario

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class VeterinarioRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class VeterinariosTable(tag: Tag) extends Table[Veterinario](tag, "veterinarios") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nombre = column[String]("nombre")
    def carnet = column[Int]("carnet")
    def telefono = column[Int]("telefono")
    def direccion = column[String]("direccion")
    def sueldo = column[Int]("sueldo")
    def * = (id, nombre, carnet, telefono, direccion, sueldo) <> ((Veterinario.apply _).tupled, Veterinario.unapply)
  }

  private val veterinarios = TableQuery[VeterinariosTable]

  def create(nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int): Future[Veterinario] = db.run {
    (veterinarios.map(p => (p.nombre, p.carnet, p.telefono, p.direccion, p.sueldo))
      returning veterinarios.map(_.id)
      into ((nameAge, id) => Veterinario(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))
    ) += (nombre, carnet, telefono, direccion, sueldo)
  }

  def list(): Future[Seq[Veterinario]] = db.run {
    veterinarios.result
  }
}
