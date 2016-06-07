package com.happyspace.pdchallenge;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Unit test for App.
 * Uses System Rules to evaluate parameters and System.exit.
 */

@RunWith(MockitoJUnitRunner.class)
public class AppTest
{
    /**
     * Standard Top-N for test.
     */
    private static int TOP_N = 10;

    /**
     * Rule for system exit.
     */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    /**
     * Rule for system out.
     */
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();


    /**
     * Test message and exit for no arguments.
     */
    @Test
    public void testNoArguments(){
        systemOutRule.clearLog();
        exit.expectSystemExitWithStatus(-1);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(Messages.INVALID_ARGUMENTS, systemOutRule.getLog().trim());
            }
        });
        App.main(new String[]{});
    }

    /**
     * Test message and exit for too many arguments.
     */
    @Test
    public void testOneArgument(){
        systemOutRule.clearLog();
        exit.expectSystemExitWithStatus(-1);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(Messages.INVALID_ARGUMENTS, systemOutRule.getLog().trim());
            }
        });
        App.main(new String[]{"1"});
    }

    /**
     * Test message and exit for non integer.
     */
    @Test
    public void testNonInteger()
    {
        systemOutRule.clearLog();
        exit.expectSystemExitWithStatus(-1);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(Messages.NON_INTEGER, systemOutRule.getLog().trim());
            }
        });
        App.main(new String[]{"Moo", "/moo"});
    }


    /**
     * Test message and exit for large top N.
     */
    @Test
    public void testExceedsMaxNumber() {
        int tooBig = App.MAX_WORDS_MAX + 1;

        systemOutRule.clearLog();
        exit.expectSystemExitWithStatus(-1);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(Messages.EXCEEDS_MAX_TOP_N, systemOutRule.getLog().trim());
            }
        });
        App.main(new String[]{Integer.toString(tooBig), "/moo"});
    }

    /**
     * Test message and exit for non-existent path.
     */
    @Test
    public void testNonExistentPath() {
        systemOutRule.clearLog();
        exit.expectSystemExitWithStatus(-1);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(Messages.PATH_DOES_NOT_EXIST, systemOutRule.getLog().trim());
            }
        });
        App.main(new String[]{Integer.toString(AppTest.TOP_N), "/moo/moo/moo"});
    }


    /**
     * Test reading a single file.
     */
    @Test
    public void testSingleFileWordsOutput(){
        systemOutRule.clearLog();
        App.main(new String[]{String.valueOf(TOP_N), "src/test/resources/numbers_one_file/numbers.txt"});

        String log = systemOutRule.getLog();
        int header = log.indexOf(":");
        String substring = log.substring(0, header);
        String format = String.format(Messages.WORDS_HEADER_FORMAT, TOP_N);
        assertEquals(substring, format.substring(0,header));
    }

    /**
     * Test reading a file and a directory.
     */
    @Test
    public void testDirectoryAndFileInput(){
        systemOutRule.clearLog();
        App.main(new String[]{
                String.valueOf(TOP_N),
                "src/test/resources/numbers_one_file/numbers.txt",
                "src/test/resources/numbers_three_files"
        });
        String log = systemOutRule.getLog();
        int index = log.indexOf("40");
        assertNotEquals(index, "-1");
    }

}
