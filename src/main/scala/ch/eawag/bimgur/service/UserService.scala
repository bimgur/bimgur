package ch.eawag.bimgur.service

import ch.eawag.bimgur.model.{User, UserList}
import org.scalajs.dom.ext.Ajax
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

case class UserService(activitiRestUrl: String) {

  val url = activitiRestUrl + "/identity/users"

  def getUsers: Future[Seq[User]] = {
    val requestFuture = Ajax.get(url)
    requestFuture.onFailure { case error =>
      println("Loading users failed: " + error)
    }
    requestFuture.map(response => read[UserList](response.responseText).data)
  }

}