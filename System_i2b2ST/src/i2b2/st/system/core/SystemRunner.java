package i2b2.st.system.core;

import i2b2.st.system.process.MedicationRunner;

import java.io.File;

import org.apache.uima.cas.CAS;



public class SystemRunner {

	public static void main(String[] args) {
		
		File xmiCasFile = new File("220-01.txt.xmi");
		String tsd = "C:\\Users\\krishagni\\NLPworkspace\\System_i2b2ST\\aggregateUMLSTYpesystem";
		CasDeserialiszer csd = new CasDeserialiszer(xmiCasFile, tsd);
		CAS cas;
		try {
			cas = csd.getCAS();
			MedicationRunner mr = new MedicationRunner();
			mr.process(cas);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
