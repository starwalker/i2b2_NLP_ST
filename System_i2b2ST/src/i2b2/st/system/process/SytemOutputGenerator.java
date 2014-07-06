package i2b2.st.system.process;

import i2b2.st.system.core.Annotation;
import i2b2.st.system.core.CasDeserialiszer;
import i2b2.st.system.core.Section;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SytemOutputGenerator {

	public static String sysoutFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\mergedOutput";
	public static String cTakesTsd = "C:\\Users\\krishagni\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014- Heart Diseases\\Datasets\\2014\\Track2\\training-RiskFactors-Gold-Set1\\aggregateUMLSTYpesystem";	
	public static String medAnnType = "org.apache.ctakes.typesystem.type.textsem.MedicationMention";
	public static CAS cas;
	public static String outFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\systemOutput";
	public static File outFolder;
	public static String fileName;
	public static String sectionInfoFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\sectionResutSet1\\SectionForSet1";
	public static File annotatedMedicationList = new File("annotatedMedList.csv");
	static FileWriter fw ;
	static LinkedList<String> parsedSectionList;
	static List<Section> sectionList;
	public static void main(String[] args) throws IOException {

		File sysoutFolder = new File(sysoutFolderPath);
		File[] sysouFiles = sysoutFolder.listFiles();
		fw = new FileWriter(annotatedMedicationList);
		for (File sysoutFile : sysouFiles) {
			fileName = sysoutFile.getName().split("\\.")[0]+".xml";
			generateSectionFromFile();
			processOutputFile(sysoutFile);
		}
		fw.close();
	}

	private static boolean generateSectionFromFile() throws IOException {
		//System.out.println(begin+"  "+end);
		File sectionFilesFolder = new File(sectionInfoFolderPath);
		File [] sectionFiles = sectionFilesFolder.listFiles();
		File sectionFile = findFiles(sectionFiles);
		//get section info
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
	private static void processOutputFile(File sysoutFile) {
		CasDeserialiszer csd = new CasDeserialiszer(sysoutFile, cTakesTsd);
		try {
			cas = csd.getCAS();
			//System.out.println(cas.getDocumentLanguage());
			writeToXML();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	private static void writeToXML() throws TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("root");
			doc.appendChild(rootElement);
			Element text = doc.createElement("TEXT");
			text.setTextContent(cas.getDocumentText());
			rootElement.appendChild(text);
			//create tags
			Element tags = doc.createElement("TAGS");
			rootElement.appendChild(tags);

			//getAnnotation
			writeMedAnnotation(tags,doc);


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);

			//write to console or file
			if(outFolder==null||!outFolder.exists()){
				outFolder = new File(outFolderPath);
				outFolder.mkdir();
			}
			System.out.println("writing file: "+fileName);
			StreamResult file = new StreamResult(new File(outFolder,fileName));
			//write data
			transformer.transform(source, file);
			//System.out.println("done");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	private static void writeMedAnnotation(Element tags, Document doc) throws IOException {
		HashMap<String, String> drugClassMap = getDrugClass(new File("Drug_class.csv"));

		TypeSystem ts = cas.getTypeSystem();  

		Iterator<Type> typeItr = ts.getTypeIterator();
		List<Section> medSections = getMedSectionsFromSections("med","medical");
		while (typeItr.hasNext()) {
			Type type = (Type) typeItr.next();
			if (type.getName().equals(medAnnType)) {

				AnnotationIndex<AnnotationFS> annotations2 = cas.getAnnotationIndex(type);
				int i =0;
				for (AnnotationFS afs : annotations2)
				{
					Annotation ann = new Annotation();

					String annText   = afs.getCoveredText().toLowerCase();
					Integer startOff = afs.getBegin();
					Integer endOff = afs.getEnd();

					fw.write(annText+"\n");
					if(!annText.equals("today")){
						String drugClass = drugClassMap.get(annText);

						if(medSections.size()>0&&drugClass!=null){
							for (Section section : medSections) {
								Integer sectionStart = section.getStartOffset();
								Integer sectionEnd = section.getEndOffset();
								if(sectionStart<startOff&&sectionEnd>endOff){

									for (int j = 0; j < 3; j++) {
										Element medicationElem = doc.createElement("MEDICATION");
										medicationElem.setAttribute("id", "DOC"+i);
										if(j==0){
											medicationElem.setAttribute("time", "before DCT");
										}else if(j==1){
											medicationElem.setAttribute("time", "during DCT");
										}else{
											medicationElem.setAttribute("time", "after DCT");
										}
										medicationElem.setAttribute("type1",drugClass);
										medicationElem.setAttribute("type2", "");
										tags.appendChild(medicationElem);
										i++;
									}
								}
							}
						}
						//need to implement further
						else if(drugClass!=null){
							Element medicationElem = doc.createElement("MEDICATION");
							medicationElem.setAttribute("id", "DOC"+i);
							medicationElem.setAttribute("time", "before DCT");
							medicationElem.setAttribute("type1",drugClass);
							medicationElem.setAttribute("type2", "");
							tags.appendChild(medicationElem);
							i++;
						}

					}

				}					
			}
		}

	}

	private static List<Section> getMedSectionsFromSections(String matcher, String nonMatcher) {
		List<Section> medSection = new LinkedList<Section>();
		for (Section sec : sectionList) {
			if(sec.getSectioName().toLowerCase().contains(matcher)&&!sec.getSectioName().toLowerCase().contains(nonMatcher)){
				medSection.add(sec);
			}
		}
		return medSection;
	}


	private static File findFiles(File[] sectionFiles) {
		for (File secFile : sectionFiles) {
			if(secFile.getName().split("\\.")[0].equals(fileName.split("\\.")[0])){
				return secFile;
			}
		}
		return null;
	}
	private static HashMap<String, String> getDrugClass(File drugClassFIle) throws IOException {
		HashMap<String, String> drugClassMap = new HashMap<String, String>();
		FileReader fr = new FileReader(drugClassFIle);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line=br.readLine())!=null){
			//System.out.println(line);
			if(line.split(",").length>1){
				String [] split = line.split(",");
				drugClassMap.put(split[0],split[1]);
			}

		}
		br.close();
		return drugClassMap;
	}

}
