package i2b2.st.system.core;

public class OntologyConcept {
	Integer ontologyConceptId;
	String tui;
	String cui;
	boolean disambiguated;

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public boolean isDisambiguated() {
		return disambiguated;
	}

	public void setDisambiguated(boolean disambiguated) {
		this.disambiguated = disambiguated;
	}

	public String getTui() {
		return tui;
	}

	public void setTui(String string) {
		this.tui = string;
	}

	public Integer getOntologyConceptId() {
		return ontologyConceptId;
	}

	public void setOntologyConceptId(Integer ontologyConceptId) {
		this.ontologyConceptId = ontologyConceptId;
	}
	

}
