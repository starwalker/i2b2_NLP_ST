package i2b2.st.system.core;

import java.util.ArrayList;


public class EntityMention {
	Integer startOffset;
	Integer endOffset;
	String textSpan;
	ArrayList<OntologyConcept> ocList;
	String timeIndicator;
	String fileName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTimeIndicator() {
		return timeIndicator;
	}
	public void setTimeIndicator(String timeIndicator) {
		this.timeIndicator = timeIndicator;
	}
	public ArrayList<OntologyConcept> getOcList() {
		return ocList;
	}
	public void setOcList(ArrayList<OntologyConcept> ocList) {
		this.ocList = ocList;
	}
	public Integer getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(Integer startOffset) {
		this.startOffset = startOffset;
	}
	public Integer getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(Integer endOffset) {
		this.endOffset = endOffset;
	}
	public String getTextSpan() {
		return textSpan;
	}
	public void setTextSpan(String textSpan) {
		this.textSpan = textSpan;
	}
	
	

}
