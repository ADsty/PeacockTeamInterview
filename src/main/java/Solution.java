import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.util.*;

/**
 * Main class of solution to given task.
 * Contains methods to process input file lines, group them and write formatted result in output file.
 */
public class Solution {

    /**
     * Input file name that would be parsed from command line.
     * Input file should be stored in the working directory, otherwise it may not be found.
     */
    @Argument(metaVar = "Input file", usage = "Sets input file path", required = true)
    private String inputFilePath;

    private final ArrayList<Pair<Integer, Integer>> groupsToMerge = new ArrayList<>();

    /**
     * Main method of the program,
     * parses command line with args4j, reads input file, process given lines and writes them in output file.
     *
     * @param args
     *      arguments of command line.
     */
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.parseCommandLine(args);
        HashSet<Line> lines = solution.readInputFile();
        if (lines == null) return;
        ArrayList<Pair<Line, Integer>> markedLines = solution.markLines(lines);
        HashMap<Integer, ArrayList<Line>> groups = solution.groupMarkedLines(markedLines);
        solution.writeToOutputFile(groups);
    }

    /**
     * Parses command line to get the name of input file.
     *
     * @param args
     *      arguments of command line given through main method.
     */
    private void parseCommandLine(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    /**
     * Reads input file, parses it and verifies if given line is valid or not.
     *
     * @return
     *      list of parsed and verified lines of input file,
     *      each of them contains list of words with their position and string value.
     */
    private HashSet<Line> readInputFile() {
        if (inputFilePath == null) return null;
        HashSet<Line> resultLines = new HashSet<>();
        try {
            File inputFile = new File(System.getProperty("user.dir") + "\\" + inputFilePath);
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                Line line = Line.fromString(nextLine);
                if (Line.validateLine(line)) {
                    resultLines.add(line);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Input file was not found.");
        }
        return resultLines;
    }


    /**
     * Marks lines with group indexes.
     * Due to performance, groups represented as integer indexes.
     * Also, because of performance optimizations,
     * this method uses word -> group index map to make it fast to find specific word in all the groups.
     * To solve the problem where multiple groups should be merged when new line connects them together,
     * declared optimization map is used to find such groups and mark them for merge and then process it.
     *
     * @param lines
     *      list of parsed and verified lines.
     *
     * @return
     *      map, where key is serial index of group and value is a list of lines which belongs to the group.
     */
    private ArrayList<Pair<Line, Integer>> markLines(HashSet<Line> lines) {
        int lastGroupIndex = 0;
        ArrayList<Pair<Line, Integer>> markedLines = new ArrayList<>();
        HashMap<Word, Integer> wordsToGroupsMap = new HashMap<>();
        outerLoop:
        for (Line line : lines) {
            for (Word word : line.getWords()) {
                if (word.isEmpty()) {
                    continue;
                }
                Integer groupIndex = wordsToGroupsMap.get(word);
                if (groupIndex != null) {
                    checkForMarkDiffs(groupIndex, line, wordsToGroupsMap, groupsToMerge);
                    markedLines.add(new Pair<>(line, groupIndex));
                    continue outerLoop;
                }
            }
            markWithNewGroup(line, wordsToGroupsMap, markedLines, lastGroupIndex);
            lastGroupIndex++;
        }
        return markedLines;
    }

    /**
     * Checks if there are groups that should be merged
     * after line processing and adds them to specialized map.
     *
     * @param groupIndex
     *      index of group, which should contain current line.
     *
     * @param line
     *      current processing line.
     *
     * @param wordsToGroupsMap
     *      optimization word -> group index map.
     *
     * @param groupsToMerge
     *      list of pairs of groups that should be merged after processing the lines.
     */
    private void checkForMarkDiffs(Integer groupIndex, Line line, HashMap<Word, Integer> wordsToGroupsMap,
                                   ArrayList<Pair<Integer, Integer>> groupsToMerge) {
        for (Word word : line.getWords()) {
            if (!word.isEmpty() && wordsToGroupsMap.containsKey(word)) {
                Integer groupIndexCandidate = wordsToGroupsMap.get(word);
                if (groupIndexCandidate != null && !groupIndexCandidate.equals(groupIndex)) {
                    groupsToMerge.add(new Pair<>(groupIndexCandidate, groupIndex));
                }
            }
            wordsToGroupsMap.put(word, groupIndex);
        }
    }

    /**
     * Marks current line with new group index.
     *
     * @param line
     *      current processing line.
     *
     * @param wordsToGroupsMap
     *      optimization word -> group index map.
     *
     * @param markedLines
     *      list of already processed lines.
     *
     * @param lastGroupIndex
     *      index of currently last group.
     */
    private void markWithNewGroup(Line line, HashMap<Word, Integer> wordsToGroupsMap,
                                  ArrayList<Pair<Line, Integer>> markedLines, int lastGroupIndex) {
        markedLines.add(new Pair<>(line, lastGroupIndex + 1));
        for (Word word : line.getWords()) {
            wordsToGroupsMap.put(word, lastGroupIndex + 1);
        }
    }

    /**
     * Replaces group index of lines of second in pair groups for group index of first in pair groups.
     *
     * @param groupsToMerge
     *      list of pairs of groups that should be merged.
     *
     * @param groups
     *      list of formed groups.
     */
    private void mergeGroups(ArrayList<Pair<Integer, Integer>> groupsToMerge,
                             HashMap<Integer, ArrayList<Line>> groups) {
        for (Pair<Integer, Integer> pair : groupsToMerge) {
            Integer groupToMergeIndex = pair.getSecond();
            Integer mergedGroupIndex = pair.getFirst();
            ArrayList<Line> groupToMerge = groups.get(groupToMergeIndex);
            ArrayList<Line> mergedGroup = groups.get(mergedGroupIndex);
            groupToMerge.addAll(mergedGroup);
            mergedGroup.clear();
        }
    }

    /**
     * Transforms list of processed lines into map that represents actual groups.
     *
     * @param markedLines
     *      list of processed lines.
     *
     * @return
     *      map, where key is serial index of group and value is a list of lines which belongs to the group.
     */
    private HashMap<Integer, ArrayList<Line>> groupMarkedLines(ArrayList<Pair<Line, Integer>> markedLines) {
        HashMap<Integer, ArrayList<Line>> groups = new HashMap<>();
        for (Pair<Line, Integer> groupedLine : markedLines) {
            ArrayList<Line> group = groups.get(groupedLine.getSecond());
            if (group == null) {
                ArrayList<Line> newGroup = new ArrayList<>();
                newGroup.add(groupedLine.getFirst());
                groups.put(groupedLine.getSecond(), newGroup);
            } else {
                group.add(groupedLine.getFirst());
            }
        }
        mergeGroups(groupsToMerge, groups);
        return groups;
    }

    /**
     * Formats processed data to match the task and writes result in output file.
     *
     * @param groups
     *      map that represents actual groups, where key is group index and value is list of lines of the group.
     */
    private void writeToOutputFile(HashMap<Integer, ArrayList<Line>> groups) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("result.txt"), "windows-1251"))) {
            int groupsWithTwoOrMoreElements = 0;
            HashMap<Integer, ArrayList<ArrayList<Line>>> groupsSorted = new HashMap<>();
            for (Integer groupIndex : groups.keySet()) {
                ArrayList<Line> group = groups.get(groupIndex);
                int size = group.size();
                if (size > 1) {
                    groupsWithTwoOrMoreElements++;
                }
                ArrayList<ArrayList<Line>> groupsOfThisSize = groupsSorted.computeIfAbsent(size, k -> new ArrayList<>());
                groupsOfThisSize.add(group);
            }
            ArrayList<Integer> groupSizes = new ArrayList<>(groupsSorted.keySet());
            Collections.sort(groupSizes);
            writer.write("Количество групп с более чем одним элементом : " + groupsWithTwoOrMoreElements + "\n");
            int groupCount = 1;
            for (int i = groupSizes.size() - 1; i >= 0; i--) {
                for (ArrayList<Line> group : groupsSorted.get(groupSizes.get(i))) {
                    writer.write("Группа " + groupCount + " \n");
                    for (Line line : group) {
                        writer.write(line.toString() + "\n");
                    }
                    groupCount++;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Writing result in file was incomplete.");
        }
    }

}
