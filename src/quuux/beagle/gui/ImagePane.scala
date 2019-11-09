package quuux.beagle.gui

import scala.math.{min,max}
import java.awt.print.PageFormat
import java.awt.print.Printable
import quuux.scatils.cmd.open

import java.awt.image.BufferedImage
import javax.swing.JPanel
import java.io.File
import javax.imageio.ImageIO
import quuux.beagle.Settings
import java.awt._
import event._
import quuux.scatils.i18n

/**
 * Panel that draws an image loaded from a file.
 */

class ImagePane extends JPanel with Printable {
  import Settings.zoomFactor
  if(Settings.showToolTip) setToolTipText(i18n("showsImage"))
  private var image:BufferedImage = null
  var contentPath:String = "pics/default.gif"

  display()

  private def refresh() = {
    setPreferredSize(new Dimension((image.getWidth*zoomFactor).toInt,(image.getHeight*zoomFactor).toInt))
    revalidate()
    repaint()
  }

  def display(filepath:String = "pics/default.gif") = {
    contentPath = filepath
    def noImage = ImageIO.read(new File("pics/document.png"))
    try {
      image = ImageIO.read(new File(filepath))
      if(image == null) image = noImage
    }
    catch { case _:Throwable => image = noImage }
    refresh()
  }

  def zoom(factor:Double) = { zoomFactor = factor; refresh() }

  override def paintComponent(g:Graphics) {
    super.paintComponent(g)
    setBackground(Color.white)
    val g2D = g.asInstanceOf[Graphics2D]
    val (cw,ch) = (getWidth, getHeight)                  // component dimensions
    val (w,h) = (image.getWidth*zoomFactor, image.getHeight*zoomFactor)  // rescaled image dimensions
    val (x,y) = (max(0,(cw-w)/2), max(0,(ch-h)/2))       // centered or upper left corner
    g2D.translate(x,y)
    g2D.scale(zoomFactor,zoomFactor)
    g2D.drawImage(image, 0,0, this)
  }
  
  def print(g:Graphics, pf:PageFormat, pageIndex:Int):Int = {
    if(pageIndex != 0) return Printable.NO_SUCH_PAGE
    val g2D = g.asInstanceOf[Graphics2D]
    val (iw,ih) = (image.getWidth, image.getHeight)
    val (pw,ph) = (pf.getImageableWidth, pf.getImageableHeight)
    val c = min(pw/iw, ph/ih)                  // scaling factor
    g2D.translate((pw-iw*c)/2, (ph-ih*c)/2)    // center image
    g2D.scale(c,c)                             // scale but keep aspect ratio
    g2D.drawImage(image, 0,0, this)
    return Printable.PAGE_EXISTS
  }
  

  addMouseListener(new MouseListenerTrait {
    override def mouseClicked(e:MouseEvent) = if(contentPath != null) open(contentPath)
  })

  addMouseWheelListener(new MouseWheelListener {
    def mouseWheelMoved(e:MouseWheelEvent):Unit = {
      if(e.getModifiers == 0) return      // some control key needs to be pressed
      val notches = e.getWheelRotation
      if(notches > 0) zoom(Settings.zoomFactor*0.9)
      if(notches < 0) zoom(Settings.zoomFactor*1.1)
    }
  })

}
