package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.User

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class StorekeeperRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class VeterinariosTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nombre = column[String]("nombre")
    def carnet = column[Int]("carnet")
    def telefono = column[Int]("telefono")
    def direccion = column[String]("direccion")
    def sueldo = column[Int]("sueldo")
    def type_1 = column[String]("type")
    def * = (id, nombre, carnet, telefono, direccion, sueldo, type_1) <> ((User.apply _).tupled, User.unapply)
  }

  private val tableQ = TableQuery[VeterinariosTable]

  def create(nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int): Future[User] = db.run {
    (tableQ.map(p => (p.nombre, p.carnet, p.telefono, p.direccion, p.sueldo, p.type_1))
      returning tableQ.map(_.id)
      into ((nameAge, id) => User(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6))
    ) += (nombre, carnet, telefono, direccion, sueldo, "storekeeper")
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.type_1 === "storekeeper").map(s => (s.id, s.nombre)).result
  }

  def list(): Future[Seq[User]] = db.run {
    tableQ.filter(_.type_1 === "storekeeper").result
  }

  // to cpy
  def getById(id: Long): Future[Seq[User]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int): Future[Seq[User]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.nombre
    db.run(q.update(nombre))
    val q2 = for { c <- tableQ if c.id === id } yield c.carnet
    db.run(q2.update(carnet))
    val q3 = for { c <- tableQ if c.id === id } yield c.telefono
    db.run(q3.update(telefono))
    val q4 = for { c <- tableQ if c.id === id } yield c.sueldo
    db.run(q4.update(sueldo))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[User]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }
}
