package ch.fhnw.ima.bimgur

import ch.fhnw.ima.bimgur.App.Location.AnalysesLocation
import ch.fhnw.ima.bimgur.component.AnalysesPage
import ch.fhnw.ima.bimgur.controller.BimgurController.BimgurCircuit
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import org.scalajs.dom.document

import scala.scalajs.js

object App extends js.JSApp {

  val baseUrl = BaseUrl(dom.window.location.href.takeWhile(_ != '#'))

  // all supported URL hashes
  sealed abstract class Location(val link: String)

  object Location {

    object AnalysesLocation extends Location("analyses")

    def values = List[Location](AnalysesLocation)
  }

  // configures how URLs map to components
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location].buildConfig { dsl =>
    import dsl._

    val analysesConnection = BimgurCircuit.connect(_.analyses)

    def filterRoute(s: Location): Rule = staticRoute("#/" + s.link, s) ~> renderR(ctl => {
      s match {
        case AnalysesLocation => analysesConnection(AnalysesPage(_))
      }
    })

    val filterRoutes: Rule = Location.values.map(filterRoute).reduce(_ | _)

    filterRoutes.notFound(redirectToPage(Location.AnalysesLocation)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(ctl: RouterCtl[Location], r: Resolution[Location]) = {

    def activeIf(loc: Location) = if (r.page == loc) "active" else ""
    val activeIfUsers = activeIf(AnalysesLocation)

    <.div(^.className := "container",
      <.ul(^.className := "nav nav-tabs",
        <.li(^.role := "presentation", ^.className := activeIfUsers, <.a(^.href := "#/analyses", "Analyses"))
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  val router: ReactComponentU[Unit, Resolution[Location], Any, TopNode] =
    Router(baseUrl, routerConfig.logToConsole)()

  def main() = {
    ReactDOM.render(router, document.getElementById("root"))
  }

}
