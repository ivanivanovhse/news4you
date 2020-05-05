package com.news4you.http
import com.github.ghik.silencer.silent
import com.news4you.crawler.Crawler
import com.news4you.domain.{PageRequest, _}
import com.news4you.http.HttpClient.HttpClient
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio._

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._

object NewsService {

  final case class PageContent(
    url: String,
    documents:List[Document])

/*  object PageContent {
    implicit val decoderDocument: Decoder[Document] = deriveDecoder
    implicit val encoder: Encoder[PageContent] = deriveEncoder
    implicit val decoder: Decoder[PageContent] = deriveDecoder
  }*/

  @silent("unreachable") // https://github.com/scala/bug/issues/11457
  def routes[R <:HttpClient](rootUri: String): HttpRoutes[RIO[R, ?]] = {
    type News4YouTask[A] = RIO[R, A]
    rootUri.toString
    val dsl: Http4sDsl[News4YouTask] = Http4sDsl[News4YouTask]
    import dsl._

    implicit def circeJsonDecoder[A](
      implicit
      decoder: Decoder[A]
    ): EntityDecoder[News4YouTask, A] =
      jsonOf[News4YouTask, A]
    implicit def circeJsonEncoder[A](
      implicit
      encoder: Encoder[A]
    ): EntityEncoder[News4YouTask, A] =
      jsonEncoderOf[News4YouTask, A]

    HttpRoutes.of[News4YouTask] {

      case GET -> Root / "news" / newsLink =>
        for {
          docs <- Crawler.documents(PageRequest(newsLink))
          response <- Ok(/*PageContent(newsLink,*/docs/*)*/)
        } yield response
    }
  }
}
