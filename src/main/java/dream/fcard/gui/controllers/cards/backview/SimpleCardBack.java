package dream.fcard.gui.controllers.cards.backview;

import java.io.IOException;
import java.util.function.Consumer;

import dream.fcard.gui.controllers.windows.MainWindow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * The rear view of a front-back card.
 */
public class SimpleCardBack extends AnchorPane {
    @FXML
    private Button seeFrontButton;
    @FXML
    private Button correctButton;
    @FXML
    private Label answerLabel;

    public SimpleCardBack(String backOfCard, Consumer<Boolean> wantToSeeFront, Consumer<Boolean> updateScore) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/Cards/"
                    + "Back/SimpleCardBack.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
            answerLabel.setText(backOfCard);
            seeFrontButton.setOnAction(e -> wantToSeeFront.accept(true));
            correctButton.setOnAction(e -> updateScore.accept(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}