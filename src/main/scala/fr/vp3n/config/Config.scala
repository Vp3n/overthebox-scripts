package fr.vp3n.config

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import configs.Configs._
import configs.syntax._

import scala.util.Try

object Config extends LazyLogging {

   sealed trait Action
   object Action {
     case object Start extends Action
     case object Stop extends Action
   }

    def load(): Try[Config.App] = {
      Try(ConfigFactory.load().get[Config.App]("app").value)
    }

  case class App(overthebox: Config.Overthebox)
  case class Overthebox(domain: String,
                        login: String,
                        password: String,
                        interface: String,
                        action: Action)
}
