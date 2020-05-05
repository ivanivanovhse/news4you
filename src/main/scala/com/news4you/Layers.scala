package com.news4you

import com.news4you.config.{AppConfigProvider, ConfigProvider}
import com.news4you.http.HttpClient
import com.news4you.http.HttpClient.{ClientTask, HttpClient}
import org.http4s.client.blaze.BlazeClientBuilder
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger
import zio.{Task, ZIO, ZLayer}

import scala.concurrent.ExecutionContext.Implicits

object Layers {

    //type SystemEnv = Blocking with Clock with Console
    type ConfigurationEnv = ConfigProvider with Logging with Clock with Blocking
    type AppConfigurationEnv =ConfigurationEnv with AppConfigProvider with  ClientTask
    /*type Layer1Env =
        Layer0Env with AppConfigProvider with ClientTask *//*with DbConfigProvider*/

    /*type Layer2Env =
      Layer1Env with ClientTask*/


    type News4YouEnv =   AppConfigurationEnv with HttpClient

    object live {
        /* val loggerLayer: ZLayer[Console with Clock, Nothing, Logging] = Logging.console(
           format = (_, logEntry) => logEntry,
           rootLoggerName = Some("news4you")
         )
     */
        private def makeHttpClient =
            ZIO.runtime[Any].map { implicit rts =>
                BlazeClientBuilder
                    .apply[Task](Implicits.global)
                    .resource
                    .toManaged
                /*.toManaged
                .toLayer.orDie*/


                /*.toManaged*/
                /*  .toLayer.orDie*/
            }


        val configurationEnv: ZLayer[Blocking, Throwable, ConfigurationEnv] =
            Blocking.any ++Clock.live ++ ConfigProvider.live ++ Slf4jLogger.make((_, msg) => msg) /*++ Slf4jLogger.make((_, msg) => msg)*/

        val appConfigurationEnv: ZLayer[ConfigurationEnv, Throwable, AppConfigurationEnv] =
            AppConfigProvider.fromConfig /*++ DbConfigProvider.fromConfig*/ ++ ZLayer.fromManaged(makeHttpClient.toManaged_.flatten) ++ ZLayer.identity

        /*val layer2: ZLayer[Layer1Env, Throwable, Layer2Env] =

          ZLayer.fromManaged(makeHttpClient.toManaged_.flatten)++ ZLayer.identity*/

        /* val layer2: ZLayer[Layer1Env, Throwable, HttpClient] =
             layer1 >>> HttpClient.http4s*/
        /*ZLayer.fromServices[Logger[String], Client[Task], Service] { (logger, http4sClient) =>
          Http4s(logger, http4sClient)
        } ++ ZLayer.identity*//*++ ZLayer.identity*/
        //  HttpClient.http4s ++ ZLayer.identity

        val appLayer: ZLayer[Blocking, Throwable, News4YouEnv] =
            configurationEnv >>> appConfigurationEnv >>> HttpClient.http4s ++ ZLayer.identity
            //(layer0 >>> layer1 >>> HttpClient.http4s) ++ ZLayer.identity[Blocking] /*layer2*/
        /*ZLayer.identity[Layer0Env] ++*/
        /*layer2 *//*>>> layer3*/
        /*http4sClientLayer*/
    }

}
