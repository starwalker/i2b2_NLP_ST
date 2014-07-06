package i2b2.st.system.core;

import java.io.File;
import java.io.FileInputStream;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;

public class CasDeserialiszer {
	CAS cas;
	File casFile;
	String tsd;
	public String getTsd() {
		return tsd;
	}
	public void setTsd(String tsd) {
		this.tsd = tsd;
	}
	
	public File getCasFile() {
		return casFile;
	}
	public void setCasFile(File casFile) {
		this.casFile = casFile;
	}
	public CasDeserialiszer(File casFile,String tsd) {
		super();
		this.casFile = casFile;
		this.tsd = tsd;
	}
	public CAS getCAS() throws  Exception {
		TypeSystemDescription tsd = UIMAFramework.getXMLParser().parseTypeSystemDescription(new XMLInputSource(this.tsd));
		
		CAS cas = CasCreationUtils.createCas(tsd, null, null);
		XmiCasDeserializer.deserialize(new FileInputStream(this.casFile), cas,false);
		return cas;
		
	}

}
