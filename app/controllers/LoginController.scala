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

import javax.inject._
import it.innove.play.pdf.PdfGenerator

class LoginController @Inject() (repo: VeterinarioRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[LoginForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def index = Action {
    Ok(views.html.login(newForm))
  }

  def logout = Action {
    Ok("Bye").withNewSession
  }

  def login = Action.async { implicit request =>
    println("GOING TO LOGIN");
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.login(errorForm)))
      },
      res => {
        repo.getByLogin(res.user, res.password).map { res2 =>
          if (res2.length > 0) {
            println("LOGGED");
            Ok("Welcome!").withSession("userSecurity" -> res2(0).nombre, "userSecurity2" -> res2(0).nombre)
            if (res2(0).type_1 == "veterinario") {
              Ok(views.html.veterinario_profile2(res2(0)))
            }
            else if (res2(0).type_1 == "insumo") {
              Ok(views.html.insumo_profile2(res2(0)))
            } else if (res2(0).type_1 == "storekeeper") {
              Ok(views.html.storekeeper_profile2(res2(0)))
            } else if (res2(0).type_1 == "account") {
              Ok(views.html.veterinario_profile2(res2(0)))
            } else {
              Ok(views.html.veterinario_profile2(res2(0)))
            }
          } else {
            println("NO logged");
            Ok(views.html.login(newForm))
          }
        }
      }
    )
  }

}

case class LoginForm(user: String, password: String)
