package cyber;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ModifyCaseView extends CaseView {

	ModifyCaseView(String header) {
		super(header);
	}

	@Override
	Stage buildView() {
		Scene scene = new Scene(this.updateCaseGridPane, this.CASE_WIDTH, this.CASE_HEIGHT);
		stage.setScene(scene);
		return stage;
	}
}
