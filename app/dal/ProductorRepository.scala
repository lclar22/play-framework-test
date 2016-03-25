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

  private val tableQ = TableQuery[ProductoresTable]

  def create(nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long): Future[Productor] = db.run {
    (tableQ.map(p => (p.nombre, p.carnet, p.telefono, p.direccion, p.cuenta, p.asociacion))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Productor(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6))
    ) += (nombre, carnet, telefono, direccion, cuenta, asociacion)
  }

  def list(): Future[Seq[Productor]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Productor]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long): Future[Seq[Productor]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.nombre
    db.run(q.update(nombre))
    val q2 = for { c <- tableQ if c.id === id } yield c.carnet
    db.run(q2.update(carnet))
    val q3 = for { c <- tableQ if c.id === id } yield c.telefono
    db.run(q3.update(telefono))
    val q4 = for { c <- tableQ if c.id === id } yield c.cuenta
    db.run(q4.update(cuenta))
    val q5 = for { c <- tableQ if c.id === id } yield c.asociacion
    db.run(q5.update(asociacion))

    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Productor]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }


  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id < 10L).map(s => (s.id, s.nombre)).result
  }

}
