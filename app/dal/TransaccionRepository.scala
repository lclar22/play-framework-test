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

  private class TransaccionesTable(tag: Tag) extends Table[Transaccion](tag, "transacciones") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def monto = column[Int]("monto")
    def cuenta = column[Int]("cuenta")
    def cliente = column[Int]("cliente")
    def * = (id, monto, cuenta, cliente) <> ((Transaccion.apply _).tupled, Transaccion.unapply)
  }

  private val transacciones = TableQuery[TransaccionesTable]

  def create(monto: Int, cuenta: Int, cliente: Int): Future[Transaccion] = db.run {
    (transacciones.map(p => (p.monto, p.cuenta, p.cliente))
      returning transacciones.map(_.id)
      into ((nameAge, id) => Transaccion(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (monto, cuenta, cliente)
  }

  def list(): Future[Seq[Transaccion]] = db.run {
    transacciones.result
  }
}
