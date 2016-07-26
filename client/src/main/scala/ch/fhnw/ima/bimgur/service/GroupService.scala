package ch.fhnw.ima.bimgur.service

import ch.fhnw.ima.bimgur.model.{Group, GroupList}
import org.scalajs.dom.ext.Ajax
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

case class GroupService(activitiRestUrl: String) {

  val url = activitiRestUrl + "/identity/groups"

  def getGroups: Future[Seq[Group]] = {
    val requestFuture = Ajax.get(url)
    requestFuture.map(response => read[GroupList](response.responseText).data)
  }

  def getRandomGroups: Future[Seq[Group]] = {
    val r = new scala.util.Random()
    val groups = 0 to r.nextInt(10) map (i => Group("Group " + i))
    Future.successful(groups)
  }

}