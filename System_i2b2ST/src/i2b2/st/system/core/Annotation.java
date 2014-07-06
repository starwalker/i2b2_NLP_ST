package i2b2.st.system.core;

public class Annotation {
	String riskFactor;
	Integer startOffset;
	Integer endOffset;
	String annotatedText;
	String fileName;
	String sectionName;
	String time;
	String indicator;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIndicator() {
		return indicator;
	}
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRiskFactor() {
		return riskFactor;
	}
	public void setRiskFactor(String riskFactor) {
		this.riskFactor = riskFactor;
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
	public String getAnnotatedText() {
		return annotatedText;
	}
	public void setAnnotatedText(String annotatedText) {
		this.annotatedText = annotatedText;
	}
	
	

}
