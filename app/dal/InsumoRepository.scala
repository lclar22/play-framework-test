package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Insumo

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class InsumoRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class InsumosTable(tag: Tag) extends Table[Insumo](tag, "insumos") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def monto = column[Int]("monto")
    def cuenta = column[Int]("cuenta")
    def cliente = column[Int]("cliente")
    def * = (id, monto, cuenta, cliente) <> ((Insumo.apply _).tupled, Insumo.unapply)
  }

  private val insumos = TableQuery[InsumosTable]

  def create(monto: Int, cuenta: Int, cliente: Int): Future[Insumo] = db.run {
    (insumos.map(p => (p.monto, p.cuenta, p.cliente))
      returning insumos.map(_.id)
      into ((nameAge, id) => Insumo(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (monto, cuenta, cliente)
  }

  def list(): Future[Seq[Insumo]] = db.run {
    insumos.result
  }
}
