package quuux.beagle.gui

import javax.swing.JTabbedPane
import java.awt.event.KeyEvent
import quuux.beagle.Beagle
import quuux.scatils.i18n

/**
 * Manages all the tabs of the application
 */
class TabsPanel(beagle:Beagle) extends JTabbedPane {

  def tabTitle(title:String) =  title

  addTab(tabTitle(i18n("searchTabTitle")), null, beagle.searchPanel, i18n("searchTabTip"))
  setMnemonicAt(0, KeyEvent.VK_1);

  addTab(tabTitle(i18n("settingsTabTitle")), null, beagle.settingsPanel, i18n("settingsTabTip"))
  setMnemonicAt(1, KeyEvent.VK_2);

  addTab(tabTitle(i18n("aboutTabTitle")), null, beagle.aboutPanel, i18n("aboutTabTip"))
  setMnemonicAt(2, KeyEvent.VK_3);

}

