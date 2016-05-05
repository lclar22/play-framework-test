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
import it.innove.play.pdf.PdfGenerator

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler


class MainController @Inject() (val messagesApi: MessagesApi, deadbolt: DeadboltActions)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{


  def index = deadbolt.WithAuthRequest()() { authRequest =>
    Future {
            authRequest.session.get("userSecurity").map(user => println(user));
             Ok(views.html.index(new MyDeadboltHandler)(authRequest))
           }
   }

  def index3 = Action { request =>
    request.session.get("userSecurity").map { user =>
      Ok("Hello " + user)
    }.getOrElse {
      Unauthorized("Oops, you are not connected")
    }
  }
  //def index_pdf = Action {
  //	val generator = new PdfGenerator
  //  Ok(generator.toBytes(views.html.index(), "http://localhost:9000/")).as("application/pdf")
  //}
}
