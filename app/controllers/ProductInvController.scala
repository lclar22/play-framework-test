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

class ProductInvController @Inject() (repo: ProductInvRepository,repo2: InsumoRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateProductInvForm] = Form {
    mapping(
      "productId" -> longNumber,
      "proveedorId" -> longNumber,
      "amount" -> number,
      "amountLeft" -> number
    )(CreateProductInvForm.apply)(CreateProductInvForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    Ok(views.html.productInv_index(newForm))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productInv_index(errorForm)))
      },
      res => {
        repo.create(res.productId, res.proveedorId, res.amount, res.amountLeft).map { _ =>
          Redirect(routes.ProductInvController.index)
        }
      }
    )
  }

  // update required
  val updateForm: Form[UpdateProductInvForm] = Form {
    mapping(
      "id" -> longNumber,
      "productId" -> longNumber,
      "proveedorId" -> longNumber,
      "amount" -> number,
      "amountLeft" -> number
    )(UpdateProductInvForm.apply)(UpdateProductInvForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {

    Ok(views.html.productInv_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo2.getListNames().onComplete { res1 =>
      repo.getById(id).map { res =>
        val anyData = Map("id" -> id.toString().toString(), "productId" -> res.toList(0).productId.toString(), "proveedorId" -> res.toList(0).proveedorId.toString(), "amount" -> res.toList(0).amount.toString(), "amountLeft" -> res.toList(0).amountLeft.toString())
        Ok(views.html.productInv_update(updateForm.bind(anyData), res1))
      }
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.productInv_index(newForm))
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
        Future.successful(Ok(views.html.productInv_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.productId, res.proveedorId, res.amount, res.amountLeft).map { _ =>
          Redirect(routes.ProductInvController.index)
        }
      }
    )
  }

}

case class CreateProductInvForm(productId: Long, proveedorId: Long, amount: Int, amountLeft: Int)

case class UpdateProductInvForm(id: Long, productId: Long, proveedorId: Long, amount: Int, amountLeft: Int)