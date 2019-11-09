package quuux.beagle

import xml.{XML, Node}

/**
 * Record of a file with its the location, keywords and content.
 * @author Stefan Maetschke
 */

/**
 * Encapsulates the file location, keywords and further text that describes a document/image.
 */
class Record(val id:String, var score:Double, var location:String, var keywords:String) {
  var ngrams = Set[String]()
  update()

  /** performs an update.  Call after record has been modified */
  def update() {
    //ngrams = NGrams(id) ++ NGrams(location) ++ NGrams(keywords)
    ngrams = NGrams(id) ++ NGrams(keywords)
  }

  /** returns the file extension of the document referenced */
  def extension = location.lastIndexOf('.') match {
    case -1 => ""
    case idx:Int => location.substring(idx,location.length)
  }

  /** Returns the filename the referenced document is stored under within the database */
  def docname = id+extension.toUpperCase

  /** returns the string representation */
  override def toString =
    "Record %s:\n%.2f\n%s\n%s" format (id, score, location, keywords)
}

/**
 * Record factory. Mostly serialization.
 */
object Record {

  /** returns an XML representation of the annotation */
  def toXML(record:Record) =
    <Record>
      <Id>{record.id}</Id>
      <Location>{record.location}</Location>
      <Keywords>{record.keywords}</Keywords>
    </Record>

  /** creates a record from its xml representation */
  def fromXML(node:Node) = {
    def text(element:String) = (node \ element).text
    new Record(text("Id"), 0.0, text("Location"), text("Keywords"))
  }
}


/**
 * Manages a sequence of records
 */
class Records extends Seq[Record] {
  var isModified = false
  private var records = Seq[Record]()
  private var ids = 0L   // creates record ids.

  /** adds a record to the sequence */
  def add(location:String, keywords:String) = {
    isModified = true
    val record = new Record("%010d" format ids, 0.0, location, keywords)
    records = records :+ record
    ids += 1
    record
  }

  /** sorts the records according to n-gram similarity to the given text */
  def sort(text:String) {
    val searchNGrams = NGrams(text)
    records.foreach(r => r.score = NGrams.score(searchNGrams, r.ngrams))
    records = records.sortBy(-_.score)
  }

  /** deletes the specified record */
  def delete(index:Int) {
    isModified = true
    val id = records(index).id
    records = records.filterNot( _.id == id)
  }
  
  /** loads records from file in XML format */
  def load(filepath:String) {
    isModified = true
    val xml = XML.loadFile(filepath)
    ids = (xml \\ "IDs").text.toLong
    records = (xml \\ "Record").map(Record.fromXML)
  }

  /** saves records to file in XML format */
  def save(filepath:String) {
    val xml = {
      <Beagle version = "1.0">
      <IDs>{ids}</IDs>
      <Records>{records.map(Record.toXML)}</Records>
      </Beagle>
    }
    XML.save(filepath,xml, "UTF-16")
  }


  // implements Seq interface
  def apply(index:Int) = records(index)
  def length = records.length
  def iterator = records.iterator
}


/**
 * Methods to serialize and de-serialize records from and to XML.
 */
object Records {

  /** just an example */
  def main(args:Array[String]) {
    val records = new Records()
    for(i <- 0 to 5) yield records.add("location.ext"+i,"keys"+i)
    records.save("test.xml")
    records.load("test.xml")
    records.foreach(println)
    println(records(0).extension)
  }
}