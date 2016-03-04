package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Banco

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class BancoRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class BancosTable(tag: Tag) extends Table[Banco](tag, "bancos") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nombre = column[String]("nombre")
    def tipo = column[String]("tipo")
    def * = (id, nombre, tipo) <> ((Banco.apply _).tupled, Banco.unapply)
  }

  private val bancos = TableQuery[BancosTable]

  def create(nombre: String, tipo: String): Future[Banco] = db.run {
    (bancos.map(p => (p.nombre, p.tipo))
      returning bancos.map(_.id)
      into ((nameAge, id) => Banco(id, nameAge._1, nameAge._2))
    ) += (nombre, tipo)
  }

  // update required
  def update(id: Long, nombre: String, tipo: String): Future[Seq[Banco]] = db.run {
    val q = for { c <- bancos if c.id === id } yield c.nombre
    db.run(q.update(nombre))

    var q2 = (bancos.filter(_.id === id).map(_.tipo)).update((tipo))
    db.run(q2)
    
    bancos.filter(_.id === id).result
  }

  def list(): Future[Seq[Banco]] = db.run {
    bancos.result
  }

  def getNameListByFirst10(): Future[Seq[String]] = db.run {
    bancos.filter(_.id < 10L).map(s => (s.nombre)).result 
  }

  def getNameListByName(nombre: String): Future[Seq[String]] = db.run {
    bancos.filter(_.nombre === nombre).map(_.nombre).result
  }

  def getCuentaListByName(nombre: String): Future[Seq[Banco]] = db.run {
    bancos.filter(_.nombre === nombre).result 
  }

  def getCuentaById(id: Long): Future[Seq[Banco]] = db.run {
    bancos.filter(_.id === id).result
  }

  def updateCuentaName(id: Long, new_nombre: String) {
    val q = for { c <- bancos if c.id === id } yield c.nombre
    db.run(q.update(new_nombre))
  }

  def getCuentaById_1(id: Long): Future[Seq[Banco]] = db.run {
    bancos.filter(_.id === id).result
  }
  
  def listNames(): Future[Seq[String]] = db.run {
    bancos.map(_.nombre).result
  }

}
