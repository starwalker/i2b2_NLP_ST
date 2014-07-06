package i2b2.st.system.process;

import i2b2.st.system.core.CasDeserialiszer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;

public class MedicationCSVWriter {
	public static String sysoutFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\mergedOutput";
	public static String textFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\textFiles";	
	public static String cTakesTsd = "C:\\Users\\krishagni\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014- Heart Diseases\\Datasets\\2014\\Track2\\training-RiskFactors-Gold-Set1\\aggregateUMLSTYpesystem";
	public static String medAnnType = "org.apache.ctakes.typesystem.type.textsem.MedicationMention";
	static CAS cas;
	static String fileName;
	public static void main(String[] args) {
		
		try {
			File [] outFiles = new File(sysoutFolderPath).listFiles();
			FileWriter fw = new FileWriter("CtakesFilteresOut_Test_Medication.csv");
			for (File outFile : outFiles) {
				System.out.println("writing file"+outFile.getName());
				fileName = outFile.getName();
				medTrainingFileWriter(outFile,fw);
			}
			fw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void medTrainingFileWriter(File outFile, FileWriter fw) throws Exception {
		CasDeserialiszer csd = new CasDeserialiszer(outFile, cTakesTsd); 
		cas = csd.getCAS();
		TypeSystem ts = cas.getTypeSystem();  
		Iterator<Type> typeItr = ts.getTypeIterator();
		while (typeItr.hasNext()) {
			Type type = (Type) typeItr.next();
			if (type.getName().equals(medAnnType)) {

				AnnotationIndex<AnnotationFS> annotations2 = cas.getAnnotationIndex(type);
				
				for (AnnotationFS afs : annotations2){
					fw.write(fileName+","+afs.getCoveredText()+","+afs.getBegin()+","+afs.getEnd()+"\n");
				}
				
			}
		}
	}

}
