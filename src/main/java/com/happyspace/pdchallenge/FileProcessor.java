package com.happyspace.pdchallenge;

import com.happyspace.pdchallenge.exceptions.FailedToCreateFileList;
import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

/**
 * Processes files found on a given system path.
 */
@NotThreadSafe
public class FileProcessor {
    /**
     * A list of file paths derived from a given path.
     */
    final List<Path> paths = new ArrayList<>();

    /**
     * Number of workers.
     */
    private final int numberOfWorkers;

    /**
     * A file paths that represent the starting point for processing.
     */
    private final ArrayList<Path> start;

    /**
     * The number of words to return.
     */
    private final int topN;

    /**
     * The max depth to traverse.
     */
    private final int maxDepth;

    /**
     *
     * @param numberOfWorkers the number of workers.
     * @param start list of valid paths.
     * @param topN how many items to record.
     * @param maxDepth traversal limit for directories
     */
    FileProcessor(int numberOfWorkers, ArrayList<Path> start, int topN, int maxDepth){
        this.numberOfWorkers = numberOfWorkers;
        this.start = start;
        this.topN = topN;

        this.maxDepth = maxDepth;
    }

    /**
     * @return the reduced list of results for all files.
     */
    public List<Map.Entry<String, Integer>> processFiles()
            throws FailedToCreateFileList, ExecutionException, InterruptedException {
        List<Map.Entry<String, Integer>> results;

        for (Path path : start) {
            if(!createFileList(path)) {
                throw new FailedToCreateFileList();
            }
        }
        return process();
    }

    /**
     * Protected method that creates a list of files to process.
     * @return whether or not the file system was traversable.
     */
    protected boolean createFileList(Path start) {
        boolean success = true;
        Set<FileVisitOption> options = EnumSet.noneOf(FileVisitOption.class);
        try {
            Files.walkFileTree(start, options, this.maxDepth, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // is this a regular file.
                    if (attrs.isRegularFile()) {
                        paths.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex){
            success = false;
        }

        return success;
    }

    /**
     * A protected method that maps file paths to tasks.
     *
     * @return Result of the reduce.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected  List<Map.Entry<String, Integer>> process()
            throws InterruptedException, ExecutionException {
        List<Map.Entry<String, Integer>> results = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();

        ExecutorService exec = Executors.newFixedThreadPool(numberOfWorkers);
        List<Callable<List<Map.Entry<String , Integer>>>> tasks = new ArrayList<>();
        // create tasks
        for (Path path : paths ) {
            tasks.add(new WordCountTask(path, topN));
        }

        List<Future<List<Map.Entry<String , Integer>>>> futures = exec.invokeAll(tasks);

        for(Future<List<Map.Entry<String , Integer>>> future : futures){
            List<Map.Entry<String, Integer>> entries = future.get();
            for(Map.Entry<String , Integer> entry : entries) {
                if(counts.containsKey(entry.getKey())){
                    Integer value = counts.get(entry.getKey());
                    counts.put(entry.getKey(), value + entry.getValue());
                }
                else {
                    counts.put(entry.getKey(), entry.getValue());
                }
            }
        }

        exec.isShutdown();

        results.addAll(counts.entrySet());
        Collections.sort(results, new EntryComparator());
        if(results.size() > topN) {
            results = results.subList(0, topN);
        }

        return results;
    }
}
