import java.util.Objects;

/**
 * Class that represents word of some line.
 * It contains position of such word and its string value.
 * Due to the task, values as "" considered empty.
 */
public class Word {
    private final int position;
    private final String value;

    public Word(int position, String value) {
        this.position = position;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value.equals("\"\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return position == word.getPosition() && value.equals(word.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, value);
    }

    @Override
    public String toString() {
        return value;
    }
}
