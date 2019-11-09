package quuux.beagle.gui

import javax.swing.text.JTextComponent
import javax.swing.{JTextArea, JTextField}
import java.awt.event.{FocusEvent, FocusListener}
import java.awt.Color


/**
 * Implements a text component that shows a hint text in the background,
 * which gets replace as soon as the user enters text.
 */

trait HintText extends JTextComponent with FocusListener {
  val hint:String
  addFocusListener(this)
  setForeground(Color.gray)

  def clear() = { super.setText(hint); setForeground(Color.gray) }
  override def focusLost(e:FocusEvent) = if(getText.isEmpty) clear()
  override def focusGained(e:FocusEvent) = if(getText.isEmpty) setText("")
  override def setText(text:String) = {super.setText(text); setForeground(Color.black)} 
  override def getText = if(super.getText == hint) "" else super.getText
}

/** JTextField with hint */
class HintTextField(val hint:String) extends JTextField(hint) with HintText

/* JTextArea with hint */
class HintTextArea(val hint:String) extends JTextArea(hint) with HintText 