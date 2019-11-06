package dream.fcard.logic.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dream.fcard.logic.stats.Session;
import dream.fcard.logic.stats.UserStats;
import dream.fcard.model.Deck;
import dream.fcard.model.TestCase;
import dream.fcard.model.cards.FlashCard;
import dream.fcard.model.cards.FrontBackCard;
import dream.fcard.model.cards.JavaCard;
import dream.fcard.model.cards.JavascriptCard;
import dream.fcard.model.cards.MultipleChoiceCard;
import dream.fcard.util.DateTimeUtil;
import dream.fcard.util.FileReadWrite;
import dream.fcard.util.json.JsonParser;
import dream.fcard.util.json.exceptions.JsonFormatException;
import dream.fcard.util.json.exceptions.JsonWrongValueException;
import dream.fcard.util.json.jsontypes.JsonObject;
import dream.fcard.util.json.jsontypes.JsonValue;

/**
 * Interface to managing storage for the program.
 */
public class StorageManager {

    private static boolean isRootResolved = false;

    private static String root;
    private static String decksSubDir = "./decks";
    private static String statsSubDir = "./stats";
    private static String statsFileName = "stats.json";
    private static String statsFileFullPath;
    private static String codeSubDir = "./code";

    /**
     * Determine root directory of the application:
     *  main for intellij project,
     *  jar directory when running as jar,
     *  current executed directory for default
     * following resolution root is resolved to `root/data/`
     */
    public static void resolveRoot() {
        if (isRootResolved) {
            return;
        }
        URL thisClassUrl = StorageManager.class.getResource("StorageManager.class");

        switch (thisClassUrl.getProtocol()) {
        case "file":
            try {
                String platformIndependentPath = Paths.get(StorageManager.class
                        .getResource("StorageManager.class").toURI()).toString();
                root = FileReadWrite.resolve(platformIndependentPath, "../../../../../../../../../");
            } catch (URISyntaxException i) {
                System.out.println("error");
                System.exit(-1);
            }
            break;
        case "jar":
            try {
                root = FileReadWrite.resolve(
                        new File(StorageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                                .getPath(), "../");
            } catch (URISyntaxException e) {
                System.out.println("jar is broken as unable to resolve path");
                System.exit(-1);
            }
            break;
        default:
            root = System.getProperty("user.dir");
        }
        root = FileReadWrite.resolve(root, "./data");
        resolveStatsPath();
        isRootResolved = true;
    }

    /**
     * User provide directory to use for storage
     *
     * @param path path to new directory for storage
     */
    public static void provideRoot(String path) {
        root = path;
        resolveStatsPath();
        isRootResolved = true;
    }

    /**
     * Returns value of current root.
     *
     * @return root directory
     */
    public static String getRoot() {
        return root;
    }

    // ROOT CODE --------------------------------------------------------------

    /**
     * Write a deck into decks storage.
     *
     * @param deck deck object to write
     */
    public static void writeDeck(Deck deck) {
        resolveRoot();
        String path = FileReadWrite.resolve(root, decksSubDir + "/" + deck.getDeckName() + ".json");
        FileReadWrite.write(path, deck.toJson().toString());
    }

    /**
     * Delete a deck.
     * @param deck the deck to remove.
     */
    public static void deleteDeck(Deck deck) {
        resolveRoot();
        String path = FileReadWrite.resolve(root, decksSubDir + "/" + deck.getName() + ".json");
        FileReadWrite.delete(path);
    }

    /**
     * Load all decks in storage.
     *
     * @return ArrayList of decks in storage
     */
    public static ArrayList<Deck> loadDecks() {
        resolveRoot();
        String path = FileReadWrite.resolve(root, decksSubDir);

        if (!FileReadWrite.fileExists(path)) {
            return new ArrayList<>();
        }

        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk.filter(Files::isRegularFile)
                    .map(x -> loadDeck(x.toString()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads a single deck.
     *
     * @param filePath Must be valid existing filepath to a deck json file.
     * @return deck object
     */
    public static Deck loadDeck(String filePath) {
        try {
            return parseDeckJsonFile(FileReadWrite.read(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("FILE DOES NOT EXIST");
        }
        return null;
    }

    /**
     * Parse input as a json deck string.
     *
     * @param input json deck string
     * @return deck object
     */
    private static Deck parseDeckJsonFile(String input) {
        try {
            try {
                ArrayList<FlashCard> cards = new ArrayList<>();
                JsonObject deckJson = JsonParser.parseJsonInput(input).getObject();
                for (JsonValue x : deckJson.get(Schema.DECK_CARDS).getArray()) {
                    JsonObject cardJson = x.getObject();
                    FlashCard card = null;
                    switch (cardJson.get(Schema.TYPE_FIELD).getString()) {
                    case Schema.FRONT_BACK_TYPE:
                        card = new FrontBackCard(
                                cardJson.get(Schema.FRONT_FIELD).getString(),
                                cardJson.get(Schema.BACK_FIELD).getString());
                        break;
                    case Schema.JAVASCRIPT_TYPE:
                        card = new JavascriptCard(
                                cardJson.get(Schema.FRONT_FIELD).getString(),
                                cardJson.get(Schema.BACK_FIELD).getString());
                        break;
                    case Schema.MULTIPLE_CHOICE_TYPE:
                        ArrayList<String> choices = new ArrayList<>();
                        for (JsonValue choiceJson : cardJson.get(Schema.CHOICES_FIELD).getArray()) {
                            choices.add(choiceJson.getString());
                        }
                        card = new MultipleChoiceCard(
                                cardJson.get(Schema.FRONT_FIELD).getString(),
                                cardJson.get(Schema.BACK_FIELD).getString(),
                                choices);
                        break;
                    case Schema.JAVA_TYPE:
                        ArrayList<TestCase> testcases = new ArrayList<>();
                        for (JsonValue caseJson : cardJson.get(Schema.TEST_CASES_FIELD).getArray()) {
                            testcases.add(new TestCase(
                                    caseJson.getObject().get(Schema.TEST_CASE_INPUT).getString(),
                                    caseJson.getObject().get(Schema.TEST_CASE_OUTPUT).getString()));
                        }
                        card = new JavaCard(cardJson.get(Schema.FRONT_FIELD).getString(), testcases);
                        break;
                    default:
                        System.out.println("Unexpected card type, but silently continues");
                        continue;
                    }
                    cards.add(card);
                }
                return new Deck(cards, deckJson.get(Schema.DECK_NAME).getString());
            } catch (JsonWrongValueException e1) {
                System.out.println("JSON file wrong schema");
            }
        } catch (JsonFormatException e2) {
            System.out.println("JSON file has errors\n" + e2.getMessage());
        }
        return null;
    }

    /**
     * Overwrite all files in the subdirectory with the given set of decks.
     *
     * @param decks an array list of decks
     */
    public static void saveAll(ArrayList<Deck> decks) {
        resolveRoot();
        for (Deck d : decks) {
            writeDeck(d);
        }
    }

    // DECK CODE --------------------------------------------------------------

    /**
     * Resolve path to stats file.
     */
    public static void resolveStatsPath() {
        statsFileFullPath = FileReadWrite.resolve(root, statsSubDir + "/" + statsFileName);
    }

    /**
     * Save stats data.
     */
    public static void saveStats(UserStats userStats) {
        resolveRoot();
        FileReadWrite.write(statsFileFullPath, userStats.getSessionList().toJson().toString());
    }

    /**
     * Initialize and load stats data if any.
     */
    public static void loadStats() {
        resolveRoot();
        //Stats.getUserStats();
        try {
            ArrayList<Session> arr = new ArrayList<>();
            JsonValue statsJson = JsonParser.parseJsonInput(FileReadWrite.read(statsFileFullPath));
            for (JsonValue sessionJson : statsJson.getArray()) {
                JsonObject sessionJsonObj = sessionJson.getObject();
                Session session = new Session(
                        DateTimeUtil.getDateTimeFromJson(sessionJsonObj.get(Schema.SESSION_START).getObject()),
                        DateTimeUtil.getDateTimeFromJson(sessionJsonObj.get(Schema.SESSION_END).getObject()));
                //session.setScore(sessionJsonObj.get(Schema.SESSION_SCORE).getInt());
                // todo: only need setScore for SessionLists containing DeckSession objects. @AHaliq
                arr.add(session);
            }
            //Stats.setSessionList(new SessionList(arr));
            // load login session
        } catch (FileNotFoundException e) {
            System.out.println("STATS FILE DOES NOT EXIST");
        } catch (JsonFormatException e) {
            System.out.println("STATS JSON IS ILL FORMED\n" + e.getMessage());
        } catch (JsonWrongValueException e) {
            System.out.println("UNEXPECTED JSON FORMAT FOR STATS\n" + e.getMessage());
        }
    }

    // STATS CODE -------------------------------------------------------------

    /**
     * Write a code file to the code sub directory in root.
     * @param fileName  name of file with extensions
     * @param code      string content to be written
     */
    public static void writeCode(String fileName, String code) {
        resolveRoot();
        String path = FileReadWrite.resolve(root, codeSubDir + "/" + fileName);
        FileReadWrite.write(path, code);
    }

    /**
     * Get the path to the code file assuming its in root code subdirectory.
     * @param fileName  name of file
     * @return          full path to file
     */
    public static String getCodePath(String fileName) {
        resolveRoot();
        return FileReadWrite.resolve(root, codeSubDir + "/" + fileName);
    }

    // CODE EXECUTION CODE ----------------------------------------------------
}
