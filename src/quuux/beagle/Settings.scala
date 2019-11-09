package quuux.beagle

import xml.XML
import java.io.File


/**
 * Application settings.
 */
object Settings {
  val defaultPath = "Beagle.properties"

  // global settings
  var showToolTip:Boolean     = false
  var zoomFactor:Double       = 1.0
  var locale:String           = ""
  var enableDelete:Boolean    = true
  var saveDelete:Boolean      = true
  var inputFilepath:String    = "."


  /** saves settings to a file in XML format */
  def save(filepath:String = defaultPath) {
    val xml = {
      <Beagle version = "1.0">
        <showToolTip>{showToolTip}</showToolTip>
        <zoomFactor>{zoomFactor}</zoomFactor>
        <locale>{locale}</locale>
        <enableDelete>{enableDelete}</enableDelete>
        <saveDelete>{saveDelete}</saveDelete>
        <inputFilepath>{inputFilepath}</inputFilepath>
      </Beagle>
    }
    XML.save(filepath,xml, "UTF-16")
  }

  /** loads settings from a file in XML format */
  def load(filepath:String = defaultPath) {
    if(!(new File(defaultPath)).exists) save(filepath)
    val xml = XML.loadFile(filepath)
    def read(name:String):String = (xml \\ name).text

    showToolTip = read("showToolTip").toBoolean
    zoomFactor = read("zoomFactor").toDouble
    locale = read("locale").toString
    enableDelete = read("enableDelete").toBoolean
    saveDelete = read("saveDelete").toBoolean
    inputFilepath = read("inputFilepath").toString
  }


  /** Usage example */
  def main(args:Array[String]) {
    Settings.zoomFactor = 0.7
    Settings.save()
    Settings.load()
    println(Settings.showToolTip)
    println(Settings.zoomFactor)
    println(Settings.locale)
  }
}