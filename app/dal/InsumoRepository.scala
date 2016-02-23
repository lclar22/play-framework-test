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
    def nombre = column[String]("nombre")
    def costo = column[Int]("costo")
    def porcentage = column[Int]("porcentage")
    def descripcion = column[String]("descripcion")
    def unidad = column[Long]("unidad")
    def * = (id, nombre, costo, porcentage, descripcion, unidad) <> ((Insumo.apply _).tupled, Insumo.unapply)
  }

  private val insumos = TableQuery[InsumosTable]

  def create(nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long): Future[Insumo] = db.run {
    (insumos.map(p => (p.nombre, p.costo, p.porcentage, p.descripcion, p.unidad))
      returning insumos.map(_.id)
      into ((nameAge, id) => Insumo(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))
    ) += (nombre, costo, porcentage, descripcion, unidad)
  }

  def list(): Future[Seq[Insumo]] = db.run {
    insumos.result
  }
}
