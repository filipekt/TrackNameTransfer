package cz.filipekt;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Provides a simple GUI interface to the functionality offered by
 * {@link Transfer}.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class GUIWrapper extends Application {

	/**
	 * Starts the JavaFX runtime and launches the application
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * Constructs the GUI interface offered by this class
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane pane = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(column1, column2);
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		Label labelDirA = new Label("Directory A");
		Label labelDirB = new Label("Directory B");
		pane.add(labelDirA, 0, 0);
		pane.add(fieldDirA, 1, 0);
		pane.add(labelDirB, 0, 1);
		pane.add(fieldDirB, 1, 1);
		Button selectA = new Button("Select..");
		Button selectB = new Button("Select..");
		selectA.setOnAction(new DirAction(fieldDirA));
		selectB.setOnAction(new DirAction(fieldDirB));
		pane.add(selectA, 2, 0);
		pane.add(selectB, 2, 1);
		Button OKbutton = new Button("OK");
		OKbutton.setOnAction(okAction);
		Button clearButton = new Button("Clear");
		clearButton.setOnAction(clearAction);
		pane.add(OKbutton, 0, 2);
		pane.add(clearButton, 1, 2);
		progressBar.setProgress(0);
		progressBar.setDisable(true);
		progressBar.prefWidthProperty().bind(primaryStage.widthProperty());
		pane.add(progressBar, 0, 3, 3, 1);
		fieldDirA.setOnDragOver(event -> {
			event.acceptTransferModes(TransferMode.COPY);
		});
		fieldDirA.setOnDragDropped(new DragAction(fieldDirA));
		fieldDirB.setOnDragOver(event -> {
			event.acceptTransferModes(TransferMode.COPY);
		});
		fieldDirB.setOnDragDropped(new DragAction(fieldDirB));
		primaryStage.setTitle("TrackNameTransfer");
		primaryStage.setWidth(500);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Handles the drag&drop functionality of the input fields
	 * 
	 * @author Tomas Filipek <tom.filipek@seznam.cz>
	 */
	private static class DragAction implements EventHandler<DragEvent> {

		/**
		 * The field over which drag&drop is performed
		 */
		private final TextField field;

		/**
		 * @param field
		 *            The field over which drag&drop is performed
		 */
		public DragAction(TextField field) {
			this.field = field;
		}

		/**
		 * Makes sure that when the user drop a file in the input field, its
		 * content is updated with the path to the file
		 */
		@Override
		public void handle(DragEvent event) {
			Dragboard dragBoard = event.getDragboard();
			if (dragBoard.hasFiles()) {
				for (File file : dragBoard.getFiles()) {
					field.setText(file.getAbsolutePath().toString());
				}
				event.setDropCompleted(true);
				event.consume();
			}
		}

	}

	/**
	 * The input field specifying the source directory
	 */
	private final TextField fieldDirA = new TextField();

	/**
	 * The input field specifying the target directory
	 */
	private final TextField fieldDirB = new TextField();

	/**
	 * The progress-bar which marks that the application is currently busy
	 */
	private final ProgressBar progressBar = new ProgressBar();

	/**
	 * Executed when the user clicks the "Clear" button in the main application
	 * window. Clears the input forms.
	 */
	private EventHandler<ActionEvent> clearAction = event -> {
		fieldDirA.setText("");
		fieldDirB.setText("");
	};

	/**
	 * Executed when the user clicks the "OK" button in the main application
	 * window. Starts the track name transferring process.
	 */
	private EventHandler<ActionEvent> okAction = event -> {
		try {
			if ((fieldDirA.getText() == null) || fieldDirA.getText().isEmpty()) {
				throw new IllegalArgumentException();
			}
			if ((fieldDirB.getText() == null) || fieldDirB.getText().isEmpty()) {
				throw new IllegalArgumentException();
			}
			Thread worker = new Thread(() -> {
				try {
					Transfer tr = new Transfer(fieldDirA.getText(), fieldDirB.getText());
					tr.work();
				} catch (Exception ex) {
					reportError(ex.getLocalizedMessage());
				} finally {
					Platform.runLater(() -> {
						progressBar.setProgress(0);
						progressBar.setDisable(true);
					});
				}
			});
			worker.start();
			progressBar.setProgress(-1);
			progressBar.setDisable(false);
		} catch (IllegalArgumentException e) {
			reportError("Please enter the paths to both directories.");
		}
	};

	/**
	 * Shows a blocking pop-up window, containing an error message
	 * 
	 * @param message
	 *            The error message to show
	 */
	private void reportError(String message) {
		Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
		alert.showAndWait();
	}

	/**
	 * Provides the "directory selection" dialogs, allowing for selection of the
	 * source and target directories.
	 * 
	 * @author Tomas Filipek <tom.filipek@seznam.cz>
	 */
	private static class DirAction implements EventHandler<ActionEvent> {

		/**
		 * The field where the selected directory will be recorded
		 */
		private final TextField field;

		/**
		 * Shows a directory selection dialog and records the selected directory
		 * to the field given in {@link DirAction#field}
		 */
		@Override
		public void handle(ActionEvent event) {
			DirectoryChooser dc = new DirectoryChooser();
			File file;
			Window window = field.getScene().getWindow();
			if ((file = dc.showDialog(window)) != null) {
				field.setText(file.toPath().toAbsolutePath().toString());
			}
		}

		/**
		 * @param field The field where the selected directory will be recorded
		 */
		public DirAction(TextField field) {
			this.field = field;
		}

	}

}
