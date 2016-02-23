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

class VeterinarioController @Inject() (repo: VeterinarioRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val veterinarioForm: Form[CreateVeterinarioForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "carnet" -> number,
      "telefono" -> number,
      "direccion" -> text,
      "sueldo" -> number
    )(CreateVeterinarioForm.apply)(CreateVeterinarioForm.unapply)
  }

  def index = Action {
    Ok(views.html.veterinario_index(veterinarioForm))
  }

  def addVeterinario = Action.async { implicit request =>
    veterinarioForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.veterinario_index(errorForm)))
      },
      veterinario => {
        repo.create(veterinario.nombre, veterinario.carnet, veterinario.telefono, veterinario.direccion, veterinario.sueldo).map { _ =>
          Redirect(routes.VeterinarioController.index)
        }
      }
    )
  }

  def getVeterinarios = Action.async {
  	repo.list().map { veterinarios =>
      Ok(Json.toJson(veterinarios))
    }
  }
}

case class CreateVeterinarioForm(nombre: String, carnet: Int, telefono: Int, direccion: String, sueldo: Int)
