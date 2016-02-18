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
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
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
        repo.create(veterinario.monto, veterinario.cuenta, veterinario.cliente).map { _ =>
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

case class CreateVeterinarioForm(monto: Int, cuenta: Int, cliente: Int)
