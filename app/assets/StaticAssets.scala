package controllers

import play.mvc.Controller

object StaticAssets extends Controller {
  def getUrl(file: String): String = {
    routes.Assets.versioned(file).toString
  }

  def getImg(file: String): String = {
    val cdn: String = null;//"https://d6e24qc6ycr4l.cloudfront.net/"
    if (cdn != null) {
      cdn + file
    }
    else {
      images + file
    }
  }

  var images = "https://{placeholder}/"

}
