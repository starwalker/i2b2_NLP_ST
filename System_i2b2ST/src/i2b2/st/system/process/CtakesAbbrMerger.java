package i2b2.st.system.process;

import i2b2.st.system.core.Annotation;
import i2b2.st.system.core.CasDeserialiszer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

public class CtakesAbbrMerger {
//	static File rutaXmiCasFileFolder = new File("C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\abbr_output");
//	static String rutaTsd = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\abbreviationRulesTypeSystem.xml";
//	static String ctakesOutFolderPath = "C:\\Users\\krishagni\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014- Heart Diseases\\Datasets\\2014\\Track2\\training-RiskFactors-Gold-Set1\\ctakesXmiCasOut";
	
	static File rutaXmiCasFileFolder = new File("C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\rutaOutput");
	static String rutaTsd = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\Medication\\V1\\abbreviationRulesTypeSystem.xml";
	static String ctakesOutFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\xmiFiles";
	
	
	static String cTakesTsd = "C:\\Users\\krishagni\\Dropbox\\TCRN DTM Projects\\Data,Text Mining, IE, IR, NLP\\ST- i2b2 -2014- Heart Diseases\\Datasets\\2014\\Track2\\training-RiskFactors-Gold-Set1\\aggregateUMLSTYpesystem";
	public static final String mOutputDirPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\mergedOutput";
	public static File mOutputDir;	
	public static File currentRutaXmiCasFile;
	public static CAS cas;
	public static void main(String[] args) {
		File [] rutaXmiCasFiles = rutaXmiCasFileFolder.listFiles();
		
		for (File rutaXmiCasFile : rutaXmiCasFiles) {
			currentRutaXmiCasFile = rutaXmiCasFile;
			List<Annotation> medicationAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.medication.medication");
			List<Annotation> cadAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.cad");
			List<Annotation> diaAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.diabetes");
			List<Annotation> htnAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.hypertension");
			List<Annotation> labValueAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.LabValues");
			List<Annotation> bPValueAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"medication.abbr.abbreviationRules.Bpvalue");
			List<Annotation> hlValueAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.hyperlipedimia");
			List<Annotation> hgValueAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.hgValue");
			List<Annotation> bmiValueAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.bmiValue");
			List<Annotation> obesityAnnList = getMedicationRutaAnnotation(rutaXmiCasFile,rutaTsd,"i2b2.riskfactor.diseasedisorder.obesity");
			try {
				File ctakeFile = findFile(rutaXmiCasFile.getName());
				System.out.println("processing: "+ctakeFile.getName());
				if(ctakeFile!=null){
				CasDeserialiszer csd = new CasDeserialiszer(ctakeFile, cTakesTsd);
				cas = csd.getCAS();
				addToCtakesAnnotation(medicationAnnList,"org.apache.ctakes.typesystem.type.textsem.MedicationMention");
				addToCtakesAnnotation(cadAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(diaAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(htnAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(labValueAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(bPValueAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(hlValueAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(hgValueAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(bmiValueAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				addToCtakesAnnotation(obesityAnnList,"org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention");
				processCas(cas);
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

		
	}
	
	
	private static  CAS addToCtakesAnnotation(List<Annotation> annList,String annType) throws Exception {
		//find ctakes outFile
		
			
			
			TypeSystem ts= cas.getTypeSystem();
			Type type2 = ts.getType(annType);
			for (Annotation annotation : annList) {
				AnnotationFS test = cas.createAnnotation(type2, annotation.getStartOffset(), annotation.getEndOffset());
				cas.getIndexRepository().addFS(test);
			}
			return cas;
		} 
		
		// add annotation
		//write xmiOut

	public static void processCas(CAS aCAS) throws ResourceProcessException {
	 	JCas jcas;
	    try {
	      jcas = aCAS.getJCas();
	    } catch (CASException e) {
	      throw new ResourceProcessException(e);
	    }

	    // retrieve the filename of the input file from the CAS
	    
	       String outFileName = currentRutaXmiCasFile.getName();
	       mOutputDir = new File(mOutputDirPath);
	        if (!mOutputDir.exists()) {
	          mOutputDir.mkdirs();
	        }
	        File outFile = new File(mOutputDir, outFileName);
	        //modelFileName = mOutputDir.getAbsolutePath() + "/" + inFile.getName() + ".ecore";
	      
	    // serialize XCAS and write to output file
	    try {
	      writeXmi(jcas.getCas(), outFile);
	    } catch (IOException e) {
	      throw new ResourceProcessException(e);
	    } catch (SAXException e) {
	      throw new ResourceProcessException(e);
	    }
	  }

	  
	   
	  private static void writeXmi(CAS aCas, File name) throws IOException, SAXException {
	    FileOutputStream out = null;

	    try {
	      // write XMI
	      out = new FileOutputStream(name);
	      XmiCasSerializer ser = new XmiCasSerializer(aCas.getTypeSystem());
	      XMLSerializer xmlSer = new XMLSerializer(out, false);
	      ser.serialize(aCas, xmlSer.getContentHandler());
	    } finally {
	      if (out != null) {
	        out.close();
	      }
	    }
	  }
	
	private static File findFile(String name) {
		File ctakesFolder = new File(ctakesOutFolderPath);
		File[] ctakesFiles = ctakesFolder.listFiles();
		for (File ctakesFile : ctakesFiles) {
			if(ctakesFile.getName().split("\\.")[0].equals(name.split("\\.")[0])){
				return ctakesFile;
			}
		}
		return null;

	}

	private static List<Annotation> getMedicationRutaAnnotation(File xmiCasFile, String tsd,String typeName) {
		CasDeserialiszer csd = new CasDeserialiszer(xmiCasFile, tsd);
		CAS cas;
		List<Annotation> annList = new ArrayList<Annotation>();
		try {
			cas = csd.getCAS();
			TypeSystem ts = cas.getTypeSystem();  

			Iterator<Type> typeItr = ts.getTypeIterator();

			while (typeItr.hasNext()) {
				Type type = (Type) typeItr.next();
				if (type.getName().equals(typeName)) {

					AnnotationIndex<AnnotationFS> annotations2 = cas.getAnnotationIndex(type);
					for (AnnotationFS afs : annotations2)
					{
						Annotation ann = new Annotation();
						Integer startOff = afs.getBegin();
						Integer endOff   = afs.getEnd();
						String annText   = afs.getCoveredText();
						ann.setAnnotatedText(annText);
						ann.setEndOffset(endOff);ann.setStartOffset(startOff);
						annList.add(ann);
					}					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return annList;
	}


}
