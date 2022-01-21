package cyber;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CyberCop extends Application {

	public static final String DEFAULT_PATH = "data"; // folder name where data files are stored
	public static final String DEFAULT_HTML = "/CyberCop.html"; // local HTML
	public static final String APP_TITLE = "Cyber Cop"; // displayed on top of app

	CCView ccView = new CCView();
	CCModel ccModel = new CCModel();
	CaseView caseView; // UI for Add/Modify/Delete menu option
	GridPane cyberCopRoot;
	Stage stage;
	Stage viewstage;
	static Case currentCase; // points to the case selected in TableView.

	public static void main(String[] args) {
		launch(args);
	}

	/** start the application and show the opening scene */
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		primaryStage.setTitle("Cyber Cop");
		cyberCopRoot = ccView.setupScreen();
		setupBindings();
		setupAction();
		Scene scene = new Scene(cyberCopRoot, ccView.ccWidth, ccView.ccHeight);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		ccView.webEngine.load(getClass().getResource(DEFAULT_HTML).toExternalForm());
		primaryStage.show();
	}

	/**
	 * setupBindings() binds all GUI components to their handlers. It also binds
	 * disabsleProperty of menu items and text-fields with ccView.isFileOpen so that
	 * they are enabled as needed
	 */
	void setupBindings() {

		/* bind MenuItems to ccView.isFileOpen */
		ccView.openFileMenuItem.disableProperty().bind(ccView.isFileOpen);
		ccView.saveFileMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		ccView.closeFileMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		ccView.addCaseMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		ccView.modifyCaseMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		ccView.deleteCaseMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		ccView.caseCountChartMenuItem.disableProperty().bind(ccView.isFileOpen.not());
		/* bind TextField to ccView.isFileOpen */
		ccView.titleTextField.disableProperty().bind(ccView.isFileOpen.not());
		ccView.caseNumberTextField.disableProperty().bind(ccView.isFileOpen.not());
		ccView.caseTypeTextField.disableProperty().bind(ccView.isFileOpen.not());
		ccView.yearComboBox.disableProperty().bind(ccView.isFileOpen.not());
		/* bind Button to ccView.isFileOpen */
		ccView.searchButton.disableProperty().bind(ccView.isFileOpen.not());
		ccView.clearButton.disableProperty().bind(ccView.isFileOpen.not());

		/* display the content of current-case when click the row of table-view */
		ccView.caseTableView.getSelectionModel().selectedItemProperty()
				.addListener((observableValue, oldValue, newValue) -> {
					if (newValue != null) {
						currentCase = newValue;
						currentCasedisplay();
						/* display the web according to different kinds of link */
						if (currentCase.getCaseLink() == null || currentCase.getCaseLink().isBlank()) { // if no link in
																										// data
							URL url = getClass().getResource(DEFAULT_HTML); // default html
							if (url != null)
								ccView.webEngine.load(url.toExternalForm());
						} else if (currentCase.getCaseLink().toLowerCase().startsWith("http")) { // if external link
							ccView.webEngine.load(currentCase.getCaseLink());
						} else {
							URL url = getClass().getClassLoader().getResource(currentCase.getCaseLink().trim()); // local
																													// link
							if (url != null)
								ccView.webEngine.load(url.toExternalForm());
						}
					}
				});
		ccView.caseTableView.getSelectionModel().selectedItemProperty().addListener((event) -> {
		});
	}

	void setupAction() {
		/* saveFileMenuItem handler */
		ccView.saveFileMenuItem.setOnAction((event) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(DEFAULT_PATH));
			File file = fileChooser.showSaveDialog(stage);
			boolean writeSuccess = ccModel.writeCases(DEFAULT_PATH + "/" + file.getName());//invoke ccModel's writeCase() method.
			// If the writeCase() method returns true, it displays the "filename saved" message on messageLabel.
			if (writeSuccess) {
				ccView.messageLabel.setText(file.getName() + " saved.");
			}
		});

		/* caseCountChartMenuItem handler */
		ccView.caseCountChartMenuItem.setOnAction((event) -> {
			//Invokes ccView's showChartView(), passing ccModel's yearMap to it.
			ccView.showChartView(ccModel.yearMap);
		});

		/* openFileMenuItem handler */
		ccView.openFileMenuItem.setOnAction((event) -> {
			/* choose file and open it */
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(DEFAULT_PATH));
			File file = fileChooser.showOpenDialog(stage);
			if (file != null) {
				ccModel.readCases(DEFAULT_PATH + "/" + file.getName());
			}
			ccView.isFileOpen.set(true);// when file opened, isFileOpen is true
			ccView.caseTableView.setItems(ccModel.caseList);
			ccView.messageLabel.setText(String.valueOf(ccModel.caseList.size()) + " cases.");
			ccModel.buildYearMapAndList();
			Collections.sort(ccModel.yearList);// sort the year in year-combo-box
			ccView.yearComboBox.setItems(ccModel.yearList);// set year-combo-box
			if (!ccModel.caseList.isEmpty()) {
				currentCase = ccModel.caseList.get(0);// change current-case to the 1st row
				currentCasedisplay();
			} else
				currentCase = null;
			currentCasedisplay();
		});

		/* closeFileMenuItem handler */
		ccView.closeFileMenuItem.setOnAction((event) -> {
			ccView.isFileOpen.set(false);// when file closed, isFileOpen is false
			ccView.caseTableView.setItems(null);// clear the table-view
			currentCase = null;// change current-case to null
			ccViewclear();
		});

		/* exitMenuItem handler */
		ccView.exitMenuItem.setOnAction((event) -> {
			stage.close();
		});

		/* searchButton handler */
		ccView.searchButton.setOnAction((event) -> {
			List<Case> cases = ccModel.searchCases(ccView.titleTextField.getText(), ccView.caseTypeTextField.getText(),
					ccView.yearComboBox.getValue(), ccView.caseNumberTextField.getText());
			ObservableList<Case> resultsCases = FXCollections.observableArrayList();
			for (Case c : cases) {
				resultsCases.add(c);// put the searched cases into resultsCases
			}
			ccView.caseTableView.setItems(resultsCases);// show the resultsCases
			/* if find any case, set 1st row to current case and display it */
			if (!resultsCases.isEmpty()) {
				currentCase = resultsCases.get(0);
				currentCasedisplay();
			} else
				currentCase = null;
			ccView.messageLabel.setText(String.valueOf(resultsCases.size()) + " cases.");// update message-label
		});

		/* clearButton handler */
		ccView.clearButton.setOnAction((event) -> {
			ccViewclear();
		});

		/* addCaseMenuItem handler */
		ccView.addCaseMenuItem.setOnAction((event) -> {
			caseView = new AddCaseView("Add Case");
			caseView.updateButton.setText("Add Case");
			viewstage = caseView.buildView();
			viewstage.show();
			/* handlers of buttons in the add case view */
			caseView.updateButton.setOnAction((e) -> {
				String message = null;
				try {
					// if any of the first 4 features of the case is empty, throw exception
					if (caseView.caseDatePicker.getValue().toString() == "" || caseView.titleTextField.getText() == ""
							|| caseView.caseTypeTextField.getText() == ""
							|| caseView.caseNumberTextField.getText() == "") {
						message = "Case must have date, title, type, and number";
						throw new DataException(message);
					} else if (ccModel.caseMap.containsKey(caseView.caseNumberTextField.getText())) { // if caseMap contains the new case number, throw exception 
						message = "Duplicate case number";
						throw new DataException(message);
					} else {
						Case c = new Case(caseView.caseDatePicker.getValue().toString(),
								caseView.titleTextField.getText(), caseView.caseTypeTextField.getText(),
								caseView.caseNumberTextField.getText(), caseView.caseLinkTextField.getText(),
								caseView.categoryTextField.getText(), caseView.caseNotesTextArea.getText()); // construct a new case
						ccModel.caseList.add(c);// put the new case into case-list
						ccView.caseTableView.setItems(ccModel.caseList);// display the case-list
						ccView.messageLabel.setText(String.valueOf(ccModel.caseList.size()) + " cases.");// update message-label
						/* update yearList and combo box */
						int flag = 0;
						for (String yr : ccModel.yearList) {
							if (c.getCaseDate().substring(0, 4).equals(yr)) {
								flag = 1;
							} // if the year is in yearMap's keySet, mark it as 1
						}
						if (flag == 0) {
							ccModel.yearList.add(c.getCaseDate().substring(0, 4));// add the year to the yearList
						}
						Collections.sort(ccModel.yearList);// sort the year in year-combo-box
						ccModel.caseMap.put(caseView.caseNumberTextField.getText(), c); // put the new case into caseMap
					}
				} catch (DataException e1) {
				}
			});
			caseView.clearButton.setOnAction((e) -> {
				caseViewclear();
			});
			caseView.closeButton.setOnAction((e) -> {
				viewstage.close();
			});
		});

		/* modifyCaseMenuItem handler */
		ccView.modifyCaseMenuItem.setOnAction((event) -> {
			caseView = new ModifyCaseView("Modify Case");
			caseView.updateButton.setText("Modify Case");
			currentCasetoupdate();
			viewstage = caseView.buildView();
			viewstage.show();
			/* handlers of buttons in the modify case view */
			caseView.updateButton.setOnAction((e) -> {
				String message = null;
				try {
					// if any of the first 4 features of the case is empty, throw exception
					if (caseView.caseDatePicker.getValue().toString() == "" || caseView.titleTextField.getText() == ""
							|| caseView.caseTypeTextField.getText() == ""
							|| caseView.caseNumberTextField.getText() == "") {
						message = "Case must have date, title, type, and number";
						throw new DataException(message);
					} else if (caseView.caseNumberTextField.getText()!=currentCase.caseNumberProperty().toString() &&ccModel.caseMap.containsKey(caseView.caseNumberTextField.getText())) {
						//if the case number is changed to one which already exists, throw exception
						message = "Duplicate case number";
						throw new DataException(message);
					} else {
						ccModel.caseList.remove(currentCase); // delete the current-case
						ccModel.caseMap.remove(currentCase.caseNumberProperty().toString()); // delete the current-case from caseMap
						Case c = new Case(caseView.caseDatePicker.getValue().toString(),
								caseView.titleTextField.getText(), caseView.caseTypeTextField.getText(),
								caseView.caseNumberTextField.getText(), caseView.caseLinkTextField.getText(),
								caseView.categoryTextField.getText(), caseView.caseNotesTextArea.getText());// construct a new case according to the text-field
						ccModel.caseList.add(c);// put the new case into caseList
						/* update yearList and combo box */
						int flag = 0;
						for (String yr : ccModel.yearList) {
							if (c.getCaseDate().substring(0, 4).equals(yr)) {
								flag = 1;
							} // if the year is in yearMap's keySet, mark it as 1
						}
						if (flag == 0) {
							ccModel.yearList.add(c.getCaseDate().substring(0, 4));// add the year to the yearList
						}
						Collections.sort(ccModel.yearList);// sort the year in year-combo-box
						ccModel.caseMap.put(caseView.caseNumberTextField.getText(), c); // put the new case into caseMap
					}
				} catch (DataException e1) {
				}
			});
			caseView.clearButton.setOnAction((e) -> {
				caseViewclear();
			});
			caseView.closeButton.setOnAction((e) -> {
				viewstage.close();
			});
		});

		/* deleteCaseMenuItem handler */
		ccView.deleteCaseMenuItem.setOnAction((event) -> {
			caseView = new DeleteCaseView("Delete Case");
			caseView.updateButton.setText("Delete Case");
			currentCasetoupdate();
			viewstage = caseView.buildView();
			viewstage.show();
			/* handlers of buttons in the delete case view */
			caseView.updateButton.setOnAction((e) -> {
				ccModel.caseList.remove(currentCase); //delete currentCase from caseList
				ccModel.caseMap.remove(currentCase.caseNumberProperty().toString()); //delete currentCase from caseMap
				ccView.messageLabel.setText(String.valueOf(ccModel.caseList.size()) + " cases.");// update message-label
			});
			caseView.clearButton.setOnAction((e) -> {
				caseViewclear();
			});
			caseView.closeButton.setOnAction((e) -> {
				viewstage.close();
			});
		});

	}

	/* clear the text-field in the case-view */
	void caseViewclear() {
		caseView.titleTextField.setText("");
		caseView.caseLinkTextField.setText("");
		caseView.caseTypeTextField.setText("");
		caseView.caseNotesTextArea.setText("");
		caseView.caseDatePicker.setValue(LocalDate.now());
	}

	/* clear the text-field in the main view */
	void ccViewclear() {
		ccView.titleTextField.setText("");
		ccView.yearComboBox.setValue("");
		ccView.caseTypeTextField.setText("");
		ccView.caseNumberTextField.setText("");
		ccView.caseNotesTextArea.setText("");
		ccView.webEngine.load(getClass().getResource(DEFAULT_HTML).toExternalForm());
	}

	/* set the text-field in the case-view to current-case */
	void currentCasedisplay() {
		ccView.titleTextField.setText(currentCase.getCaseTitle());
		ccView.yearComboBox.setValue(currentCase.getCaseDate().substring(0, 4));
		ccView.caseTypeTextField.setText(currentCase.getCaseType());
		ccView.caseNumberTextField.setText(currentCase.getCaseNumber());
		ccView.caseNotesTextArea.setText(currentCase.getCaseNotes());
	}

	/* set the text-field in the main view to current-case */
	void currentCasetoupdate() {
		caseView.titleTextField.setText(currentCase.getCaseTitle());
		caseView.caseDatePicker
				.setValue(LocalDate.parse(currentCase.getCaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		caseView.caseTypeTextField.setText(currentCase.getCaseType());
		caseView.caseNumberTextField.setText(currentCase.getCaseNumber());
		caseView.categoryTextField.setText(currentCase.getCaseCategory());
		caseView.caseLinkTextField.setText(currentCase.getCaseLink());
		caseView.caseNotesTextArea.setText(currentCase.getCaseNotes());
	}

}
