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

class BancoController @Inject() (repo: BancoRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val bancoForm: Form[CreateBancoForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateBancoForm.apply)(CreateBancoForm.unapply)
  }

  def index = Action {
    Ok(views.html.banco_index(bancoForm))
  }

  def addTransaccion = Action.async { implicit request =>
    bancoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.banco_index(errorForm)))
      },
      banco => {
        repo.create(banco.monto, banco.cuenta, banco.cliente).map { _ =>
          Redirect(routes.BancoController.index)
        }
      }
    )
  }

  def getTransacciones = Action.async {
  	repo.list().map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }
}

case class CreateBancoForm(monto: Int, cuenta: Int, cliente: Int)
