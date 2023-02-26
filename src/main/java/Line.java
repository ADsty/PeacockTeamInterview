import java.util.*;
import java.util.regex.Pattern;

/**
 * Class that represents a line of input file.
 * Contains methods to parse string line into java object
 * and to validate value of string line.
 */
public class Line {

    /**
     * List of words in the line, which of them contains information about its position and string value.
     */
    private final ArrayList<Word> words;

    /**
     * Creates new line object with given list of words.
     *
     * @param words
     *      list of words in the line.
     */
    public Line(ArrayList<Word> words) {
        this.words = words;
    }


    /**
     * Creates new line object from given string line.
     *
     * @param line
     *      is string line from input file or another source
     *      that was validated before.
     *
     * @return
     *      new line object.
     */
    public static Line fromString(String line) {
        String[] lineList = line.split(";");
        ArrayList<Word> resultWords = new ArrayList<>();
        for (int i = 0; i < lineList.length; i++) {
            Word word = new Word(i, lineList[i]);
            resultWords.add(word);
        }
        return new Line(resultWords);
    }

    /**
     * Method to get words of the line.
     * Due to performance optimizations, returns actual list of words and not a copy of it.
     *
     * @return
     *      list of words of the line.
     */
    public ArrayList<Word> getWords() {
        return words;
    }

    /**
     * Method to get size of list of words in the line.
     *
     * @return
     *      size of list of words.
     */
    public int size() {
        return words.size();
    }

    /**
     * Method to check if given line matches required pattern.
     * The pattern is "some digit";"some digit";..."some digit"
     *
     * @param line
     *      is string line that needs validation.
     *
     * @return
     *      true if line matches pattern and false if it's not.
     */
    public static boolean validateLine(String line) {
        String regex = "^([\"]\\d*[\"][;])*[\"]\\d*[\"]$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(line).matches();
    }

    /**
     * Checks equality of two lines.
     * If the references are not the same, then checks lists of words of the lines for their equality.
     *
     * @param o
     *      line with which to check equality.
     *
     * @return
     *      true if lines equal and false if they're not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return words.equals(line.getWords());
    }

    /**
     * Hashes list of words of the line.
     *
     * @return
     *      integer hashcode to use.
     */
    @Override
    public int hashCode() {
        return Objects.hash(words);
    }

    /**
     * Converts list of words into initial string line from which list was formed.
     *
     * @return
     *      initial string line.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Word word : words) {
            result.append(word.toString()).append(";");
        }
        return result.substring(0, result.length() - 1);
    }
}
