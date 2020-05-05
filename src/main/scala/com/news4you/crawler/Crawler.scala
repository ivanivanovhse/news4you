package com.news4you.crawler
import com.news4you.domain._
import com.news4you.http.HttpClient.{Response, get}
import com.news4you.http.implicits.HttpRequestOps
import io.circe.generic.auto._

object Crawler {
    def documents(request: PageRequest): Response[List[Document]] =
        get[Document]("news",request.parameters)
}
