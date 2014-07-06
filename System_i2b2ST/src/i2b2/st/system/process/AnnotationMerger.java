package i2b2.st.system.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AnnotationMerger {
	/**
	CAD
	Diabetes
	Hyperlipidemia
	Hypertension
	Medication
	Obese
	**/
	static String FH = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\FH";
	static String SH = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\SH";
	static String Dia = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\diabetes";
	static String HTN = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\hypertension";
	static String HL = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\hyperlipidemia";
	static String MED = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\medications";
	static String OBE = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\obesity";
	static String fileName;
	static File [] textFiles = new File("").listFiles();
	static File [] FHfile = new File(FH).listFiles();//
	static File [] SHfile = new File(SH).listFiles();//
	static File [] Diafile = new File(Dia).listFiles();//
	static File [] HTNfiile = new File(HTN).listFiles();//
	static File [] MEDfile = new File(MED).listFiles();//
	static File [] OBEfile = new File(OBE).listFiles();//
	static File [] HLfile = new File(HL).listFiles();
	static  File outFileFolder = new File("C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\FH_SH_DIA_MED_HTN_OBE");
	static  File outFileFolder2 = new File("C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\results\\FINAL_MERGE");
	static String textFileFolder = "C:\\Users\\krishagni\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014- Heart Diseases\\Datasets\\2014\\Track2\\training-RiskFactors-Gold-Set1\\textFiles";
	public static void main(String[] args) throws Exception {
		
		 for (File file : outFileFolder.listFiles()) {
			 fileName = file.getName();
			 //find FH file
			 System.out.println("writing file"+fileName);
			 mergeAnn(file,findFile(fileName, HLfile),"HYPERLIPIDEMIA");
			 //getRootTextTags(file);
		}
		

	}

	private static void mergeAnn(File file, File file2, String tag) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = null;
	    Document doc = null;
	    Document doc2 = null;

	    try {
	            db = dbf.newDocumentBuilder();
	            doc = db.parse(file);
	            doc2 = db.parse(file2);
	            NodeList ndListFirstFile = doc.getElementsByTagName("TAGS");
	            NodeList nodeList = doc2.getElementsByTagName(tag);
	            for (int i = 0; i < nodeList.getLength(); i++) {
	            	 Node nodeArea = doc.importNode(nodeList.item(0), true);
	  	           // Node nodeCity = doc.importNode(doc2.getElementsByTagName("city").item(0), true);
	  	            ndListFirstFile.item(0).appendChild(nodeArea);
				}
	           
	           // ndListFirstFile.item(0).appendChild(nodeCity);

	          TransformerFactory tFactory = TransformerFactory.newInstance();
	          Transformer transformer = tFactory.newTransformer();
	          transformer.setOutputProperty(OutputKeys.INDENT, "yes");  

	          DOMSource source = new DOMSource(doc);
	          StreamResult result = new StreamResult(new StringWriter());
	          transformer.transform(source, result); 
	          File outFile = new File(outFileFolder2,fileName);
	          Writer output = new BufferedWriter(new FileWriter(outFile));
	          String xmlOutput = result.getWriter().toString();  
	          output.write(xmlOutput);
	          output.close();

	    } catch (ParserConfigurationException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (SAXException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (TransformerException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }




	}
		
	
	private static String getTextFile(String fileName) throws IOException {
		//find Files
		File textFolder = new File(textFileFolder);
		File textFile = findFile(fileName,textFolder.listFiles());
		FileReader fr = new FileReader(textFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String text = "";
		while((line=br.readLine())!=null){
			text = text+line+"\n";
		}
		br.close();fr.close();
		return text;
	}
	private static File findFile(String fileName, File[] textFolder) {
		for (File textFile : textFolder) {
			if(textFile.getName().split("\\.")[0].equalsIgnoreCase(textFile.getName().split("\\.")[0])){
				return textFile;
			}
		}
		return null;
	}
	private static NodeList getObesityTag(File[] fileList) throws Exception {
		File file = findFile(fileName, OBEfile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList medicationTag = doc.getElementsByTagName("OBESE");
		return medicationTag;
	}
	

}
