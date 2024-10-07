import GUIutils.GUIBoard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Board;
import utils.BoardLayouts;

public class GUI extends Application implements EntryPoint {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox rootDisplay = new VBox();


        GUIBoard guiBoard = new GUIBoard(new Board(15, BoardLayouts.getBoardLayout(15)));


        HBox playerTray = new HBox();

        rootDisplay.getChildren().addAll(guiBoard.getRoot(), playerTray);
        Scene root = new Scene(rootDisplay);
        primaryStage.setScene(root);
        primaryStage.show();
    }
}
