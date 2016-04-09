package controllers

import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.collection.mutable.ListBuffer
import java.util.LinkedHashMap
import collection.mutable
import scala.collection.mutable.ArrayBuffer

import javax.inject._

class RequestRowController @Inject() (repo: RequestRowRepository, repoProductReq: ProductRequestRepository, repoInsum: InsumoRepository, repoProductor: ProductorRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateRequestRowForm] = Form {
    mapping(
      "requestId" -> longNumber,
      "productId" -> longNumber,
      "productorId" -> longNumber,
      "quantity" -> number,
      "precio" -> number,
      "status" -> text
    )(CreateRequestRowForm.apply)(CreateRequestRowForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    val productReqNames = getProductReqNamesMap()
    val insumoNames = getInsumoNamesMap()
    Ok(views.html.requestRow_index(productReqNames, insumoNames))
  }

  def addGet = Action {
    val productReqNames = getProductReqNamesMap()
    val insumoNames = getInsumoNamesMap()
    val productorNames = getProductorNamesMap()
    Ok(views.html.requestRow_add(newForm, productReqNames, insumoNames, productorNames))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRow_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.create(res.requestId, res.productId, res.productorId, res.quantity, res.precio, res.status).map { _ =>
          Redirect(routes.ProductRequestController.show(1L))
        }
      }
    )
  }

  def getRequestRows = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateRequestRowForm] = Form {
    mapping(
      "id" -> longNumber,
      "requestId" -> longNumber,
      "productId" -> longNumber,
      "productorId" -> longNumber,
      "quantity" -> number,
      "precio" -> number,
      "status" -> text
    )(UpdateRequestRowForm.apply)(UpdateRequestRowForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.requestRow_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "requestId" -> res.toList(0).requestId.toString(),
                                "productId" -> res.toList(0).productId.toString(), "productorId" -> res.toList(0).productorId.toString(),
                                "quantity" -> res.toList(0).quantity.toString(), "precio" -> res.toList(0).precio.toString(), "status" -> res.toList(0).status.toString())
      val productReqNames = getProductReqNamesMap()
      val insumoNames = getInsumoNamesMap()
      val productorNames = getProductorNamesMap()
      Ok(views.html.requestRow_update(updateForm.bind(anyData), productReqNames, insumoNames, productorNames))
    }
  }

  def getProductReqNamesMap(): Map[String, String] = {
    Await.result(repoProductReq.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getInsumoNamesMap(): Map[String, String] = {
    Await.result(repoInsum.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getProductorNamesMap(): Map[String, String] = {
    Await.result(repoProductor.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  // update required
  def getAccept(id: Long) = Action.async {
    repo.acceptById(id).map {case (res) =>
      repoInsum.updateAmount(res(0).productId, - res(0).quantity);
      Redirect(routes.VeterinarioController.profile(1L))
    }
  }

// update required
  def getSend(id: Long) = Action.async {
    repo.sendById(id).map {case (res) =>
      Redirect(routes.VeterinarioController.profile(1L))
    }
  }

// update required
  def getFinish(id: Long) = Action.async {
    repo.finishById(id).map {case (res) =>
      Redirect(routes.VeterinarioController.profile(1L))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.requestRow_index(Map[String, String](), Map[String, String]()))
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
        Future.successful(Ok(views.html.requestRow_update(errorForm, Map[String, String](), Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.requestId, res.productId, res.productorId, res.quantity, res.precio, res.status).map { _ =>
          Redirect(routes.RequestRowController.index)
        }
      }
    )
  }

}

case class CreateRequestRowForm(requestId: Long, productId: Long, productorId: Long, quantity: Int, precio: Int, status: String)

case class UpdateRequestRowForm(id: Long, requestId: Long, productId: Long, productorId: Long, quantity: Int, precio: Int, status: String)