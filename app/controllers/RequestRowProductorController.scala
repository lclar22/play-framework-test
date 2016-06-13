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
import play.api.data.format.Formats._ 

import javax.inject._

class RequestRowProductorController @Inject() (repo: RequestRowProductorRepository, repoProductReq: RequestRowRepository, 
                                               repoInsum: ProductRepository, repoProductor: ProductorRepository,
                                               val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateRequestRowProductorForm] = Form {
    mapping(
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "productorId" -> longNumber,
      "quantity" -> number,
      "precio" -> of[Double],
      "status" -> text
    )(CreateRequestRowProductorForm.apply)(CreateRequestRowProductorForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")
  var productReqNames = getProductReqNamesMap()
  var insumoNames = getInsumoNamesMap()
  var productorNames = getProductorNamesMap()

  def index = Action {
    productReqNames = getProductReqNamesMap()
    insumoNames = getInsumoNamesMap()
    Ok(views.html.requestRowProductor_index(productReqNames, insumoNames))
  }

  def addGet = Action {
    productReqNames = getProductReqNamesMap()
    insumoNames = getInsumoNamesMap()
    productorNames = getProductorNamesMap()
    Ok(views.html.requestRowProductor_add(newForm, productReqNames, insumoNames, productorNames))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowProductor_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.create(  
                      res.requestRowId, res.productId, insumoNames(res.productId.toString()),
                      res.productorId, productorNames(res.productorId.toString()),
                      res.quantity, res.precio, res.status
                    ).map { _ =>
          repoProductor.updateTotalDebt(res.productorId, res.precio);
          Redirect(routes.RequestRowController.show(res.requestRowId))
        }
      }
    )
  }

  def getRequestRowProductors = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateRequestRowProductorForm] = Form {
    mapping(
      "id" -> longNumber,
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "productorId" -> longNumber,
      "quantity" -> number,
      "precio" -> of[Double],
      "status" -> text
    )(UpdateRequestRowProductorForm.apply)(UpdateRequestRowProductorForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.requestRowProductor_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "requestRowId" -> res.toList(0).requestRowId.toString(),
                                "productId" -> res.toList(0).productId.toString(), "productorId" -> res.toList(0).productorId.toString(),
                                "quantity" -> res.toList(0).quantity.toString(), "precio" -> res.toList(0).precio.toString(), "status" -> res.toList(0).status.toString())
      productReqNames = getProductReqNamesMap()
      insumoNames = getInsumoNamesMap()
      productorNames = getProductorNamesMap()
      Ok(views.html.requestRowProductor_update(updateForm.bind(anyData), productReqNames, insumoNames, productorNames))
    }
  }

  def getProductReqNamesMap(): Map[String, String] = {
    Await.result(repoProductReq.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: Long) => 
        cache put (key.toString(), value.toString())
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
      Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

// update required
  def getSend(id: Long) = Action.async {
    repo.sendById(id).map {case (res) =>
      Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

// update required
  def getFinish(id: Long) = Action.async {
    repo.finishById(id).map {case (res) =>
      Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.requestRowProductor_index(Map[String, String](), Map[String, String]()))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // to copy
  def requestRowProductorsByProductor(id: Long) = Action.async {
    repo.requestRowProductorsByProductor(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowProductor_update(errorForm, Map[String, String](), Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.requestRowId, res.productId, insumoNames(res.productId.toString), res.productorId, productorNames(res.productorId.toString), res.quantity, res.precio, res.status).map { _ =>
          Redirect(routes.RequestRowController.show(res.requestRowId))
        }
      }
    )
  }

}

case class CreateRequestRowProductorForm(requestRowId: Long, productId: Long, productorId: Long, quantity: Int, precio: Double, status: String)

case class UpdateRequestRowProductorForm(id: Long, requestRowId: Long, productId: Long, productorId: Long, quantity: Int, precio: Double, status: String)