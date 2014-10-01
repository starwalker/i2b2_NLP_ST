package i2b2.st.system.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ModelOutputToxmlGenerator {
	static String extractedAnnotationFilePath = "CtakesFilteresOut_Test_medication.csv";
	static String textFileFolder = "D:\\Dropbox - Manish\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014-  Task 2 -Heart Diseases\\ErrorAnalysis\\systemResults\\systemOutput\\testData\\textFiles";
	static String modelInputFile = "";
	static String resultOutputFolderPath = "D:\\Dropbox - Manish\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014-  Task 2 -Heart Diseases\\ErrorAnalysis\\systemResults\\systemOutput\\testData\\results\\medication_complete";
	static LinkedHashMap<String, LinkedList<String>> outputAnn = new LinkedHashMap<String, LinkedList<String>>();
	
	public static void main(String[] args) {
		try {
			LinkedList<String> predictedClassLabel = readModelOutputFile();
			getAnnotations(predictedClassLabel);
			//writeToXml();
			medicationWriteToXMl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	private static void medicationWriteToXMl() throws Exception {
		HashMap<String, String> drugClassMap = getDrugClass(new File("Drug_class.csv"));
		for (Entry<String, LinkedList<String>> elem : outputAnn.entrySet()) {
			String fileName =elem.getKey().split("\\.")[0]+".xml";
			LinkedList<String> annList = elem.getValue();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("root");
			doc.appendChild(rootElement);
			Element text = doc.createElement("TEXT");
			//get Text File
			String fileText = getTextFile(fileName);
			text.setTextContent(fileText);
			rootElement.appendChild(text);
			//create tags
			Element tags = doc.createElement("TAGS");
			rootElement.appendChild(tags);
			int i = 1;
			for (String ann : annList) {
				//220-01.txt.xmi,HCTZ,185,189,continuing

				//System.out.println(ann);
				String medications = ann.split(",")[1];
				String drugClass = drugClassMap.get(medications.toLowerCase());
				
				if(!medications.equals("today")){
					if(drugClass!=null){
						System.out.println(">>>"+ann);
						String time = ann.split(",")[4];
						//String indicator = "";
						//						if(ann.split(",")[1].toLowerCase().contains("a1c")){
						//							indicator = "A1C";
						//						}else if(ann.split(",")[1].toLowerCase().contains("glu")){
						//							indicator = "glucose";
						//						}
						//						else{
						//							indicator = "mention";
						//						}
						if(time.equalsIgnoreCase("continuing")){
							for (int j = 0; j <= 2; j++) {
								if(j==0){
									Element obesityElem = doc.createElement("MEDICATION");
									obesityElem.setAttribute("id", "DOC"+i);
									obesityElem.setAttribute("time", "before DCT");
									obesityElem.setAttribute("type1", drugClass);
									obesityElem.setAttribute("type2", "");
									Element annotationElem = doc.createElement("MEDICATION");
									annotationElem.setAttribute("id", "M"+i);
									annotationElem.setAttribute("start",ann.split(",")[2] );
									annotationElem.setAttribute("end",ann.split(",")[3] );
									obesityElem.appendChild(annotationElem);
									tags.appendChild(obesityElem);
								}
								if(j==1){
									Element obesityElem = doc.createElement("MEDICATION");
									obesityElem.setAttribute("id", "DOC"+i);
									obesityElem.setAttribute("time", "after DCT");
									obesityElem.setAttribute("type1", drugClass);
									obesityElem.setAttribute("type2", "");
									Element annotationElem = doc.createElement("MEDICATION");
									annotationElem.setAttribute("id", "M"+i);
									annotationElem.setAttribute("start",ann.split(",")[2] );
									annotationElem.setAttribute("end",ann.split(",")[3] );
									obesityElem.appendChild(annotationElem);
									tags.appendChild(obesityElem);
								}
								if(j==2){
									Element obesityElem = doc.createElement("MEDICATION");
									obesityElem.setAttribute("id", "DOC"+i);
									obesityElem.setAttribute("time", "during DCT");
									obesityElem.setAttribute("type1", drugClass);
									obesityElem.setAttribute("type2", "");
									Element annotationElem = doc.createElement("MEDICATION");
									annotationElem.setAttribute("id", "M"+i);
									annotationElem.setAttribute("start",ann.split(",")[2] );
									annotationElem.setAttribute("end",ann.split(",")[3] );
									obesityElem.appendChild(annotationElem);
									tags.appendChild(obesityElem);
								}
								i++;
							}
						}else{
							Element obesityElem = doc.createElement("MEDICATION");
							obesityElem.setAttribute("id", "DOC"+i);
							obesityElem.setAttribute("time", time);
							obesityElem.setAttribute("type1", drugClass);
							obesityElem.setAttribute("type2", "");
							Element annotationElem = doc.createElement("MEDICATION");
							annotationElem.setAttribute("id", "M"+i);
							annotationElem.setAttribute("start",ann.split(",")[2] );
							annotationElem.setAttribute("end",ann.split(",")[3] );
							obesityElem.appendChild(annotationElem);
							tags.appendChild(obesityElem);

						}

						i++;
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);

			//write to console or file


			System.out.println("writing file: "+fileName);
			StreamResult file = new StreamResult(new File(resultOutputFolderPath,fileName));
			//write data
			transformer.transform(source, file);




		}
	}
	private static void writeToXml() throws IOException, Exception {
		for (Entry<String, LinkedList<String>> elem : outputAnn.entrySet()) {
			String fileName =elem.getKey().split("\\.")[0]+".xml";
			LinkedList<String> annList = elem.getValue();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("root");
			doc.appendChild(rootElement);
			Element text = doc.createElement("TEXT");
			//get Text File
			String fileText = getTextFile(fileName);
			text.setTextContent(fileText);
			rootElement.appendChild(text);
			//create tags
			Element tags = doc.createElement("TAGS");
			rootElement.appendChild(tags);
			int i = 1;
			for (String ann : annList) {
				System.out.println(ann);

				String time = ann.split(",")[5];
				String indicator = "";
								if(ann.split(",")[1].matches("[1-4][0-9][0-9]/[0-9][0-9]")){
									indicator = "high bp";
								}
//								}else if(ann.split(",")[1].toLowerCase().contains("glu")){
//									indicator = "glucose";
//								}
								else{
									indicator = "mention";
								}
				if(time.equalsIgnoreCase("continuing")){
					for (int j = 0; j <= 2; j++) {
						if(j==0){
							Element obesityElem = doc.createElement("HYPERTENSION");
							obesityElem.setAttribute("id", "DOC"+i);
							obesityElem.setAttribute("time", "before DCT");
							obesityElem.setAttribute("indicator", indicator);
							tags.appendChild(obesityElem);
						}
						if(j==1){
							Element obesityElem = doc.createElement("HYPERTENSION");
							obesityElem.setAttribute("id", "DOC"+i);
							obesityElem.setAttribute("time", "after DCT");
							obesityElem.setAttribute("indicator", indicator);
							tags.appendChild(obesityElem);
						}
						if(j==2){
							Element obesityElem = doc.createElement("HYPERTENSION");
							obesityElem.setAttribute("id", "DOC"+i);
							obesityElem.setAttribute("time", "during DCT");
							obesityElem.setAttribute("indicator", indicator);
							tags.appendChild(obesityElem);
						}
						i++;
					}
				}else{
					Element obesityElem = doc.createElement("HYPERTENSION");
					obesityElem.setAttribute("id", "DOC"+i);
					obesityElem.setAttribute("time", time);
					obesityElem.setAttribute("indicator", indicator);
					tags.appendChild(obesityElem);

				}

				i++;
			}




			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);

			//write to console or file


			System.out.println("writing file: "+fileName);
			StreamResult file = new StreamResult(new File(resultOutputFolderPath,fileName));
			//write data
			transformer.transform(source, file);

			//			File outFile = new File(resultOutputFolderPath,fileName.split("\\.")[0]+"xml");
			//			FileWriter fw = new FileWriter(outFile);
			////			for (String ann : annList) {
			////				fw.write(ann+"\n");
			////			}
			////			fw.close();
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
	private static void getAnnotations(LinkedList<String> predictedClassLabel) throws IOException {
		FileReader fr = new FileReader(extractedAnnotationFilePath);
		BufferedReader br = new BufferedReader(fr);
		String line;
		int i = 0;
		String fileName="";

		while((line=br.readLine())!=null){
			fileName = line.split(",")[0];
			if(i<predictedClassLabel.size()){
				
				if(outputAnn.containsKey(fileName)){
					outputAnn.get(fileName).add(line+","+predictedClassLabel.get(i));
				}else{
					LinkedList<String> ann = new LinkedList<String>();
					ann.add(line+","+predictedClassLabel.get(i));
					outputAnn.put(fileName, ann);
				}
			}
			
			//System.out.println(line+":"+predictedClassLabel.get(i));
			i++;	
		}
		System.out.println(outputAnn);	
		fr.close();br.close();
	}


	private static LinkedList<String> readModelOutputFile() throws Exception {
		FileReader fr  = new FileReader("hpertension_prediction");
		BufferedReader br = new BufferedReader(fr);
		String line;
		//      1 1:during D 1:during D         *1      0      0      0    
		LinkedList<String> predictedClassLabel = new LinkedList<String>();
		while((line=br.readLine())!=null){

			if( line.matches("^\\s+\\d+\\s\\d.*")){
				//String classLabel = "";
				String classLabel = (line.split(":")[2].split("\\s+")[0]);
				if(classLabel.equals("during")){
					classLabel = "during DCT";
				}else if(classLabel.equals("before")){
					classLabel = "before DCT";
				}else if(classLabel.equals("after")){
					classLabel = "after DCT";
				}else if (classLabel.equals("continui")){
					classLabel = "continuing";
				}
				predictedClassLabel.add(classLabel);
			}
		}
		return predictedClassLabel;
	}

}
