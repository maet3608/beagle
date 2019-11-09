package quuux.beagle.gui

import java.awt.print.PrinterJob

import quuux.scatils.cmd.{open, explore}

import javax.swing.SwingConstants.CENTER
import java.awt._
import javax.swing._
import javax.swing.event.{ ListSelectionListener, ListSelectionEvent, ChangeListener,ChangeEvent}
import filechooser.{ FileSystemView, FileFilter }
import java.io.File
import java.awt.event._
import scala.math.min
import quuux.beagle.{ Settings, Beagle, Record }
import quuux.scatils.i18n

/**
 * Swing panel to find and edit annotations
 */

class SearchPanel(beagle: Beagle) extends JPanel(new BorderLayout(5, 5)) {
  val buttonSize = new Dimension(100, 35)
  val buttonFont = new Font("Dialog", Font.PLAIN, 12)
  val searchButton = new SearchButton
  val searchField = new SearchField
  val addButton = new AddButton
  val openButton = new OpenButton
  val printButton = new PrintButton
  val deleteButton = new DeleteButton
  val keywordsField = new KeywordsField
  val imageZoomSlider = new ImageZoomSlider
  val imagePane = new ImagePane
  val imageScrollPane = new ImageScrollPane(imagePane)
  val idLabel = new IDLabel
  val nameLabel = new NameLabel
  val listModel = new ListModel(beagle)

  val buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5))
  buttonPanel.add(addButton)
  buttonPanel.add(openButton)
  buttonPanel.add(printButton)
  buttonPanel.add(deleteButton)

  val searchPanel = new JPanel(new BorderLayout)
  searchPanel.add(searchButton, BorderLayout.LINE_END)
  searchPanel.add(searchField, BorderLayout.CENTER)

  val keywordsScrollPane = new JScrollPane(keywordsField)
  keywordsScrollPane.setMinimumSize(new Dimension(50, 50))

  val contentPanel = new JPanel(new BorderLayout)
  val contentSubPanel = new JPanel(new BorderLayout)
  contentSubPanel.add(idLabel, BorderLayout.PAGE_START)
  contentSubPanel.add(nameLabel, BorderLayout.CENTER)
  contentPanel.add(contentSubPanel, BorderLayout.PAGE_START)
  contentPanel.add(keywordsScrollPane, BorderLayout.CENTER)
  contentPanel.add(buttonPanel, BorderLayout.PAGE_END)

  val imagePanel = new JPanel(new BorderLayout)
  imagePanel.add(imageScrollPane, BorderLayout.CENTER)
  imagePanel.add(imageZoomSlider, BorderLayout.PAGE_END)

  val recordsList = new RecordsList
  val recordsScrollPane = new JScrollPane(recordsList)

  val splitVPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, recordsScrollPane, contentPanel)
  splitVPanel.setOneTouchExpandable(true)
  splitVPanel.setDividerLocation(400)

  val inputPanel = new JPanel(new BorderLayout(0, 0))
  inputPanel.add(searchPanel, BorderLayout.PAGE_START)
  inputPanel.add(splitVPanel, BorderLayout.CENTER)

  val splitHPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, imagePanel)
  splitHPanel.setOneTouchExpandable(true)
  splitHPanel.setDividerLocation(0.3)

  add(splitHPanel, BorderLayout.CENTER)

  updateButtons()

  /** When shutting down ensure that changes are saved. */
  beagle.addWindowListener(new WindowAdapter() {
    override def windowClosing(event: WindowEvent) = {
      val index = recordsList.getSelectedIndex
      if (index >= 0 && index < listModel.getSize) {
        val record = beagle.record(index)
        record.keywords = keywordsField.getText
        record.update()
        beagle.records.isModified = true
        beagle.save()
      }
    }
  })

  /** updates the content of the different panels that show record data */
  def set(record: Record) = {
    idLabel.setText(i18n("documentLabel")+ ": " + record.id)
    nameLabel.setText(record.location)
    keywordsField.setText(record.keywords)
    imagePane.display(beagle.docpath(record))
  }

  /** clears all content fields */
  def clear() = {
    idLabel.setText("")
    keywordsField.setText("")
    nameLabel.setText("")
    imagePane.display()
  }

  /** updates button states */
  def updateButtons() = {
    val n = listModel.getSize
    deleteButton.setEnabled(n > 0 && Settings.enableDelete)
    searchButton.setEnabled(n > 0)
    searchField.setEnabled(n > 0)
    keywordsField.setEnabled(n > 0)
    openButton.setEnabled(n > 0)
  }

  /** searches for the given text and updates the records list and content panels */
  def search(text: String) = {
    beagle.records.sort(text)
    if (beagle.records.size > 0) {
      set(beagle.records(0))
      recordsList.revalidate()
      recordsList.repaint()
      recordsList.requestFocusInWindow()
      recordsList.setSelectedIndex(0)
      recordsList.ensureIndexIsVisible(0)
    }
  }

  def createImageIcon(path: String) = {
    val imgURL = "pics/" + path //SearchPanel.class.getResource(path)
    new ImageIcon(imgURL)
  }

  class SearchButton extends JButton(i18n("searchButtonLabel")) {
    if(Settings.showToolTip) setToolTipText(i18n("searchButtonTip"))
    setPreferredSize(buttonSize)
    setIcon(createImageIcon("search.png"))
    setFont(buttonFont)
    setMnemonic(KeyEvent.VK_S)
    addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) = search(searchField.getText)
    })
  }

  class SearchField extends HintTextField(i18n("searchFieldHint")) {
    if(Settings.showToolTip) setToolTipText(i18n("searchFieldTip"))
    setPreferredSize(new Dimension(150, searchButton.getPreferredSize.height))
    setHorizontalAlignment(CENTER)
    addKeyListener(new KeyListenerTrait() {
      override def keyTyped(event: KeyEvent) = if (event.getKeyChar.toInt == 10) search(getText)
    })
  }

  class RecordsList extends JList(listModel) {
    if(Settings.showToolTip) setToolTipText(i18n("recordsListTip"))
    setFocusable(true)
    setCellRenderer(new CellRenderer)
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    setFixedCellHeight(18)
    setMinimumSize(new Dimension(50, 50)) // otherwise splitPane does not shrink

    getSelectionModel.addListSelectionListener(new ListSelectionListener {
      def valueChanged(e: ListSelectionEvent) = {
        val index = getSelectedIndex
        if (e.getValueIsAdjusting() == false)
          if (index >= 0 && index < listModel.getSize) set(beagle.record(index)) else clear()
      }
    })

    addMouseListener(new MouseAdapter() {
      override def mouseClicked(e: MouseEvent) =
        if (e.getClickCount == 2) {
          val index = recordsList.locationToIndex(e.getPoint)
          if (index >= 0) open(beagle.docpath(index))
        }
    })
  }

  class ListModel(beagle: Beagle) extends AbstractListModel[String] {
    def add(index: Int) { fireIntervalAdded(this, index, index) }
    def remove(index: Int) { fireIntervalAdded(this, index, index + 1) }
    def change(index: Int) { fireContentsChanged(this, index, index + 1) }
    def getSize: Int = beagle.records.size
    def getElementAt(index: Int) = beagle.records(index).keywords
  }

