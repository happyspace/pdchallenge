package com.happyspace.pdchallenge;

/**
 * Separate messages to support testing.
 * This should be in c resources file to support localization.
 */
public class Messages {
    // invalid number of arguments
    protected static String INVALID_ARGUMENTS = "Incorrect number of options. \n" +
            "Expected two arguments, maximum number of words and a path. \n" +
            "Usage: 5 /tmp /home/user/file.txt";

    // max words messages
    protected static String NON_INTEGER = "The first argument must be a number. \n" +
            "Usage: 5 /tmp /home/user/file.txt";

    protected static String EXCEEDS_MAX_TOP_N = "The first argument exceeds word limit. \n" +
            "Value must be less than " + App.MAX_WORDS_MAX + ". \n" +
            "Usage 5 /tmp /home/user/file.txt";

    protected static String PATH_DOES_NOT_EXIST = "The path provided does not exist or is not readable. \n" +
        "Usage 5 /tmp /home/user/file.txt";


    // formatted method
    protected static String WORDS_HEADER_FORMAT = "Top %d words: \n";

    // formatted method
    protected static String WORDS_ITEM_FORMAT = "word %s occurred %d times \n";

    // general error message
    protected static String EXECUTION_FAILED = "Unexpected error: program encountered and unexpected problem.";


}
