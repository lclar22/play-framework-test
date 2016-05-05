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

  def login = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.login(errorForm)))
      },
      res => {
        repo.getByLogin(res.user, res.password).map { res =>
          if (res.length > 0) {
            Ok("Welcome!").withSession("connected" -> "user@gmail.com")
            //request.session("login") = res(0).name
            /*if (res(0).type_1 == "veterinario") {
              Ok(routes.VeterinarioController.profile(res(0).id)).withSession("connected" -> "user@gmail.com")
            } else if (res(0).type_1 == "insumo") {
              Ok(routes.InsumoUserController.profile(res(0).id))
            } else if (res(0).type_1 == "storekeeper") {
              Ok(routes.StorekeeperController.profile(res(0).id))
            } else if (res(0).type_1 == "account") {
              //Redirect(routes.AccountController.profile(res(0).id))
            } */
          } else {
            Ok(views.html.login(newForm))
          }
        }
      }
    )
  }
}

case class LoginForm(user: String, password: String)
