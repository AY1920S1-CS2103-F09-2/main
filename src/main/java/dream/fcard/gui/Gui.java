//@@author nattanyz
package dream.fcard.gui;

import dream.fcard.model.State;
import dream.fcard.model.cards.FlashCard;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Gui {

    // the one and only instance of Gui allowed
    private static Gui gui = new Gui();

    // the instance of the main window of the application, containing all UI components
    private static MainWindow applicationMainWindow = new MainWindow();
    // todo: store windowContents here instead of mainWindow?
    private static State applicationState;

    private Gui() {
        // todo
    }

    // todo: refactor Gui's constructors
    public Gui(MainWindow mainWindow, State state) {
        applicationMainWindow = mainWindow;
        applicationState = state;
    }

    static MainWindow getMainWindow() {
        return applicationMainWindow;
    }

    /** Accessor for the Gui instance */
    public static Gui getInstance() {
        if (gui == null) {
            gui = new Gui();
        }
        return gui;
    }

    public static void renderFront(FlashCard flashCard) {
        // todo: use functional interfaces, from renderCard?
        // get text to be rendered
        String cardText = flashCard.getFront();

        // generate Node representing flashCard's contents
        FlashCardDisplay node = new FlashCardDisplay(cardText);

        // display the Node in the Gui
        display(node);
    }

    public static void renderBack(FlashCard flashCard) {
        // todo: use functional interfaces, from renderCard?
        // get text to be rendered
        String cardText = flashCard.getBack();

        // generate Node representing flashCard's contents
        FlashCardDisplay node = new FlashCardDisplay(cardText);

        // display the Node in the Gui
        display(node);
    }

    // temporary method to render FlashCardDisplay without using FlashCard class
    static void renderCard(String cardText) {
        FlashCardDisplay node = new FlashCardDisplay(cardText);
        display(node);
    }

    static void display(Node node) {
        // get primary display area of MainWindow
        GridPane windowContents = getMainWindow().getWindowContents(); // todo: check coding standards?
        GridPane.setConstraints(node, 0,0);

        // remove anything currently in the display area
        windowContents.getChildren().clear();

        // show the Node in the display area
        windowContents.getChildren().add(node);
    }

    static void setTitle(String title) {
        getMainWindow().setTitle(title);
    }
}