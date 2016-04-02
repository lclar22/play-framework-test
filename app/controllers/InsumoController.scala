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
      "unidad" -> longNumber,
      "currentAmount" -> number
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
      res => {
        repo.create(res.nombre, res.costo, res.porcentage, res.descripcion, res.unidad, res.currentAmount).map { _ =>
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
      "unidad" -> longNumber,
      "currentAmount" -> number
    )(UpdateInsumoForm.apply)(UpdateInsumoForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.insumo_show(id))
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> res.toList(0).nombre, "costo" -> res.toList(0).costo.toString(), "porcentage" -> res.toList(0).porcentage.toString(), "descripcion" -> res.toList(0).descripcion, "unidad" -> res.toList(0).unidad.toString(), "currentAmount" -> res.toList(0).currentAmount.toString())
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
        repo.update(res.id, res.nombre, res.costo, res.porcentage, res.descripcion, res.unidad, res.currentAmount).map { _ =>
          Redirect(routes.InsumoController.index)
        }
      }
    )
  }

  def upload(id: Long) = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename;
      val type1 = filename.substring(filename.length - 4);
      val contentType = picture.contentType
      val fileNewName = id.toString() + "_product" + type1
      val path_1 = "C:/Users/Luis Arce/scala/play-scala-intro/public/images/"
      try { 

        new File(s"$path_1$fileNewName").delete()
      } catch {
        case e: Exception => println(e)
      }
      picture.ref.moveTo(new File(s"$path_1$fileNewName"))
      Ok(views.html.insumo_show(id))
    }.getOrElse {
      Redirect(routes.InsumoController.show(id)).flashing(
        "error" -> "Missing file")
    }
  }

}

case class CreateInsumoForm(nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long, currentAmount: Int)

case class UpdateInsumoForm(id: Long, nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long, currentAmount: Int)