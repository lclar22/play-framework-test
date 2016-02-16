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

class ProductorController @Inject() (repo: ProductorRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val productorForm: Form[CreateProductorForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateProductorForm.apply)(CreateProductorForm.unapply)
  }

  def index = Action {
    Ok(views.html.productor_index(productorForm))
  }

  def addProductor = Action.async { implicit request =>
    productorForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productor_index(errorForm)))
      },
      productor => {
        repo.create(productor.monto, productor.cuenta, productor.cliente).map { _ =>
          Redirect(routes.ProductorController.index)
        }
      }
    )
  }

  def getProductores = Action.async {
  	repo.list().map { productores =>
      Ok(Json.toJson(productores))
    }
  }
}

case class CreateProductorForm(monto: Int, cuenta: Int, cliente: Int)
