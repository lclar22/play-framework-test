package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Productor

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductoresTable(tag: Tag) extends Table[Productor](tag, "productores") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def monto = column[Int]("monto")
    def cuenta = column[Int]("cuenta")
    def cliente = column[Int]("cliente")
    def * = (id, monto, cuenta, cliente) <> ((Productor.apply _).tupled, Productor.unapply)
  }

  private val productores = TableQuery[ProductoresTable]

  def create(monto: Int, cuenta: Int, cliente: Int): Future[Productor] = db.run {
    (productores.map(p => (p.monto, p.cuenta, p.cliente))
      returning productores.map(_.id)
      into ((nameAge, id) => Productor(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (monto, cuenta, cliente)
  }

  def list(): Future[Seq[Productor]] = db.run {
    productores.result
  }
}
