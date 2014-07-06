package i2b2.st.system.process;

import i2b2.st.system.core.EntityMention;
import i2b2.st.system.core.OntologyConcept;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class cTakesDiseaseDisorderExtractor {
	public static String sysoutFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\mergedOutputNewline";
	public static String textFolderPath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\testData\\textFiles";
	public static void main(String[] args) throws IOException {
		FileWriter fw = new FileWriter("CtakesFilteresOut_Test_hl.csv");
		File [] outputFiles = new File(sysoutFolderPath).listFiles();
		for (File file : outputFiles) {
			System.out.println(file.getName());
			ArrayList<EntityMention> em  = processOutputFile(file);
			//System.out.println(em.size());
			em = filterAnnotations(em);
			//System.out.println(em.size());
			//filter obesity types
			//ArrayList<EntityMention> obesityEntities = filterObesityEntity(em);
			//filter hyperlipidemia types
			ArrayList<EntityMention> hlEntities = filterHyperlipidemiaTypes(em);
			//filter hyperTension Types
			//ArrayList<EntityMention> htEntities = filterHyperTensionTYpes(em);
			//filter diabetes type
			//ArrayList<EntityMention> diabetesEntities = filterDiabetesTYpes(em);
			writeToxls(hlEntities,fw,file);
			
		}
		fw.close();
	}
	private static ArrayList<EntityMention> filterDiabetesTYpes(
			ArrayList<EntityMention> em) {
		//COO11849,COO11854,COO11860,COO11881,COO11882,COO11884,COO17667,CO206172,CO267176,CO342257,CO406682,C1456868
		ArrayList<String> diaOcList = new ArrayList<String>();
		diaOcList.add("C0011849");
		diaOcList.add("C0011854");
		diaOcList.add("C0011860");
		diaOcList.add("C0011881");
		diaOcList.add("C0011882");
		diaOcList.add("C0011884");
		diaOcList.add("C0017667");
		diaOcList.add("C0206172");
		diaOcList.add("C0267176");
		diaOcList.add("C0342257");
		diaOcList.add("C0406682");
		diaOcList.add("C1456868");
		ArrayList<EntityMention> diaEntities = new ArrayList<EntityMention>();
		for (EntityMention entityMention : em) {
			for (OntologyConcept ontologyConcept : entityMention.getOcList()) {
				//System.out.println(ontologyConcept.getCui());
				if(diaOcList.contains(ontologyConcept.getCui())){
					System.out.println(entityMention.getTextSpan());
					diaEntities.add(entityMention);
					break;
				}
			}
			//System.out.println(entityMention.getTextSpan().toLowerCase());
			if(		entityMention.getTextSpan().toLowerCase().contains("DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DM2".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DM 2".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DMII".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DM II".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("IDDM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DMT2".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type 1 DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("T1DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("NIDDM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type I DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type 1 DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type1 DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("Type II DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type 2 DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("type2 DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("T2DM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("AODM".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("MODY".toLowerCase())||
					//entityMention.getTextSpan().toLowerCase().contains("NOD".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("DKA".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("Hgb A1c".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("GLU".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("FG".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("FS".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("OGTT".toLowerCase())||
					entityMention.getTextSpan().toLowerCase().contains("a1c".toLowerCase())){
				System.out.println(entityMention.getTextSpan().toLowerCase());
				diaEntities.add(entityMention);
			}
		}
		return diaEntities;
	}
	private static ArrayList<EntityMention> filterHyperTensionTYpes(
			ArrayList<EntityMention> em) {
		//C0020538,C0020540,C0024588,C0155617,C0155583	
		ArrayList<String> hyperTensionOcList = new ArrayList<String>();
		hyperTensionOcList.add("C0020538");
		hyperTensionOcList.add("C0020540");
		hyperTensionOcList.add("C0024588");
		hyperTensionOcList.add("C0155617");
		hyperTensionOcList.add("C0155583");
		ArrayList<EntityMention> htEntities = new ArrayList<EntityMention>();
		for (EntityMention entityMention : em) {
			for (OntologyConcept ontologyConcept : entityMention.getOcList()) {
				if(hyperTensionOcList.contains(ontologyConcept.getCui())){
					System.out.println(entityMention.getTextSpan());
					htEntities.add(entityMention);
					break;
				}
			}
			//System.out.println(entityMention.getTextSpan().toLowerCase());
			if(entityMention.getTextSpan().toLowerCase().contains("bp")||
					entityMention.getTextSpan().toLowerCase().contains("blood pressure")||
					entityMention.getTextSpan().toLowerCase().contains("hbp")||
					entityMention.getTextSpan().toLowerCase().contains("htn")||
					entityMention.getTextSpan().matches("[1-4][0-9][0-9]/[0-9][0-9]")){
				System.out.println(entityMention.getTextSpan().toLowerCase());
				htEntities.add(entityMention);
			}
		}
		return htEntities;
	}
	private static ArrayList<EntityMention> filterHyperlipidemiaTypes(ArrayList<EntityMention> em) {
		ArrayList<String> hyperLipidemiaOcList = new ArrayList<String>();
		hyperLipidemiaOcList.add("C0020473");
		hyperLipidemiaOcList.add("C0020443");
		hyperLipidemiaOcList.add("C0342879");
		hyperLipidemiaOcList.add("C1522133");
		hyperLipidemiaOcList.add("C0853084");
		hyperLipidemiaOcList.add("C0023822");
		hyperLipidemiaOcList.add("C0242339");
			

		ArrayList<EntityMention> hlEntities = new ArrayList<EntityMention>();
		for (EntityMention entityMention : em) {
			for (OntologyConcept ontologyConcept : entityMention.getOcList()) {
				if(hyperLipidemiaOcList.contains(ontologyConcept.getCui())){
					System.out.println(entityMention.getTextSpan());
					hlEntities.add(entityMention);
					break;
				}
			}
			//System.out.println(entityMention.getTextSpan().toLowerCase());
			if(entityMention.getTextSpan().toLowerCase().contains("chol")||
					entityMention.getTextSpan().toLowerCase().contains("lipid")||
					entityMention.getTextSpan().toLowerCase().contains("ldl")||
					entityMention.getTextSpan().toLowerCase().contains("hl")){
				//System.out.println(entityMention.getTextSpan().toLowerCase());
				hlEntities.add(entityMention);
			}
		}
		return hlEntities;
	}
	private static ArrayList<EntityMention> filterObesityEntity(ArrayList<EntityMention> em) {
		ArrayList<String> obesityOcList = new ArrayList<String>();
		obesityOcList.add("C0028756");
		obesityOcList.add("C0028754");
		obesityOcList.add("C1319441");
		obesityOcList.add("C0031880");
		ArrayList<EntityMention> obesityEntities = new ArrayList<EntityMention>();
		for (EntityMention entityMention : em) {
			for (OntologyConcept ontologyConcept : entityMention.getOcList()) {
				if(obesityOcList.contains(ontologyConcept.getCui())){
					System.out.println(entityMention.getTextSpan());
					obesityEntities.add(entityMention);
					break;
				}
			}
			//System.out.println(entityMention.getTextSpan().toLowerCase());
			if(entityMention.getTextSpan().toLowerCase().contains("bmi")){
				//System.out.println(entityMention.getTextSpan().toLowerCase());
				obesityEntities.add(entityMention);
			}
		}
		return obesityEntities;
		
	}
	private static void writeToxls(ArrayList<EntityMention> em, FileWriter fw, File file) throws IOException {
		
		for (EntityMention entityMention : em) {
			fw.write(file.getName()+",");
			fw.write(entityMention.getTextSpan()+",");
			fw.write(entityMention.getStartOffset()+",");
			fw.write(entityMention.getEndOffset()+",");
//			for (OntologyConcept ontologyConcept : entityMention.getOcList()) {
//				//fw.write(ontologyConcept.getCui()+",");
//			}
			fw.write("\n");
			
		}		
		
	}
	private static ArrayList<EntityMention> filterAnnotations(ArrayList<EntityMention> em) {
		//filter Dr
		//filter 20/07
		//filter offset if annotaion is between bigger one
		//remove filtered
		Iterator<EntityMention> itr = em.iterator();
		while (itr.hasNext()) {
			EntityMention entityMention = (EntityMention) itr.next();
			if(entityMention.getTextSpan().equalsIgnoreCase("Dr")){
				itr.remove();
			}
			
		}
		itr = em.iterator();
		while (itr.hasNext()) {
			EntityMention entityMention = (EntityMention) itr.next();
			boolean ifFound = getAnnotationInAnnotatedSpan(em,entityMention);
			if(ifFound){
				itr.remove();
			}
			
		}
		itr = em.iterator();
		while (itr.hasNext()) {
			EntityMention entityMention = (EntityMention) itr.next();
			if(entityMention.getTextSpan().matches("[0-9][0-9]/[0-9][0-9]")){
				//System.out.println(entityMention.getTextSpan());
				itr.remove();
			}
			
		}

		
		
//		for (Integer index : indexToremove) {
//			em.remove(index);
//		}
		return em;
	}
	private static boolean getAnnotationInAnnotatedSpan(
			ArrayList<EntityMention> em, EntityMention entityMention) {
		for (EntityMention listEm : em) {
			Integer emStartPos = listEm.getStartOffset();
			Integer emEndPos = listEm.getEndOffset();
			//if 
			if(emStartPos<entityMention.getStartOffset()&&emEndPos>=entityMention.getEndOffset()){
				return true;
			}else if(emStartPos>entityMention.getStartOffset()&&emEndPos<=entityMention.getEndOffset()){
				return true;
			}
		}
		return false;
	}
	private static String getFileText(String fileName) throws IOException {
		String fileText = "";
		File inFolder =  new File(textFolderPath);
		File [] inFileList = inFolder.listFiles();
		for (File file : inFileList) {
			String fileName2 = file.getName().split("\\.")[0];
			if(fileName2.equals(fileName.split("\\.")[0])){
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line=br.readLine())!=null){
					fileText = fileText+line+"\n";
				}
				br.close();fr.close();
			}
		}
		return fileText;

	}
	private static ArrayList<EntityMention> processOutputFile(File sysoutFile) throws IOException {
		FileReader fr = new FileReader(sysoutFile);
		BufferedReader br = new  BufferedReader(fr);
		String line;
		ArrayList<EntityMention> entityMentionList = new ArrayList<EntityMention>();
		//System.out.println(sysoutFile.getName());
		while((line=br.readLine())!=null){
			if(line.contains("textsem:DiseaseDisorderMention")){
				String [] split = line.split(" ");
				String startOffset = split[3].split("=")[1].replace("\"", "");
				String endOffset = split[4].split("=")[1].replace("\"", "");
				//System.out.println(startOffset+endOffset);
				Pattern p = Pattern.compile("\"([^\"]*)\"");
				Matcher m = p.matcher(line);
				ArrayList<OntologyConcept> ontologyConcepts = new ArrayList<OntologyConcept>();
				int i = 1;
				while (m.find()) {
					//System.out.println(m.group(1));
					if(i==6){
						String [] conceptIds = m.group(1).split(" ");
						for (String ids : conceptIds) {
							if(!ids.equals("0")){
								Integer id = Integer.parseInt(ids);
								//System.out.println(id);
								OntologyConcept oc = new OntologyConcept();
								oc.setOntologyConceptId(id);
								ontologyConcepts.add(oc);
							}
							
						}
						//System.out.println(m.group(1));
					}

					i++;
				}
				
				EntityMention em = new EntityMention();
				Integer startOff =Integer.parseInt(startOffset);
				em.setStartOffset(startOff);
				Integer endOff = Integer.parseInt(endOffset);
				em.setEndOffset(endOff);
				String fileText = getFileText(sysoutFile.getName());
				String annotatedText = fileText.substring(startOff, endOff);
				em.setTextSpan(annotatedText);
				em.setOcList(ontologyConcepts);
				entityMentionList.add(em);
				LinkedHashMap<Integer,String[]> concepTuiMAp = getConceptTuiMap(sysoutFile);
				entityMentionList = setTui(entityMentionList,concepTuiMAp);
				//get list
				
		}
	}
		fr.close();br.close();
		return entityMentionList;
	}
	private static ArrayList<EntityMention> setTui(ArrayList<EntityMention> entityMentionList,
			LinkedHashMap<Integer, String[]> concepTuiMAp) {
		for (EntityMention outputAnnotation : entityMentionList) {
			ArrayList<OntologyConcept> list = outputAnnotation.getOcList();
			if(list.size()>0){
				for (OntologyConcept ontologyConcept : list) {
					Integer ocId = ontologyConcept.getOntologyConceptId();
					//System.out.println(concepTuiMAp.get(ocId));
					String [] conceptFeatures = concepTuiMAp.get(ocId);
					ontologyConcept.setTui(conceptFeatures[0]);
					ontologyConcept.setCui(conceptFeatures[1]);
					//ontologyConcept.setDisambiguated(Boolean.parseBoolean(conceptFeatures[2]));
					//System.out.println(ocId);
				}
			}
			
		}
		return entityMentionList;
	}

	public static LinkedHashMap<Integer, String[]> getConceptTuiMap(
			File outputFile) throws NumberFormatException, IOException {
		LinkedHashMap<Integer, String[]> concepTuiMAp = new LinkedHashMap<Integer, String[]>();
		FileReader fr = new FileReader(outputFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line=br.readLine())!=null){
			if(line.contains("refsem:UmlsConcept")){
				String [] split = line.split(" ");
				//System.out.println(split[5]+split[6]);
				Integer id = Integer.parseInt(split[1].split("=")[1].replace("\"", ""));
				String tui = (split[6].split("=")[1].replace("\"", ""));
				String cui = (split[5].split("=")[1].replace("\"", ""));
				//String disStr = (split[8].split("=")[1].replace("\"", "").replace("/>", ""));
				String [] conceptFeatures = {tui,cui};

				//System.out.println(id+":"+tui);
				concepTuiMAp.put(id, conceptFeatures);
			}

		}
		br.close();fr.close();
		//System.out.println(concepTuiMAp);
		return concepTuiMAp;
	}
}
