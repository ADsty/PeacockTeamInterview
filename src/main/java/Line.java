import java.util.*;
import java.util.regex.Pattern;

/**
 * Class that represents a line of input file.
 * Contains methods to parse string line into java object
 * and to validate value of string line.
 */
public class Line {
    private final ArrayList<Word> words;

    public Line(ArrayList<Word> words) {
        this.words = words;
    }

    public static Line fromString(String line) {
        String[] lineList = line.split(";");
        ArrayList<Word> resultWords = new ArrayList<>();
        for (int i = 0; i < lineList.length; i++) {
            Word word = new Word(i, lineList[i]);
            resultWords.add(word);
        }
        return new Line(resultWords);
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public static boolean checkCorrectness(String line) {
        String regex = "^([\"]\\d*[\"][;])*[\"]\\d*[\"]$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(line).matches();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Word word : words) {
            result.append(word.toString()).append(";");
        }
        return result.substring(0, result.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return words.equals(line.getWords());
    }

    @Override
    public int hashCode() {
        return Objects.hash(words);
    }
}
