package i2b2.st.system.process;

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

public class MedicationFeatureExtractor {
	static String completeSetFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\completeSet";
	static String sectionInfoFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\sectionResutSet1\\SectionForSet1";
	static String extractedAnnotationFile = "CtakesFilteresOut_Medication.csv";
	static FileWriter fw;
	static List<Annotation> annList;
	static LinkedList<String> parsedSectionList;
	static List<Section> sectionList;
	public static void main(String[] args) throws Exception {
		fw = new FileWriter(extractedAnnotationFile);
		getAnnotations();
		fw.close();
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
			NodeList obeseTagList = doc.getElementsByTagName("MEDICATION");
			ArrayList<Annotation> annListThisFile = new ArrayList<Annotation>();
			for (int i = 0; i < obeseTagList.getLength(); i++) {
				
				Node obeseNode = obeseTagList.item(i);
				if(obeseNode.hasChildNodes()){
					NodeList childNodeList = obeseNode.getChildNodes();
					for (int j = 0; j < childNodeList.getLength(); j++) {
						Node childNode = childNodeList.item(j);
						if(childNode.getNodeName().equals("MEDICATION")){
							String annotatedText = (childNode.getAttributes().getNamedItem("text").getNodeValue());
							Integer startOff = Integer.parseInt(childNode.getAttributes().getNamedItem("start").getNodeValue());
							Integer endOff = Integer.parseInt(childNode.getAttributes().getNamedItem("end").getNodeValue());
							Annotation ann = new Annotation();
							ann.setFileName(completeFile.getName());
							ann.setStartOffset(startOff);
							ann.setEndOffset(endOff);
							ann.setAnnotatedText(annotatedText);
							String timeAttr = (childNode.getAttributes().getNamedItem("time").getNodeValue());
//							String indicator = (childNode.getAttributes().getNamedItem("indicator").getNodeValue());
//							ann.setIndicator(indicator);
							ann.setTime(timeAttr);
							annListThisFile.add(ann);
							break;
						}
					}
					//System.out.println(obeseNode.getFirstChild().hasAttributes());
				}

			}
			postProcessMedication(annListThisFile);
		}
		System.out.println(annList.size());
	}
	private static void postProcessMedication(
			ArrayList<Annotation> annListThisFile) throws IOException {
		ArrayList<Annotation> tempAnnList = new ArrayList<Annotation>();
		for (Annotation annotation : annListThisFile) {
			boolean during=false,after= false,before = false;
			Integer startOffSet = annotation.getStartOffset();
			Integer endOffset = annotation.getEndOffset();
			String timeAttr = annotation.getTime();
			for (Annotation annotation2 : annListThisFile) {
				Integer startOffSet2 = annotation2.getStartOffset();
				Integer endOffset2 = annotation2.getEndOffset();
				String timeAttr2 = annotation2.getTime();
				if(startOffSet2.equals(startOffSet)&&endOffset2.equals(endOffset)){
					if(timeAttr2.equals("before DCT")){
						before=true;
					}else if(timeAttr2.equals("after DCT")){
						after=true;
					}else if(timeAttr2.equals("during DCT")){
						during = true;
					}
				}
				if(during&&after&&before){
					Annotation ann = new Annotation();
					ann.setStartOffset(startOffSet);ann.setEndOffset(endOffset);ann.setTime("continuing");ann.setFileName(annotation.getFileName());
					ann.setAnnotatedText(annotation.getAnnotatedText());
					tempAnnList.add(ann);
					break;
				}
				
			}
			if(!(during&&after&&before)){
				Annotation ann = new Annotation();
				ann.setStartOffset(startOffSet);ann.setEndOffset(endOffset);ann.setTime(annotation.getTime());ann.setFileName(annotation.getFileName());
				ann.setAnnotatedText(annotation.getAnnotatedText());
				tempAnnList.add(ann);
			}
			
			
		}
		for (Annotation annotation : tempAnnList) {
			fw.write(annotation.getFileName()+","+annotation.getAnnotatedText().toLowerCase()+","+annotation.getStartOffset()+","+annotation.getEndOffset()+","+annotation.getTime()+",\n");
		}
	}
	private static boolean generateSectionFromFile() throws IOException {
		File sectionFilesFolder = new File(sectionInfoFolderPath);
		File [] sectionFiles = sectionFilesFolder.listFiles();
		for (Annotation ann : annList) {
			//System.out.println(ann.getFileName()+" "+ann.getStartOffset()+" "+ann.getEndOffset());
			File sectionFile = findFiles(sectionFiles,ann.getFileName());
			getSectionList(sectionFile);
			//find in list
			int i = 0;
			sectionList = new LinkedList<Section>();
			for (String sectionOffset : parsedSectionList) {
				Section sec = new Section();
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

}
