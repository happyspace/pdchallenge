package com.happyspace.pdchallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * A task for counting word occurrences in text.
 * Uses the Callable interface to return results.
 *
 * Basically a simple reducer in a map reduce.
 */
public class WordCountTask implements Callable<List<Map.Entry<String , Integer>>> {

    private final Path filePath;
    private final int topN;

    /**
     *
     * @param filePath A path to a file.
     * @param topN How many words to return.
     */
    public WordCountTask(Path filePath, int topN) {
        this.filePath = filePath;
        this.topN = topN;
    }

    /**
     *
     * @return a list of words in descending order
     * limited to the 'topN' values
     * @throws Exception
     */
    @Override
    public List<Map.Entry<String , Integer>> call() throws Exception {
        return countWords();
    }

    /**
     *
     * @return a list of words in descending order
     * limited to the 'topN' values
     * @throws IOException
     */
    private List<Map.Entry<String , Integer>> countWords() throws IOException {
        List<Map.Entry<String, Integer>> results;
        Map<String, Integer> counts = new HashMap<>();
        // try with resources.
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = line.split(App.WHITE_SPACE_REGEX);
                for (String word : strings) {
                    if (counts.containsKey(word)) {
                        Integer count = counts.get(word) + 1;
                        counts.put(word, count);
                    } else {
                        counts.put(word, 1);
                    }
                }
            }

            results = new ArrayList<>(counts.entrySet());
            Collections.sort(results, new EntryComparator());
            if(results.size() > topN){
                results = results.subList(0, topN);
            }
        }
        return results;
    }
}
