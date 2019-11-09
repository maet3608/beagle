package quuux.beagle.gui

import java.awt._
import javax.swing._

import quuux.beagle.{ Settings, Beagle }
import quuux.scatils.i18n
import java.awt.event._


/**
 * Swing panel for application settings
 */
class SettingsPanel(beagle: Beagle) extends JPanel(new BorderLayout) {
  val panel = new JPanel(new SettingsLayout)
  val vPanel = new JPanel(new BorderLayout)
  val hPanel = new JPanel(new BorderLayout)
  val showToolTipLabel = new JLabel(i18n("setShowToolTips"))
  val showToolTipCombo = new ShowToolTipCombo
  val localeLabel = new JLabel("* "+i18n("setLanguage"))
  val localeCombo = new LocaleCombo
  val enableDeleteLabel = new JLabel(i18n("setEnableDelete"))
  val enableDeleteCombo = new EnableDeleteCombo
  val saveDeleteLabel = new JLabel(i18n("setSaveDelete"))
  val saveDeleteCombo = new SaveDeleteCombo
  val restartLabel = new RestartLabel


  panel.add(showToolTipLabel);  panel.add(showToolTipCombo)
  panel.add(enableDeleteLabel); panel.add(enableDeleteCombo)
  panel.add(saveDeleteLabel);   panel.add(saveDeleteCombo)
  panel.add(localeLabel);       panel.add(localeCombo)
  panel.add(restartLabel)
  panel.setBorder(BorderFactory.createEmptyBorder(15,10,15,10))

  vPanel.add(panel, BorderLayout.LINE_START)
  hPanel.add(vPanel, BorderLayout.PAGE_START)
  add(hPanel, BorderLayout.CENTER)

  class SettingsLayout extends GridLayout(0,2) {
    setVgap(10)
    setHgap(10)
  }

  class RestartLabel extends JLabel {
    setText("* "+i18n("requiresRestart"))
    setForeground(Color.gray)
  }

  class ShowToolTipCombo extends JComboBox[String] {
    addItem(i18n("yes"))
    addItem(i18n("no"))
    setSelectedIndex(if(Settings.showToolTip) 0 else 1)
    addActionListener( new ActionListener  {
      def actionPerformed(e:ActionEvent) {
        val cb = e.getSource.asInstanceOf[JComboBox[String]]
        Settings.showToolTip = if(cb.getSelectedIndex==0) true else false
      }
    })
  }

  class LocaleCombo extends JComboBox[String] {
    val locales = Array((i18n("default"),""), (i18n("english"),"en_US"), (i18n("german"),"de_DE"))
    for((name, locale) <-locales) addItem(name)
    setSelectedIndex(locales.indexWhere(l => l._2 == Settings.locale))
    addActionListener( new ActionListener  {
      def actionPerformed(e:ActionEvent) {
        val cb = e.getSource.asInstanceOf[JComboBox[String]]
        Settings.locale = locales(cb.getSelectedIndex)._2
        i18n.setLocale(Settings.locale)
      }
    })
  }

  class EnableDeleteCombo extends JComboBox[String] {
    addItem(i18n("yes"))
    addItem(i18n("no"))
    setSelectedIndex(if(Settings.enableDelete) 0 else 1)
    addActionListener( new ActionListener  {
      def actionPerformed(e:ActionEvent) {
        val cb = e.getSource.asInstanceOf[JComboBox[String]]
        Settings.enableDelete = if(cb.getSelectedIndex==0) true else false
        beagle.searchPanel.updateButtons()
      }
    })
  }

  class SaveDeleteCombo extends JComboBox[String] {
    addItem(i18n("yes"))
    addItem(i18n("no"))
    setSelectedIndex(if(Settings.saveDelete) 0 else 1)
    addActionListener( new ActionListener  {
      def actionPerformed(e:ActionEvent) {
        val cb = e.getSource.asInstanceOf[JComboBox[String]]
        Settings.saveDelete = if(cb.getSelectedIndex==0) true else false
      }
    })
  }
}

