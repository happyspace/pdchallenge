package com.happyspace.pdchallenge;

import com.happyspace.pdchallenge.exceptions.FailedToCreateFileList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test File Processor
 *
 * Use PowerMock to Mock private methods.
 *
 * See:
 * https://github.com/jayway/powermock/wiki/MockPrivate
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileProcessor.class)
public class FileProcessorTest {

    /**
     * Standard Top-N for test.
     */
    private static int TOP_N = 10;

    private int processors;

    FileProcessor fileProcessor;

    @Before
    public void setUp(){
        processors = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Light testing of file transversal since this
     * should be well tested as part of the nio package.
     */
    @Test
    public void testCreateFileList() throws IOException {
        Path path = Paths.get("src/test/resources/one_file");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(1, paths, TOP_N, App.MAX_FILE_WALK);

        boolean created = fileProcessor.createFileList(paths.get(0));

        assertTrue(created);
        assertEquals(fileProcessor.paths.size(), 1);
    }

    @Test
    public void testCreateFileListThree() throws IOException {
        Path path = Paths.get("src/test/resources/three_files");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(1, paths, TOP_N, App.MAX_FILE_WALK);

        boolean created = fileProcessor.createFileList(paths.get(0));

        assertTrue(created);
        assertEquals(fileProcessor.paths.size(), 3);
    }

    @Test
    public void testSingleFileWords()
            throws IOException, InterruptedException, ExecutionException, FailedToCreateFileList {
        Path path = Paths.get("src/test/resources/numbers_one_file/numbers.txt");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(processors, paths, TOP_N, App.MAX_FILE_WALK);

        List<Map.Entry<String, Integer>> entries = fileProcessor.processFiles();

        assertEquals(entries.size(), 10);
    }

    @Test
    public void testSingleFileTopTwo()
            throws InterruptedException, ExecutionException, FailedToCreateFileList, IOException {
        Path path = Paths.get("src/test/resources/numbers_one_file/numbers.txt");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        int TOP_2 = 2;
        fileProcessor = new FileProcessor(processors, paths, TOP_2, App.MAX_FILE_WALK);

        List<Map.Entry<String, Integer>> entries = fileProcessor.processFiles();

        assertEquals(entries.size(), 2);
    }

    @Test
    public void testMultipleFileWords()
            throws IOException, InterruptedException, ExecutionException, FailedToCreateFileList {
        Path path = Paths.get("src/test/resources/numbers_three_files");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(processors, paths, TOP_N, App.MAX_FILE_WALK);

        List<Map.Entry<String, Integer>> entries = fileProcessor.processFiles();
        assertEquals(entries.get(0).getValue().longValue(), 30L);
    }

    @Test
    public void testAscendingNumberWords()
            throws IOException, InterruptedException, ExecutionException, FailedToCreateFileList {
        Path path = Paths.get("src/test/resources/numbers_ascending_by_count");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(processors, paths, TOP_N, App.MAX_FILE_WALK);

        List<Map.Entry<String, Integer>> entries = fileProcessor.processFiles();
        assertEquals(entries.get(0).getValue().longValue(), 10L);
    }

    @Test
    public void testAscendingNumberFiles()
            throws IOException, InterruptedException, ExecutionException, FailedToCreateFileList {
        Path path = Paths.get("src/test/resources/numbers_ascending_by_count_by_file");
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        fileProcessor = new FileProcessor(processors, paths, TOP_N, App.MAX_FILE_WALK);

        List<Map.Entry<String, Integer>> entries = fileProcessor.processFiles();
        assertEquals(entries.get(0).getValue().longValue(), 5L);
    }


}
