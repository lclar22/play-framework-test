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

class UserController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateUserForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "carnet" -> number,
      "telefono" -> number,
      "direccion" -> text,
      "sueldo" -> number,
      "type_1" -> text
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def index = Action {
    Ok(views.html.user_index(newForm))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.user_index(errorForm)))
      },
      res => {
        repo.create(res.nombre, res.carnet, res.telefono, res.direccion, res.sueldo, res.type_1).map { _ =>
          Redirect(routes.UserController.index)
        }
      }
    )
  }

  def getUsers = Action.async {
  	repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // Update required
  val updateForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "nombre" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "sueldo" -> number,
      "type_1" -> text
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.user_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> res.toList(0).nombre, "carnet" -> res.toList(0).carnet.toString(), "telefono" -> res.toList(0).telefono.toString(), "direccion" -> res.toList(0).direccion, "sueldo" -> res.toList(0).sueldo.toString(), "type_1" -> res.toList(0).type_1.toString())
      Ok(views.html.user_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.user_index(newForm))
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
        Future.successful(Ok(views.html.user_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.nombre, res.carnet, res.telefono, res.direccion, res.sueldo, res.type_1).map { _ =>
          Redirect(routes.UserController.index)
        }
      }
    )
  }
}

case class CreateUserForm(nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String)

case class UpdateUserForm(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int, type_1: String)