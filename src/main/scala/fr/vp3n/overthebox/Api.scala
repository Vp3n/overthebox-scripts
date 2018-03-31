package fr.vp3n.overthebox

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Api(loginApi: Login,
          interfaceApi: Interface) {

  def stopInterface(name: String): Future[Unit] = {
    withCredentials(auth => interfaceApi.stop(auth, name))
  }

  def startInterface(name: String): Future[Unit] = {
    withCredentials(auth => interfaceApi.start(auth, name))
  }

  private def withCredentials[T](f: Auth => Future[T]): Future[T] = {
    loginApi.login().flatMap {
      case Some(auth) => f(auth)
      case None => Future.failed(
        new IllegalStateException("Operation error, login failed")
      )
    }
  }
}
