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
    def date = column[String]("date")
    def descripcion = column[String]("descripcion")
    def * = (id, date, descripcion) <> ((Transaccion.apply _).tupled, Transaccion.unapply)
  }

  private val transacciones = TableQuery[TransaccionesTable]

  def create(date: String, descripcion: String): Future[Transaccion] = db.run {
    (transacciones.map(p => (p.date, p.descripcion))
      returning transacciones.map(_.id)
      into ((nameAge, id) => Transaccion(id, nameAge._1, nameAge._2))
    ) += (date, descripcion)
  }

  def list(): Future[Seq[Transaccion]] = db.run {
    transacciones.result
  }
}
