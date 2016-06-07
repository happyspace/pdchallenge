package com.happyspace.pdchallenge;

import com.happyspace.pdchallenge.exceptions.FailedToCreateFileList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A program that reads a collection of files and prints the top N words
 * ordered by their occurrences across all files.
 *
 * Arguments
 *      An integer representing the top N value.
 *      A list of paths. If the path is a directory the directory is scanned.
 * Program invocation example:
 *      > max-words 5 /tmp /home/user/file.txt
 */
public class App 
{
    // prevent unbounded N words
    // would be configurable in c real application
    protected static final int MAX_WORDS_MAX = 100;
    // prevent unbounded file walk
    protected static final int MAX_FILE_WALK = 99;
    // regex
    protected static final String WHITE_SPACE_REGEX = "\\s+";
    
    public static void main( String[] args )
    {
        // for simplicity there must be at least two arguments
        if(args.length == 0 || args.length < 2) {
            System.out.println(Messages.INVALID_ARGUMENTS);
            System.exit(-1);
        }
        // check that first argument can be converted to an integer
        int maxWords = 0;
        try {
            maxWords = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            System.out.println(Messages.NON_INTEGER);

            System.exit(-1);
        }
        // check that max words value is with in an acceptable range.
        if (maxWords > App.MAX_WORDS_MAX) {
            System.out.println(Messages.EXCEEDS_MAX_TOP_N);

            System.exit(-1);
        }

        String[] p = Arrays.copyOfRange(args, 1, args.length);
        ArrayList<String> s_paths = new ArrayList<>(Arrays.asList(p));
        ArrayList<Path> paths = new ArrayList<>();

        // the paths must point to an existing file or directory.
        // Note that Files.exists() checks for readability.
        // File.exists() throws a SecurityException which is a runtime exception.

        // work around a very strange bug with jUnit and 'System Rules' trapping the exit
        // within a try catch block. may file a bug later if this is a 'new' issue.
        // code works as expected on execution of the program.
        // under test the system does not actually exit.
        boolean shouldExit = false;
        for (String s_path : s_paths) {
            Path path = Paths.get(s_path);
            try {
                if (! Files.exists(path)) {
                    shouldExit = true;
                    break;
                }
                else {
                    paths.add(path);
                }
            } catch (SecurityException ex) {
                shouldExit = true;
                break;
            }
        }
        if (shouldExit) {
            System.out.println(Messages.PATH_DOES_NOT_EXIST);
            System.exit(-1);
        }

        // find out how many cores are available
        int cores = Runtime.getRuntime().availableProcessors();
        // used to create threads. If there is one core there use main execution tread.
        if(cores != 1) {
            cores--;
        }
        // create a file processor
        FileProcessor processor = new FileProcessor(cores, paths, maxWords, MAX_FILE_WALK);

        try {
            // process files
            List<Map.Entry<String, Integer>> entries = processor.processFiles();
            // print results
            int mw = maxWords;
            if(entries.size() < mw) {
                mw = entries.size();
            }

            System.out.format(Messages.WORDS_HEADER_FORMAT, mw);
            for (Map.Entry<String, Integer> entry: entries){
                System.out.format(Messages.WORDS_ITEM_FORMAT,entry.getKey(), entry.getValue());
            }
        } catch (FailedToCreateFileList e) {
            System.out.println(Messages.EXECUTION_FAILED);
            System.exit(-1);
        } catch (ExecutionException e) {
            System.out.println(Messages.EXECUTION_FAILED);
            System.exit(-1);
        } catch (InterruptedException e) {
            System.out.println(Messages.EXECUTION_FAILED);
            System.exit(-1);
        }
    }
}
