package cyber;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TSVCaseReader extends CaseReader{

	TSVCaseReader(String filename) {
		super(filename);
	}

	@Override
	List<Case> readCases() {
		List<Case> caseList = new ArrayList<>();
		Scanner input;
		/*read the file*/
		try {
			input = new Scanner(new File(filename));
			int countError = 0;
			String message = null;
			while (input.hasNextLine()) {
				String[] line = input.nextLine().toString().split("\t");
				if (line[0].trim()=="" || line[1].trim()=="" || line[2].trim()=="" || line[3].trim()=="") {
					countError++;
				} else {
				/*add every line of the file-content to the caseList as case*/
				caseList.add(new Case(line[0].trim(),line[1].trim(),line[2].trim(),
						line[3].trim(),line[4].trim(),line[5].trim(),line[6].trim()));
				}
			}
			input.close();
			if (countError>0) {
				message = countError+" cases rejected.\nThe file must have cases with \ntab separated date, title, and case number!";
				throw new DataException(message);
			};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DataException e) {
		}
		return caseList;
	}

}
