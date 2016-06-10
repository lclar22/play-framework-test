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
import play.api.data.format.Formats._ 

class ProductController @Inject() (repo: ProductRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateProductForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "cost" -> of[Double],
      "percent" -> of[Double],
      "price" -> of[Double],
      "descripcion" -> text,
      "unidad" -> longNumber,
      "currentAmount" -> number
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    Ok(views.html.product_index(newForm))
  }

  def list = Action {
    Ok(views.html.product_list())
  }

  def addProduct = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.product_index(errorForm)))
      },
      res => {
        repo.create(res.nombre, res.cost, res.percent, res.price, res.descripcion, res.unidad, res.currentAmount).map { _ =>
          Redirect(routes.ProductController.list)
        }
      }
    )
  }

  def getProducts = Action.async {
  	repo.list().map { insumos =>
      Ok(Json.toJson(insumos))
    }
  }

  // update required
  val updateForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "nombre" -> nonEmptyText,
      "cost" -> of[Double],
      "percent" -> of[Double],
      "price" -> of[Double],
      "descripcion" -> text,
      "unidad" -> longNumber,
      "currentAmount" -> number
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.product_show(id))
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> res.toList(0).nombre, "cost" -> res.toList(0).cost.toString(),
        "percent" -> res.toList(0).percent.toString(), "price" -> res.toList(0).price.toString(), "descripcion" -> res.toList(0).descripcion,
        "unidad" -> res.toList(0).unidad.toString(), "currentAmount" -> res.toList(0).currentAmount.toString())
      Ok(views.html.product_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.product_index(newForm))
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
        Future.successful(Ok(views.html.product_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.nombre, res.cost, res.percent, res.price, res.descripcion, res.unidad, res.currentAmount).map { _ =>
          Redirect(routes.ProductController.index)
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
      val path_1 = "/home/llll/Desktop/projects/isystem/public/images/"
      try { 

        new File(s"$path_1$fileNewName").delete()
      } catch {
        case e: Exception => println(e)
      }
      picture.ref.moveTo(new File(s"$path_1$fileNewName"))
      Ok(views.html.product_show(id))
    }.getOrElse {
      Redirect(routes.ProductController.show(id)).flashing(
        "error" -> "Missing file")
    }
  }

}

case class CreateProductForm(nombre: String, cost: Double, percent: Double, price: Double, descripcion: String, unidad: Long, currentAmount: Int)

case class UpdateProductForm(id: Long, nombre: String, cost: Double, percent: Double, price: Double, descripcion: String, unidad: Long, currentAmount: Int)