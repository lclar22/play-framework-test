package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Veterinario

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class VeterinarioRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class VeterinariosTable(tag: Tag) extends Table[Veterinario](tag, "veterinarios") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def monto = column[Int]("monto")
    def cuenta = column[Int]("cuenta")
    def cliente = column[Int]("cliente")
    def * = (id, monto, cuenta, cliente) <> ((Veterinario.apply _).tupled, Veterinario.unapply)
  }

  private val veterinarios = TableQuery[VeterinariosTable]

  def create(monto: Int, cuenta: Int, cliente: Int): Future[Veterinario] = db.run {
    (veterinarios.map(p => (p.monto, p.cuenta, p.cliente))
      returning veterinarios.map(_.id)
      into ((nameAge, id) => Veterinario(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (monto, cuenta, cliente)
  }

  def list(): Future[Seq[Veterinario]] = db.run {
    veterinarios.result
  }
}
