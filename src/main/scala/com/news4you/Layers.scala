package com.news4you

import com.news4you.repository.DoobieTodoRepository
import zio.ZLayer
import zio.blocking.Blocking
import zio.logging.Logging.Logging
import zio.logging.slf4j.Slf4jLogger
import com.news4you.config.AppConfigProvider
import com.news4you.config.ConfigProvider
import com.news4you.config.DbConfigProvider
import com.news4you.repository.DoobieTodoRepository
import com.news4you.repository.TodoRepository

object Layers {

  type Layer0Env =
    ConfigProvider with Logging with Blocking

  type Layer1Env =
    Layer0Env with AppConfigProvider with DbConfigProvider

  type Layer2Env =
    Layer1Env with TodoRepository

  type AppEnv = Layer2Env

  object live {

    val layer0: ZLayer[Blocking, Throwable, Layer0Env] =
      Blocking.any ++ ConfigProvider.live ++ Slf4jLogger.make((_, msg) => msg)

    val layer1: ZLayer[Layer0Env, Throwable, Layer1Env] =
      AppConfigProvider.fromConfig ++ DbConfigProvider.fromConfig ++ ZLayer.identity

    val layer2: ZLayer[Layer1Env, Throwable, Layer2Env] =
      DoobieTodoRepository.layer ++ ZLayer.identity

    val appLayer: ZLayer[Blocking, Throwable, AppEnv] =
      layer0 >>> layer1 >>> layer2
  }
}
