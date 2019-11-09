package quuux.beagle.gui

import java.awt.event.{MouseEvent, MouseListener}

/**
 * Just a simple wrapper that simplifies the implementation of mouse listeners. 
 */
trait MouseListenerTrait extends MouseListener {
  def mouseReleased(e:MouseEvent) = {}
  def mousePressed(e:MouseEvent) = {}
  def mouseEntered(e:MouseEvent) = {}
  def mouseExited(e:MouseEvent) = {}
  def mouseClicked(e:MouseEvent) = {}
}
