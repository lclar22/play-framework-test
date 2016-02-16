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
    def monto = column[Int]("monto")
    def cuenta = column[Int]("cuenta")
    def cliente = column[Int]("cliente")
    def * = (id, monto, cuenta, cliente) <> ((Proveedor.apply _).tupled, Proveedor.unapply)
  }

  private val proveedores = TableQuery[ProveedoresTable]

  def create(monto: Int, cuenta: Int, cliente: Int): Future[Proveedor] = db.run {
    (proveedores.map(p => (p.monto, p.cuenta, p.cliente))
      returning proveedores.map(_.id)
      into ((nameAge, id) => Proveedor(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (monto, cuenta, cliente)
  }

  def list(): Future[Seq[Proveedor]] = db.run {
    proveedores.result
  }
}
