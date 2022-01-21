package cyber;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Case implements Comparable<Case>{
	
	private StringProperty caseDate = new SimpleStringProperty();
	private StringProperty caseTitle = new SimpleStringProperty();
	private StringProperty caseType = new SimpleStringProperty();
	private StringProperty caseNumber = new SimpleStringProperty();
	private StringProperty caseLink = new SimpleStringProperty();
	private StringProperty caseCategory = new SimpleStringProperty();
	private StringProperty caseNotes = new SimpleStringProperty();
	
	/*constructor*/
	Case(String caseDate, String caseTitle, String caseType, String caseNumber, String caseLink, String caseCategory, String caseNotes) {
		this.caseDate.set(caseDate);
		this.caseTitle.set(caseTitle); 
		this.caseType.set(caseType);
		this.caseNumber.set(caseNumber);
		this.caseLink.set(caseLink);
		this.caseCategory.set(caseCategory);
		this.caseNotes.set(caseNotes);
	}
	
	/*get the value*/
	public String getCaseDate() { return caseDate.get(); }
	public String getCaseTitle() { return caseTitle.get(); }
	public String getCaseType() { return caseType.get(); }
	public String getCaseNumber() { return caseNumber.get(); }
	public String getCaseLink() { return caseLink.get(); }
	public String getCaseCategory() { return caseCategory.get(); }
	public String getCaseNotes() { return caseNotes.get(); }
	
	/*set the value*/
	public void setCaseDate(String date){ this.caseDate.set(date); }
	public void setCaseTitle(String title){ this.caseTitle.set(title); }
	public void setCaseType(String type){ this.caseType.set(type); }
	public void setCaseNumber(String number){ this.caseNumber.set(number); }
	public void setCaseLink(String link){ this.caseLink.set(link); }
	public void setCaseCategory(String category){ this.caseCategory.set(category); }
	public void setCaseNotes(String notes){ this.caseNotes.set(notes); }
	
	/*get the property*/
	StringProperty caseDateProperty() {	return caseDate; }
	StringProperty caseTitleProperty() { return caseTitle; }
	StringProperty caseTypeProperty() {	return caseType; }
	StringProperty caseNumberProperty() { return caseNumber; }
	StringProperty caseLinkProperty() {	return caseLink; }
	StringProperty caseCategoryProperty() {	return caseCategory; }
	StringProperty caseNotesProperty() { return caseNotes; }
	
	@Override
    public String toString() {
        return this.getCaseNumber();
    }

	@Override
	public int compareTo(Case c) {
		return c.getCaseDate().compareTo(this.getCaseDate());
	}
	
}
