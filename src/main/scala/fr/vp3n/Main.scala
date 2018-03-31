package fr.vp3n

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fr.vp3n.config.Config
import fr.vp3n.config.Config.Action
import fr.vp3n.overthebox.Api
import play.api.libs.ws.ahc.{AhcWSClientConfigFactory, StandaloneAhcWSClient}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


object Main extends App with LazyLogging {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Config.load() match {
    case Failure(e) =>
      logger.error(s"Configuration error", e)
      sys.exit(-1)
    case Success(config) =>
      val httpClient = StandaloneAhcWSClient.apply(AhcWSClientConfigFactory.forConfig())
      val interfaceApi = new overthebox.Interface(httpClient, config.overthebox)
      val loginApi = new overthebox.Login(httpClient, config.overthebox)
      val api = new Api(loginApi, interfaceApi)

      (config.overthebox.action match {
        case Action.Start => api.startInterface(config.overthebox.interface)
        case Action.Stop => api.stopInterface(config.overthebox.interface)
      }).onComplete {
        case Success(_) =>
          logger.info(s"${config.overthebox.action} " +
            s"of ${config.overthebox.interface} successful")
          sys.exit(0)
        case Failure(e) =>
          logger.error(s"Error: ${e.getMessage}")
          sys.exit(-1)
      }
  }



}
