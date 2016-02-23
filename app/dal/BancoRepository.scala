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

  def list(): Future[Seq[Banco]] = db.run {
    bancos.result
  }
}
