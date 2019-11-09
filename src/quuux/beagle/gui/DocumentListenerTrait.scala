package quuux.beagle.gui

import javax.swing.event.{DocumentEvent, DocumentListener}

/**
 * Wrapper that simplifies usage of a DocumentListener
 */
trait DocumentListenerTrait extends DocumentListener {
  def insertUpdate(e:DocumentEvent)  = {}
  def removeUpdate(e:DocumentEvent)  = {}
  def changedUpdate(e:DocumentEvent) = {} 
}
