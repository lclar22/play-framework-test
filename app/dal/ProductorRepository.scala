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
    def asociacion = column[Int]("asociacion")
    def * = (id, nombre, carnet, asociacion) <> ((Productor.apply _).tupled, Productor.unapply)
  }

  private val productores = TableQuery[ProductoresTable]

  def create(nombre: String, carnet: Int, asociacion: Int): Future[Productor] = db.run {
    (productores.map(p => (p.nombre, p.carnet, p.asociacion))
      returning productores.map(_.id)
      into ((nameAge, id) => Productor(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (nombre, carnet, asociacion)
  }

  def list(): Future[Seq[Productor]] = db.run {
    productores.result
  }
}
