package i2b2.st.system.process;

import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExtractTextFiles {
	static String inputXmlFiles = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\xmlFiles";
	static String outputTextFolder = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\textFiles";
	public static void main(String[] args) throws Exception {
		File inputXmlFileFolder = new File(inputXmlFiles);
		File [] xmlFiles = inputXmlFileFolder.listFiles();
		File outTextFolder = new File(outputTextFolder);
		for (File file : xmlFiles) {
			//read xml file
			getText(file);
			//get text
			//write to text file
		}

	}
	private static void getText(File file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = null;
	    db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        NodeList nList = doc.getElementsByTagName("TEXT");
		Node nNode = nList.item(0);
		Element eElement = (Element) nNode;
         System.out.println("writing File"+file.getName());
         File outFile = new File(outputTextFolder,file.getName().split("\\.")[0]+".txt");
         FileWriter fw = new FileWriter(outFile);
         fw.write(eElement.getTextContent());
         fw.close();
		
	}

}