//  public Component getListCellRendererComponent(JList list, Object value,
//      int index, boolean isSelected, boolean cellHasFocus) {

  class CellRenderer extends JLabel with ListCellRenderer[String] {
    private val progress = (0 to 5).map { i => createImageIcon("progress%d.png" format i) }
    private val view = FileSystemView.getFileSystemView

    def getSystemIcon(record: Record) = view.getSystemIcon(new File(beagle.docpath(record)))
    def getProgressIcon(record: Record) = progress((record.score * 5 + 0.5).toInt)

    def getListCellRendererComponent(list:JList[_<:String], value:String, index:Int, isSelected:Boolean, cellHasFocus:Boolean):Component =  {
      val record = beagle.records(index)
      setText(value)
      setIcon(if (searchField.getText.isEmpty) getSystemIcon(record) else getProgressIcon(record: Record))
      if (isSelected) {
        setBackground(list.getSelectionBackground); setForeground(list.getSelectionForeground)
      } else {
        setBackground(list.getBackground); setForeground(list.getForeground)
      }
      setEnabled(list.isEnabled)
      setFont(list.getFont)
      setOpaque(true)
      this
    }
  }

  class IDLabel extends JLabel {
    if(Settings.showToolTip) setToolTipText(i18n("idLabelTip"))
    setHorizontalAlignment(SwingConstants.CENTER)
    setPreferredSize(new Dimension(50, 20))
    //setOpaque(true)
    //setBackground(Color.white)
  }

  class NameLabel extends JLabel {
    if(Settings.showToolTip) setToolTipText(i18n("nameLabelTip"))
    setHorizontalAlignment(SwingConstants.CENTER)
    setPreferredSize(new Dimension(50, 20))
    //setOpaque(true)
    //setBackground(Color.white)
    addMouseListener(new MouseAdapter() {
      override def mouseClicked(e: MouseEvent) =
        if (e.getClickCount == 1) {
          val index = recordsList.locationToIndex(e.getPoint)
          if (index >= 0) explore(beagle.records(index).location, true)
        }
    })
  }

  class KeywordsField extends JTextArea with FocusListener {
    private var index = -1
    if(Settings.showToolTip) setToolTipText(i18n("keywordsFieldTip"))
    setFont(new Font("Dialog", Font.BOLD, 12))
    setMinimumSize(new Dimension(50, 50))
    setLineWrap(true)
    setWrapStyleWord(true)
    addFocusListener(this)

    override def focusGained(e: FocusEvent) =
      { index = recordsList.getSelectedIndex }

    override def focusLost(e: FocusEvent) = {
      if (index >= 0 && index < listModel.getSize) {
        val record = beagle.record(index)
        record.keywords = keywordsField.getText
        record.update()
        beagle.records.isModified = true
        listModel.change(index)
      }
    }
  }

  class OpenButton extends JButton(i18n("openButtonLabel")) {
    if(Settings.showToolTip) setToolTipText(i18n("openButtonTip"))
    setPreferredSize(buttonSize)
    setIcon(createImageIcon("open.png"))
    setFont(buttonFont)
    setMnemonic(KeyEvent.VK_O)
    addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) = open(imagePane.contentPath)
    })
  }

  class PrintButton extends JButton(i18n("printButtonLabel")) {
    if(Settings.showToolTip) setToolTipText(i18n("printButtonTip"))
    setPreferredSize(buttonSize)
    setIcon(createImageIcon("print.png"))
    setFont(buttonFont)
    setMnemonic(KeyEvent.VK_O)
    addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) = {
    	val printJob = PrinterJob.getPrinterJob()
    	printJob.setJobName("Beagle")
        printJob.setCopies(1)
        printJob.setPrintable(imagePane)
    	if(printJob.printDialog())
        (new Thread() { override def run() = printJob.print()} ).start()
      }	
    })
  }

  class DeleteButton extends JButton(i18n("deleteButtonLabel")) {
    if(Settings.showToolTip) setToolTipText(i18n("deleteButtonTip"))
    setPreferredSize(buttonSize)
    setIcon(createImageIcon("delete.png"))
    setFont(buttonFont)
    addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) = {
        var selection = 0
        if(Settings.saveDelete) {
          val options = Array[Object](i18n("delete"), i18n("cancel"))
          selection = JOptionPane.showOptionDialog(beagle, i18n("deleteDialogQuestion"),
             i18n("deleteDialogTitel"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options(0))
        }
        val index = recordsList.getSelectedIndex
        if (index >= 0 && index < listModel.getSize && selection == 0) {
          beagle.delete(index)
          listModel.remove(index)
          recordsList.setSelectedIndex(min(listModel.getSize - 1, index))
          updateButtons()
        }
      }
    })
  }

  class AddButton extends JButton(i18n("addButtonLabel")) {
    if (Settings.showToolTip) setToolTipText(i18n("addButtonTip"))
    setPreferredSize(buttonSize)
    setIcon(createImageIcon("add.png"))
    setFont(buttonFont)
    setMnemonic(KeyEvent.VK_A)
    addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) = addDocuments()
    })
  }

  def addDocuments() = {
    class ImageFilter extends FileFilter {
      val exts = Set(".jpg", ".jpeg", ".gif", ".png")
      def accept(f: File) = f.isDirectory || exts.exists(f.getName.toLowerCase.endsWith)
      def getDescription = "Image (%s)" format exts.mkString(", ")
    }
    val fc = new JFileChooser(Settings.inputFilepath)
    fc.setMultiSelectionEnabled(true)
    fc.addChoosableFileFilter(new ImageFilter)
    fc.setDialogTitle(i18n("addDialogTitle"))
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      Settings.inputFilepath = fc.getSelectedFile.getCanonicalPath
      (new Copier(fc.getSelectedFiles)).start()
    }
  }

  class Copier(files:Seq[File]) extends Thread {
    override def run() = {
      for (file <- files) {
        set(beagle.add(file.getCanonicalPath, file.getName))
        val lastIndex = listModel.getSize
        listModel.add(lastIndex)
        recordsList.setSelectedIndex(lastIndex)
        recordsList.ensureIndexIsVisible(lastIndex)
      }
      updateButtons()
    }
  }

  class ImageZoomSlider extends JSlider(SwingConstants.HORIZONTAL,1,200, (100*Settings.zoomFactor).toInt) {
    if(Settings.showToolTip) setToolTipText(i18n("zoomSliderTip"))
    addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        val source = e.getSource.asInstanceOf[JSlider]
        imagePane.zoom(source.getValue/100.0)
      }
    })
  }

  class ImageScrollPane(imagePane:ImagePane) extends JScrollPane(imagePane)  {
    getHorizontalScrollBar.setUnitIncrement(10)
    getVerticalScrollBar.setUnitIncrement(10)
  }
}

