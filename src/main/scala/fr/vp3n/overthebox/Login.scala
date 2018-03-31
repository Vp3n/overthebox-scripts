package fr.vp3n.overthebox

import com.typesafe.scalalogging.LazyLogging
import fr.vp3n.config.Config
import org.jsoup.Jsoup
import play.api.libs.ws.WSCookie
import play.api.libs.ws.DefaultBodyWritables._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future

case class Auth(cookie: WSCookie, token: String)

class Login(httpClient: StandaloneAhcWSClient,
            config: Config.Overthebox) extends LazyLogging {

  def login(): Future[Option[Auth]] = {
    logger.debug("Initiating login to overthebox.ovh...")
    retrieveCookie().flatMap {
      case Some(cookie) => retrieveToken(cookie).map(_.map(Auth(cookie, _)))
      case None => Future.successful(None)
    }
  }

  private def retrieveCookie(): Future[Option[WSCookie]] = {
    httpClient
      .url(s"http://${config.domain}/cgi-bin/luci/admin/overthebox/overview")
      .withFollowRedirects(false)
      .post(Map(
        "luci_username" -> Seq(config.login),
        "luci_password" -> Seq(config.password)))
      .map { res =>
        if (res.status == 302) {
          logger.debug("login successful")
          res.cookie("sysauth")
        } else if (res.status >= 400 && res.status < 500) {
          logger.error("username or password invalid")
          None
        } else {
          logger.error("login(): server error")
          None
        }
      }
  }

  private def retrieveToken(cookie: WSCookie): Future[Option[String]] = {
    httpClient
      .url(s"http://${config.domain}/cgi-bin/luci/admin/network/network")
      .withCookies(cookie)
      .withFollowRedirects(false)
      .get()
      .map(r => parseTokenFromHtml(r.body))
  }

  private def parseTokenFromHtml(html: String): Option[String] = {
    val doc = Jsoup.parse(html)
    Option(doc.select("input[name=\"token\"]")
      .first())
      .map(_.`val`())
  }
}
