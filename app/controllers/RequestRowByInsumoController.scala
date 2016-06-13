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

class RequestRowByInsumoController @Inject() (repo: RequestRowRepository, repoProductReq: ProductRequestRepository, repoUnit: UnitMeasureRepository,
                                      repoInsum: ProductRepository, repoProductor: ProductorRepository, val messagesApi: MessagesApi)
                                      (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateRequestRowByInsumoForm] = Form {
    mapping(
      "requestId" -> longNumber,
      "productId" -> longNumber,
      "quantity" -> number,
      "status" -> text,
      "unitMeasure" -> longNumber
    )(CreateRequestRowByInsumoForm.apply)(CreateRequestRowByInsumoForm.unapply)
  }

  //val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")
  var productReqsMap = getProductReqsMapMap()
  var productsMap = getproductsMapMap()
  var unidades = getUnitMeasuresMap()
  var productPrice = 0.0
  
  def getProductPrice(id: Long): Double = {
    Await.result(repoInsum.getById(id).map{ case (res1) => 
      res1(0).price
    }, 3000.millis)
  }

  def getProductById(id: Long): Product = {
    Await.result(repoInsum.getById(id).map{ case (res1) => 
      res1(0)
    }, 3000.millis)
  }

  def getUnitMeasureById(id: Long): UnitMeasure = {
    Await.result(repoUnit.getById(id).map{ case (res1) => 
      res1(0)
    }, 3000.millis)
  }

  def getUnitMeasuresMap(): Map[String, String] = {
    Await.result(repoUnit.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def index = Action {
    productReqsMap = getProductReqsMapMap()
    productsMap = getproductsMapMap()
    Ok(views.html.requestRowByInsumo_index(productReqsMap, productsMap))
  }

  def addGet = Action {
    unidades = getUnitMeasuresMap()
    productReqsMap = getProductReqsMapMap()
    productsMap = getproductsMapMap()
    Ok(views.html.requestRowByInsumo_add(newForm, productReqsMap, productsMap, unidades))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowByInsumo_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        var product1 = getProductById(res.productId)
        var productUnitMeasure =  getUnitMeasureById(product1.unitMeasure)
        var requestUnitMeasure = getUnitMeasureById(res.unitMeasure)
        var equivalent =  requestUnitMeasure.quantity.toDouble / productUnitMeasure.quantity.toDouble;

        repo.create(res.requestId, res.productId, productsMap(res.productId.toString), res.quantity, equivalent * product1.price , res.status, res.unitMeasure, res.unitMeasure.toString).map { _ =>
          Redirect(routes.ProductRequestByInsumoController.show(res.requestId))
        }
      }
    )
  }

  def getRequestRowByInsumos = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getRequestRowByInsumosByParent(id: Long) = Action.async {
    repo.listByParent(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateRequestRowByInsumoForm] = Form {
    mapping(
      "id" -> longNumber,
      "requestId" -> longNumber,
      "productId" -> longNumber,
      "quantity" -> number,
      "precio" -> of[Double],
      "status" -> text,
      "unitMeasure" -> longNumber
    )(UpdateRequestRowByInsumoForm.apply)(UpdateRequestRowByInsumoForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.requestRowByInsumo_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    unidades = getUnitMeasuresMap()
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "requestId" -> res.toList(0).requestId.toString(),
                                "productId" -> res.toList(0).productId.toString(),
                                "quantity" -> res.toList(0).quantity.toString(), 
                                "precio" -> res.toList(0).precio.toString(), 
                                "status" -> res.toList(0).status.toString(),
                                "unitMeasure" -> res.toList(0).status.toString()
                        )
      productReqsMap = getProductReqsMapMap()
      productsMap = getproductsMapMap()
      Ok(views.html.requestRowByInsumo_update(updateForm.bind(anyData), productReqsMap, productsMap, unidades))
    }
  }

  def getProductReqsMapMap(): Map[String, String] = {
    Await.result(repoProductReq.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getproductsMapMap(): Map[String, String] = {
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
      Redirect(routes.ProductRequestByInsumoController.show(res(0).requestId))
    }
  }

// update required
  def getSend(id: Long) = Action.async {
    repo.sendById(id).map {case (res) =>
      Redirect(routes.ProductRequestByInsumoController.show(res(0).requestId))
    }
  }

// update required
  def getFinish(id: Long) = Action.async {
    repo.finishById(id).map {case (res) =>
      Redirect(routes.ProductRequestByInsumoController.show(res(0).requestId))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.requestRowByInsumo_index(Map[String, String](), Map[String, String]()))
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
        Future.successful(Ok(views.html.requestRowByInsumo_update(errorForm, Map[String, String](), Map[String, String](), unidades)))
      },
      res => {
        var new_precio = res.precio
        if (res.precio == 0) {
          var product1 = getProductById(res.productId)
          var productUnitMeasure =  getUnitMeasureById(product1.unitMeasure)
          var requestUnitMeasure = getUnitMeasureById(res.unitMeasure)
          var equivalent = requestUnitMeasure.quantity.toDouble / productUnitMeasure.quantity.toDouble;
          new_precio = product1.price * equivalent
        }
        repo.update(res.id, res.requestId, res.productId, productsMap(res.productId.toString), res.quantity, new_precio , res.status, res.unitMeasure, res.unitMeasure.toString).map { _ =>
          Redirect(routes.ProductRequestByInsumoController.show(res.requestId))
        }
      }
    )
  }

}

case class CreateRequestRowByInsumoForm(requestId: Long, productId: Long, quantity: Int, status: String, unitMeasure: Long)

case class UpdateRequestRowByInsumoForm(id: Long, requestId: Long, productId: Long, quantity: Int, precio: Double, status: String, unitMeasure: Long)