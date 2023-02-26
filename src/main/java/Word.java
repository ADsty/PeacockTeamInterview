import java.util.Objects;

/**
 * Class that represents word of some line.
 * It contains position of such word and its string value.
 * Due to the task, values as "" considered empty.
 */
public class Word {

    /**
     * Position of the word in parsed line.
     */
    private final int position;

    /**
     * String value of the word, which matches the pattern "some digit" .
     */
    private final String value;

    /**
     * Creates new word to use in solution algorithm.
     *
     * @param position
     *      position of word in the line.
     *
     * @param value
     *      string value of the word.
     */
    public Word(int position, String value) {
        this.position = position;
        this.value = value;
    }

    /**
     * Method to get position of the word in line.
     *
     * @return
     *      integer representation of position of the word.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Method to get string value of the word.
     *
     * @return
     *      string form of the word.
     */
    public String getValue() {
        return value;
    }

    /**
     * Checks if string value of the word matches "", which in this task considered empty.
     *
     * @return
     *      true if value matches empty value and false if it's not.
     */
    public boolean isEmpty() {
        return value.equals("\"\"");
    }

    /**
     * Checks equality of two words.
     * If reference is not the same, then checks if positions and string values of two words are equal.
     *
     * @param o
     *      word with which to check equality.
     *
     * @return
     *      true if positions and values of words are equal and false if they're not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return position == word.getPosition() && value.equals(word.getValue());
    }

    /**
     * Hashes position and string value of the word.
     *
     * @return
     *      integer hashcode to use.
     */
    @Override
    public int hashCode() {
        return Objects.hash(position, value);
    }

    /**
     * Method to convert word object into string.
     * For now, it equals to getValue method.
     *
     * @return
     *      string representation of word object.
     */
    @Override
    public String toString() {
        return value;
    }
}
