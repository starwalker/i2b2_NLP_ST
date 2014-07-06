package i2b2.st.system.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModelTestFileGenerator {
	static String extractedAnnotationFilePath = "C:\\work\\NLP\\i2b2_sharedTask\\systemOutput\\models\\testFiles\\obesity\\CtakesFilteresOut_obesity.csv";
	public static void main(String[] args) {
		try {
			generateTestFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void generateTestFile() throws IOException {
		FileWriter fw = new FileWriter("obesity_test.arff");
		fw.write("@RELATION OBESITY\n");
		fw.write("@ATTRIBUTE  annotatedText string\n");
		fw.write("@ATTRIBUTE indicator string\n");
		fw.write("@ATTRIBUTE section string\n");
		fw.write("@ATTRIBUTE class        {'during DCT','before DCT','after DCT',continuing}\n\n\n");
		fw.write("@DATA\n");
		FileReader fr = new FileReader(extractedAnnotationFilePath);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line=br.readLine())!=null){
			//get sectionInfo from File
		}
		
	}

}
