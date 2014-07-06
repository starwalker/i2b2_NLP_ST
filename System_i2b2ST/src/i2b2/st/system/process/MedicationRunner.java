package i2b2.st.system.process;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

//update type system to include time attr ==> add feature time

public class MedicationRunner {
	
	
	public void process(CAS cas) throws CASException {
	
		//implement UIMA RUTA
		
		TypeSystem ts = cas.getTypeSystem();  

		Iterator<Type> typeItr = ts.getTypeIterator();
		while (typeItr.hasNext()) {
			Type type = (Type) typeItr.next();
			System.out.println(type.getName());
			if (type.getName().equals("org.apache.ctakes.typesystem.type.refsem.Element")) {
				List<Feature> featItr = type.getFeatures();
				//System.out.println(type.isPrimitive());;
				for (Feature feature : featItr) {
					System.out.print("\t"+feature.getDomain());
					System.out.print("\t"+feature.getName());
					System.out.println("\t"+feature.getRange());
					//System.out.println(feature.getShortName());
					//System.out.println(feature.getRange().getFeatures().size());
					
				}
			}
			
			
		}
		//System.out.println(typeItr.next().getName());
	}

}
