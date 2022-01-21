package cyber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class CCModel {
	ObservableList<Case> caseList = FXCollections.observableArrayList(); // a list of case objects
	ObservableMap<String, Case> caseMap = FXCollections.observableHashMap(); // map with caseNumber as key and Case as value
	ObservableMap<String, List<Case>> yearMap = FXCollections.observableHashMap(); // map with each year as a key and a list of all cases dated in that year as value.
	ObservableList<String> yearList = FXCollections.observableArrayList(); // list of years to populate the yearComboBox in ccView
	/**
	 * writes caseList elements in a TSV file.
	 * @param filename
	 * @return If the write is successful, it returns true. In case of IOException, it returns false.
	 */
	boolean writeCases(String filename) {
		try {
			File file = new File(filename);
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Case c : caseList) {
				String next = c.getCaseDate()+" \t"+c.getCaseTitle()+" \t"+c.getCaseType()+" \t"+c.getCaseNumber()+" \t"+c.getCaseLink()+" \t"+c.getCaseCategory()+" \t"+c.getCaseNotes()+" \t";
				bw.write(next+"\n");
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("File save failed");
			return false;
		}
		return true;
	}

	/**
	 * readCases() performs the following functions: It creates an instance of
	 * CaseReaderFactory, invokes its createReader() method by passing the filename
	 * to it, and invokes the caseReader's readCases() method. The caseList returned
	 * by readCases() is sorted in the order of caseDate for initial display in
	 * caseTableView. Finally, it loads caseMap with cases in caseList. This caseMap
	 * will be used to make sure that no duplicate cases are added to data
	 * 
	 * @param filename
	 */
	void readCases(String filename) {
		CaseReaderFactory crf = new CaseReaderFactory();
		CaseReader cr = crf.createReader(filename);
		List<Case> cases = cr.readCases();
		caseList = FXCollections.observableArrayList();
		for (Case c : cases) {
			caseList.add(c);
		}
		Collections.sort(caseList); // sort caseList in the order of caseDate for initial display in caseTableView
		for (Case c : caseList) {
			if (!caseMap.keySet().contains(c.getCaseNumber()))
				caseMap.put(c.getCaseNumber(), c); // load caseMap with cases in caseList.
		}
	}

	/**
	 * buildYearMapAndList() performs the following functions: 1. It builds yearMap
	 * that will be used for analysis purposes in Cyber Cop 3.0 2. It creates
	 * yearList which will be used to populate yearComboBox in ccView Note that
	 * yearList can be created simply by using the keySet of yearMap.
	 */
	void buildYearMapAndList() {
		yearMap = FXCollections.observableHashMap();
		yearList = FXCollections.observableArrayList();
		for (Case c : caseList) {
			int flag = 0;
			for (String yr : yearMap.keySet()) {
				if (c.getCaseDate().substring(0, 4).equals(yr)) {
					flag = 1;
				} // if the year is in yearMap's keySet, mark it as 1
			}
			if (flag == 0)
				yearMap.put(c.getCaseDate().substring(0, 4), new ArrayList<Case>()); // if the year is not in yearMap's
																						// keySet, add it to the
																						// yearMap's keySet with a empty
																						// value
			yearMap.get(c.getCaseDate().substring(0, 4)).add(c); // add the case to the value(a List<Case>)
		}
		yearList.addAll(yearMap.keySet());
	}

	/**
	 * searchCases() takes search criteria and iterates through the caseList to find
	 * the matching cases. It returns a list of matching cases.
	 */
	List<Case> searchCases(String title, String caseType, String year, String caseNumber) {
		List<Case> resultsCases = new ArrayList<Case>();
		for (Case c : caseList) {
			/*
			 * if the search-value is not empty, judge if the search-value is contained in
			 * the value of case. if the case does not contain that search-value, set its
			 * flag 0. return the cases with flag 0
			 */
			int flag = 1;
			if (title != null) {
				if (!c.getCaseTitle().toLowerCase().contains(title.toLowerCase())) {
					flag = 0;
				}
			}
			if (caseType != null) {
				if (!c.getCaseType().toLowerCase().contains(caseType.toLowerCase())) {
					flag = 0;
				}
			}
			if (year != null) {
				if (!c.getCaseDate().contains(year)) {
					flag = 0;
				}
			}
			if (caseNumber != null) {
				if (!c.getCaseNumber().contains(caseNumber)) {
					flag = 0;
				}
			}
			if (flag == 1) {
				resultsCases.add(c);
			}
		}
		return resultsCases;
	}
}
