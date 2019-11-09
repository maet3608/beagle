package quuux.beagle.gui

import java.awt.event.{KeyEvent, KeyListener, ActionEvent, ActionListener}
import javax.swing.SwingConstants.CENTER
import javax.swing.text.html.HTMLEditorKit
import javax.swing._
import java.awt.{BorderLayout, FlowLayout, Dimension}
import quuux.beagle.Beagle

/**
 * Swing panel to show about info.
 */

class AboutPanel(beagle:Beagle) extends JPanel(new BorderLayout) {
   val textPane = new JEditorPane
   val kit = new HTMLEditorKit
  
   val styleSheet = kit.getStyleSheet
   styleSheet.addRule("body {font: 12px calibri; color: gray; margin: 10px; text-align: center;}")
   styleSheet.addRule("h1 {font: 25px calibri; color: gray; text-align: center;}")
   styleSheet.addRule("h2 {font: 15px calibri; color: gray; text-align: center;}")

   val info =
   """
   <html>
   <body>
   <p />
   <h1>Beagle</h1>
   <h2>Version %s</h2>
   <p>Stefan Maetschke <br />&copy; 2011</p>
   <p>www.quuux.com</p>
   <p>stefan.maetschke@gmail.com</p>
   </body>
   """ format beagle.Version

   textPane.setEditable(false)
   textPane.setEditorKit(kit)
   textPane.setDocument(kit.createDefaultDocument)
   textPane.setText(info)
   add(textPane, BorderLayout.CENTER)
}