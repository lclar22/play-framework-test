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

  val insumoForm: Form[CreateInsumoForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateInsumoForm.apply)(CreateInsumoForm.unapply)
  }

  def index = Action {
    Ok(views.html.insumo_index(insumoForm))
  }

  def addInsumo = Action.async { implicit request =>
    insumoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.insumo_index(errorForm)))
      },
      transaccion => {
        repo.create(transaccion.monto, transaccion.cuenta, transaccion.cliente).map { _ =>
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
}

case class CreateInsumoForm(monto: Int, cuenta: Int, cliente: Int)
