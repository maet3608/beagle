package quuux.beagle


import gui._
import quuux.scatils.io.copy
import quuux.scatils.error.ErrorHandler


import javax.swing.UIManager.{setLookAndFeel, getSystemLookAndFeelClassName}
import java.awt.Toolkit.getDefaultToolkit
import quuux.beagle.Beagle._
import javax.swing.JFrame
import java.awt.event.{WindowAdapter, WindowEvent}
import java.io.File
import java.awt.Dimension
import quuux.scatils.i18n


/**
 * A simple document management system to annotate and find scanned news paper articles.
 * 
 * @author Stefan Maetschke 
 */


class Beagle extends JFrame {
  Thread.setDefaultUncaughtExceptionHandler( new ErrorHandler() { override def onInit(e:Throwable) = save() })

  val Version = "1.02"
  val records = new Records()
  load()  // records

  val searchPanel = new SearchPanel(this)
  val settingsPanel = new SettingsPanel(this)
  val aboutPanel = new AboutPanel(this)
  val tabsPanel = new TabsPanel(this)

  setContentPane(tabsPanel)
  setFrameProperties()

  def defaultSize = {
    val screen = getDefaultToolkit.getScreenSize
    new Dimension((screen.getWidth*0.7).toInt,(screen.getHeight*0.7).toInt)
  }

  def setFrameProperties() {
    setPreferredSize(defaultSize)
    pack()
    setTitle("Beagle")
    setIconImage(getDefaultToolkit.getImage("pics/beagle16.gif"));
    setResizable(true)
    setLocationRelativeTo(null) // center on screen
    setVisible(true)
    addWindowListener(new WindowAdapter() {
      override def windowClosing(event:WindowEvent) {
        save()
        Settings.save()
        System.exit(0)
      }
    })
  }


  /** Adds a record to the database */
  def add(location:String, keywords:String) = {
    val record = records.add(location:String, keywords:String)
    copy(location, docpath(record))
    record
  }

  /** returns the path to the document the record refers to within the database */
  def docpath(index:Int):String = docpath(records(index))
  def docpath(record:Record):String =  DB_DIR+record.docname

  /** Retrieves the record with the given index (not id!) */
  def record(index:Int) = records(index)

  /** Deletes the specified record */
  def delete(index:Int) {
    (new File(docpath(index))).delete()  // remove file in database
    records.delete(index)
  }


  /** loads the records from the database */
  def load() {
	def isMissing(path:String) = !(new File(path)).exists  
	if(isMissing(DB_DIR)) (new File(DB_DIR)).mkdir()
    if(isMissing(DB_PATH)) save() else records.load(DB_PATH)
  }

  /** saves the record to the database */
  def save() {
    if(!records.isModified) records.save(DB_PATH)
    records.isModified = false
  }

}

	


object Beagle {
  val DB_DIR = "database/"
  val DB_PATH = DB_DIR+"database.xml"

  def main(args:Array[String]) {
    Settings.load()
    i18n.setLocale(Settings.locale)
    setLookAndFeel(getSystemLookAndFeelClassName)
    new Beagle
  }
}






