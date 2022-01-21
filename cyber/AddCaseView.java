package cyber;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddCaseView extends CaseView {
	
	AddCaseView(String header) {
		super(header);
	}

	@Override
	Stage buildView() {
		Scene scene = new Scene(this.updateCaseGridPane, this.CASE_WIDTH, this.CASE_HEIGHT);
		stage.setScene(scene);
		return stage;
	}

}

