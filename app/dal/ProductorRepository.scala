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
    def nombre = column[String]("nombre")
    def carnet = column[Int]("carnet")
    def telefono = column[Int]("telefono")
    def direccion = column[String]("direccion")
    def cuenta = column[Long]("cuenta")
    def asociacion = column[Long]("asociacion")
    def * = (id, nombre, carnet, telefono, direccion, cuenta, asociacion) <> ((Productor.apply _).tupled, Productor.unapply)
  }

  private val productores = TableQuery[ProductoresTable]

  def create(nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long): Future[Productor] = db.run {
    (productores.map(p => (p.nombre, p.carnet, p.telefono, p.direccion, p.cuenta, p.asociacion))
      returning productores.map(_.id)
      into ((nameAge, id) => Productor(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6))
    ) += (nombre, carnet, telefono, direccion, cuenta, asociacion)
  }

  def list(): Future[Seq[Productor]] = db.run {
    productores.result
  }

  def getProductorById(id: Long): Future[Seq[Productor]] = db.run {
    productores.filter(_.id === id).result
  }

  // update required
  def update(id: Long, nombre: String): Future[Seq[Productor]] = db.run {
    val q = for { c <- productores if c.id === id } yield c.nombre
    db.run(q.update(nombre))
    productores.filter(_.id === id).result
  }

}
