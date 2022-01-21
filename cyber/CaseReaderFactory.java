package cyber;

public class CaseReaderFactory {
	CaseReader createReader(String filename) {
		/*distinguish the file type and return an appropriate caseReader*/
		if("csv".equals(filename.substring(filename.length() - 3))){
			CaseReader caseReader = new CSVCaseReader(filename);
			return caseReader;
		}
		else if ("tsv".equals(filename.substring(filename.length() - 3))){
			CaseReader caseReader = new TSVCaseReader(filename);
			return caseReader;
		}	
		
		return null;
	}
}
