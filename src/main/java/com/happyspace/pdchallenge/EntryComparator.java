package com.happyspace.pdchallenge;

import java.util.Comparator;
import java.util.Map;

/**
 * Entity Comparator.
 */
public class EntryComparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
    }
}
