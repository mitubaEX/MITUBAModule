package com.github.mituba.module

import com.github.mituba.entity.{ClassInformation, SearchResult, SearchResultFileld}
import dispatch._
import dispatch.Defaults._

/**
  * Created by mituba on 2017/08/27.
  */
class SearchModule(host: String, port: Int, core: String, searchResultFileld: SearchResultFileld) {
  def search(classInformation: ClassInformation, algorithm: String, rows: Int): List[SearchResult] = {
    val birthmarkData = classInformation.data
    val postParam: String =
      s"""
           {
            "params":{
              "q":"${birthmarkData.replace(" ", "+").replace(".", "-")}",
              "rows":"${rows}",
              "wt":"csv"
            }
           }
        """

    val postUrl: String = s"http://${host}:${port}/solr/${core}/query"

    val requestHandler = url(postUrl).POST
      .addQueryParameter("fl",
        s"""${searchResultFileld.className},
           |lev:strdist(data,"${birthmarkData}",${algorithm}),
           |${searchResultFileld.filePath},
           |${searchResultFileld.kindOfBirthmark},
           |${searchResultFileld.birthmarkData}""".stripMargin)
      .setContentType("application/json", "UTF-8")  << postParam

    val http = Http(requestHandler OK as.String)
    http().split("\n").map(_.split(",", 5))
      .map(n => new SearchResult(classInformation, new ClassInformation(filename=n(0), place=n(2), birthmark=n(3), data=n(4)), n(1))).filterNot(_.sim.contains("lev"))
      .sortWith(_.sim.toDouble > _.sim.toDouble)
      .toList
  }

}
