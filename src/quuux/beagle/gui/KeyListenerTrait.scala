package quuux.beagle.gui

import java.awt.event.{KeyEvent, KeyListener}

/**
 * Simple wrapper to simplify implementation of KeyListeners
 */
trait KeyListenerTrait extends KeyListener {
  def keyPressed(event:KeyEvent) = {}
  def keyReleased(event:KeyEvent) = {}
  def keyTyped(event:KeyEvent) = {}
}

