package i2b2.st.system.model;

import i2b2.st.system.core.Annotation;
import i2b2.st.system.core.Section;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Read csv with list of obese annotation
 * Read xmi
 * extarct feture
 * write to arff file
 * */

public class FeatureExtractor {
	static String completeSetFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\completeSet";
	
	static String sectionInfoFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\TestAll_Sectionizer_Dai";
	static String extractedAnnotationFile = "CtakesFilteresOut_Test_hl.csv";
	static List<Annotation> annList;
	static LinkedList<String> parsedSectionList;
	static List<Section> sectionList;
	//static File medCsvFile = new File("CtakesFilteresOut_Test_Medication.csv");

	public static void main(String[] args) {
		try {
			//for training data file
			  //getAnnotations();
			//getAnnotationForMedTrainingData();
			//for tes data file
			 getAnnotationFromCSv();
			generateSectionFromFile();
			//for test file
			 generateArffFromCSv();
			//for training
			 //postProcess();
			for (Annotation ann : annList) {
				System.out.println(ann.getFileName()+" "+ann.getAnnotatedText()+" "+ann.getStartOffset()+" "+ann.getEndOffset()+" "+ann.getIndicator()+" "+ann.getTime()+" "+ann.getSectionName());
			}
			 //for tarining data
			///arffWriter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
//	private static void getAnnotationForMedTrainingData() throws Exception {
//		FileReader fr  = new FileReader(medCsvFile);
//		BufferedReader br = new BufferedReader(fr);
//		String line;
//		annList = new ArrayList<Annotation>();
//		while((line=br.readLine())!=null){
//			System.out.println(line);
//			//220-01.xml,ZESTRIL,1339,1346,continuing,
//			String[] lineSplit = line.split(",");
//			String fileName = lineSplit[0];
//			String annotatedText = lineSplit[1];
//			String start = lineSplit[2];
//			String end = lineSplit[3];
//			String time = lineSplit[4];
//			Annotation ann = new Annotation();
//			ann.setAnnotatedText(annotatedText);
//			ann.setEndOffset(Integer.parseInt(end));
//			ann.setStartOffset(Integer.parseInt(start));
//			ann.setFileName(fileName);
//			ann.setTime(time);
//			annList.add(ann);
//		}
//		br.close();fr.close();
//	}
	private static void generateArffFromCSv() throws IOException {
		FileWriter fw = new FileWriter("hl_test_test.arff");
		fw.write("@RELATION OBESITY\n");
		fw.write("@ATTRIBUTE  annotatedText string\n");
		fw.write("@ATTRIBUTE indicator string\n");
		fw.write("@ATTRIBUTE section string\n");
		fw.write("@ATTRIBUTE class        {'during DCT','before DCT','after DCT',continuing}\n\n\n");
		fw.write("@DATA\n");
		for (Annotation ann : annList) {
			String indicator = "mention";
			if(ann.getAnnotatedText().toLowerCase().contains("ldl")){
				indicator = "high LDL";
			}
//			else if(ann.getAnnotatedText().toLowerCase().contains("glu")){
//				indicator = "glucose";
//			}
			fw.write("'"+ann.getAnnotatedText().trim()+"','"+indicator+"','"+ann.getSectionName()+"','during DCT'"+"\n");
		}
		
		fw.close();
		
	}
	private static void getAnnotationFromCSv() throws IOException {
		FileReader fr = new FileReader(extractedAnnotationFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		annList = new ArrayList<Annotation>();
		while((line=br.readLine())!=null){
			String [] split = line.split(",");
			System.out.println(line.split(",").length);
			if((line.split(",").length==4)){
				System.out.println(line);
				Integer startOffset = Integer.parseInt(split[3]);
				Integer endOffset = Integer.parseInt(split[2]);
				String annotatedText = split[1];
				String fileName = split[0];
				Annotation ann = new Annotation();
				ann.setAnnotatedText(annotatedText);
				ann.setFileName(fileName);
				ann.setStartOffset(startOffset);
				ann.setEndOffset(endOffset);
				annList.add(ann);
			}
			
		}
		fr.close();br.close();
	}
	private static void arffWriter() throws IOException {
		//create Header
		FileWriter fw = new FileWriter("medication_training.arff");
		fw.write("@RELATION OBESITY\n");
		fw.write("@ATTRIBUTE  annotatedText string\n");
		fw.write("@ATTRIBUTE indicator string\n");
		fw.write("@ATTRIBUTE section string\n");
		fw.write("@ATTRIBUTE class        {'during DCT','before DCT','after DCT',continuing}\n\n\n");
		fw.write("@DATA\n");
		for (Annotation ann : annList) {
			fw.write("'"+ann.getAnnotatedText().trim()+"','"+ann.getIndicator()+"','"+ann.getSectionName()+"',"+"'"+ann.getTime()+"'\n");
		}
		
		fw.close();
	}
	private static void postProcess() {
		for (Annotation ann : annList) {
			if(ann.getIndicator().equalsIgnoreCase("mention")){
				ann.setTime("continuing");
			}
		}
		
	}
	private static boolean generateSectionFromFile() throws IOException {
		File sectionFilesFolder = new File(sectionInfoFolderPath);
		File [] sectionFiles = sectionFilesFolder.listFiles();
		for (Annotation ann : annList) {
			System.out.println(ann.getFileName()+" "+ann.getStartOffset()+" "+ann.getEndOffset());
			File sectionFile = findFiles(sectionFiles,ann.getFileName());
			getSectionList(sectionFile);
			//find in list
			int i = 0;
			sectionList = new LinkedList<Section>();
			for (String sectionOffset : parsedSectionList) {
				Section sec = new Section();
				System.out.println(sectionOffset.split(" ").length);
				if(sectionOffset.split(" ").length>2){
					sec.setSectioName(sectionOffset.split(" ")[2]);
					String currentSectionEnd = sectionOffset.split(" ")[1];
					String nextSectionStart = null;
					if(i<parsedSectionList.size()-1){
						String nextSection = parsedSectionList.get(i+1);
						nextSectionStart = nextSection.split(" ")[0];
					}
					else{
						nextSectionStart = "10000";
					}
					sec.setStartOffset(Integer.parseInt(currentSectionEnd));
					sec.setEndOffset(Integer.parseInt(nextSectionStart));
					sectionList.add(sec);
					i++;
					
				}
				
			}
			for (Section sec  : sectionList) {
				Integer secStart = sec.getStartOffset();
				Integer secEnd = sec.getEndOffset();
				if(secStart<ann.getStartOffset()&&secEnd>ann.getEndOffset()){
					//System.out.println(secStart+" "+ann.getStartOffset()+" "+" "+ann.getEndOffset()+" "+secEnd);
					ann.setSectionName(sec.getSectioName());
					break;
				}
			}
		}
		
		
		
		
		//get section info
		
		return false;
	}
	
	private static void getSectionList(File sectionFile) throws IOException {
		parsedSectionList = new LinkedList<String>();
		FileReader fr = new FileReader(sectionFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line=br.readLine())!=null){
			if(line.contains("Section")){
				//System.out.println(line.split("\t")[2]);
				String sectionName = line.split("\t")[2];
				//System.out.println(sectionName);
				String start = line.split("\t")[1].split(" ")[1];
				String end = line.split("\t")[1].split(" ")[2];
				parsedSectionList.add(start+" "+end+" "+sectionName);
			}
		}
		//reverse it
		LinkedList<String> tempList = new LinkedList<String>();
		Iterator x = parsedSectionList.descendingIterator();

		// print list with descending order
		while (x.hasNext()) {
			tempList.add((String) x.next());
		}
		parsedSectionList = tempList;
		fr.close();br.close();
	}

	private static File findFiles(File[] sectionFiles, String fileName) {
		for (File secFile : sectionFiles) {
			if(secFile.getName().split("\\.")[0].equals(fileName.split("\\.")[0])){
				return secFile;
			}
		}
		return null;
	}
	
	private static void getAnnotations() throws IOException, SAXException, ParserConfigurationException {
		File [] completeSetFiles = new File(completeSetFolderPath).listFiles();
		annList = new ArrayList<Annotation>();
		for (File completeFile : completeSetFiles) {
			//read file and extarct obesity info
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(completeFile);
			doc.getDocumentElement().normalize();
			NodeList obeseTagList = doc.getElementsByTagName("DIABETES");
			for (int i = 0; i < obeseTagList.getLength(); i++) {
				Node obeseNode = obeseTagList.item(i);
				if(obeseNode.hasChildNodes()){
					NodeList childNodeList = obeseNode.getChildNodes();
					for (int j = 0; j < childNodeList.getLength(); j++) {
						Node childNode = childNodeList.item(j);
						if(childNode.getNodeName().equals("DIABETES")){
							String annotatedText = (childNode.getAttributes().getNamedItem("text").getNodeValue());
							Integer startOff = Integer.parseInt(childNode.getAttributes().getNamedItem("start").getNodeValue());
							Integer endOff = Integer.parseInt(childNode.getAttributes().getNamedItem("end").getNodeValue());
							Annotation ann = new Annotation();
							ann.setFileName(completeFile.getName());
							ann.setStartOffset(startOff);
							ann.setEndOffset(endOff);
							ann.setAnnotatedText(annotatedText);
							String timeAttr = (childNode.getAttributes().getNamedItem("time").getNodeValue());
							String indicator = (childNode.getAttributes().getNamedItem("indicator").getNodeValue());
							ann.setIndicator(indicator);
							ann.setTime(timeAttr);
							annList.add(ann);
							break;
						}
					}
					//System.out.println(obeseNode.getFirstChild().hasAttributes());
				}

			}
		}
		System.out.println(annList.size());
	}

}
