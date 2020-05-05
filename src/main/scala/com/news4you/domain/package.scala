package com.news4you

package object domain {
    case class PageRequest(pageLink: String, childNumber: Int = 5)
    case class Document(content: String)
}
