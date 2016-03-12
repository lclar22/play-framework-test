package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class InsumoController @Inject() (repo: InsumoRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateInsumoForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "costo" -> number,
      "porcentage" -> number,
      "descripcion" -> text,
      "unidad" -> longNumber
    )(CreateInsumoForm.apply)(CreateInsumoForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    Ok(views.html.insumo_index(newForm))
  }

  def addInsumo = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.insumo_index(errorForm)))
      },
      insumo => {
        repo.create(insumo.nombre, insumo.costo, insumo.porcentage, insumo.descripcion, insumo.unidad).map { _ =>
          Redirect(routes.InsumoController.index)
        }
      }
    )
  }

  def getInsumos = Action.async {
  	repo.list().map { insumos =>
      Ok(Json.toJson(insumos))
    }
  }

  // update required
  val updateForm: Form[UpdateInsumoForm] = Form {
    mapping(
      "id" -> longNumber,
      "nombre" -> nonEmptyText,
      "costo" -> number,
      "porcentage" -> number,
      "descripcion" -> text,
      "unidad" -> longNumber
    )(UpdateInsumoForm.apply)(UpdateInsumoForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.insumo_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> res.toList(0).nombre, "costo" -> res.toList(0).costo.toString(), "porcentage" -> res.toList(0).porcentage.toString(), "descripcion" -> res.toList(0).descripcion, "unidad" -> res.toList(0).unidad.toString())
      Ok(views.html.insumo_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.insumo_index(newForm))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.insumo_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.nombre, res.costo, res.porcentage, res.descripcion, res.unidad).map { _ =>
          Redirect(routes.InsumoController.index)
        }
      }
    )
  }

}

case class CreateInsumoForm(nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long)

case class UpdateInsumoForm(id: Long, nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long)