package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Transaccion

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class TransaccionRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class TransaccionesTable(tag: Tag) extends Table[Transaccion](tag, "transaccion") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fecha = column[String]("fecha")
    def descripcion = column[String]("descripcion")
    def * = (id, fecha, descripcion) <> ((Transaccion.apply _).tupled, Transaccion.unapply)
  }

  private val transacciones = TableQuery[TransaccionesTable]

  def create(fecha: String, descripcion: String): Future[Transaccion] = db.run {
    (transacciones.map(p => (p.fecha, p.descripcion))
      returning transacciones.map(_.id)
      into ((nameAge, id) => Transaccion(id, nameAge._1, nameAge._2))
    ) += (fecha, descripcion)
  }

  def list(): Future[Seq[Transaccion]] = db.run {
    transacciones.result
  }
}
