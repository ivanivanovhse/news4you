package com.news4you

import cats.effect._
import com.news4you.config._
import com.news4you.http.NewsService
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio._
import zio.clock.Clock
import zio.interop.catz._
import org.http4s.implicits._

object Server extends App {
  type AppTask[A] = RIO[Layers.News4YouEnv with Clock, A]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val prog =
      for {
        cfg    <- ZIO.access[ConfigProvider](_.get)
        _      <- logging.log.info(s"Starting with $cfg")
        appCfg = cfg.appConfig

        httpApp = Router[AppTask](
          "/todos" -> NewsService.routes(s"${appCfg.baseUrl}/todos")
        ).orNotFound

        _ <- runHttp(httpApp,appCfg.port)
      } yield 0

    prog
      .provideLayer(Layers.live.appLayer)
      .orDie
  }

  def runHttp[R <: Clock](
    httpApp: HttpApp[RIO[R, *]],
    port: Int
  ): ZIO[R, Throwable, Unit] = {
    type Task[A] = RIO[R, A]
    ZIO.runtime[R].flatMap { implicit rts =>
      BlazeServerBuilder[Task]
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(CORS(httpApp))
        .serve
        .compile[Task, Task, ExitCode]
        .drain
    }
  }
}
