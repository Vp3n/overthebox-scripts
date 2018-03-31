package fr.vp3n.overthebox

import com.typesafe.scalalogging.LazyLogging
import fr.vp3n.config.Config
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.DefaultBodyWritables._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Interface(
                 httpClient: StandaloneAhcWSClient,
                 config: Config.Overthebox) extends LazyLogging {

  def start(auth: Auth, name: String): Future[Unit] = {
    logger.debug(s"Trying to start interface $name...")
    httpClient
      .url(s"http://${config.domain}/cgi-bin/luci/admin/network/iface_reconnect/$name")
      .withCookies(auth.cookie)
      .post(Map("token" -> Seq(auth.token)))
      .map { res =>
        if (res.status == 200) {
          logger.debug(s"interface $name successfully started")
        } else {
          throw new IllegalStateException(s"Error while trying to start interface $name" +
            s" please verify the interface name")
        }
      }
  }


  def stop(auth: Auth, name: String): Future[Unit] = {
    logger.debug(s"Trying to stop interface $name...")
    httpClient
      .url(s"http://${config.domain}/cgi-bin/luci/admin/network/iface_shutdown/$name")
      .withCookies(auth.cookie)
      .post(Map("token" -> Seq(auth.token)))
      .map { res =>
        if (res.status == 200) {
          logger.debug(s"interface $name successfully stopped")
        } else {
          throw new IllegalStateException(s"Error while trying to stop interface $name" +
            s" please verify the interface name")
        }
      }

  }
}
