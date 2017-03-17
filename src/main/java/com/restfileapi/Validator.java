package com.restfileapi;

import com.restfileapi.entries.FilePath;

import java.util.regex.Pattern;

/**
 * Created by Tatyana on 16.03.2017.
 */
public class Validator {

    private static Pattern fileNamePattern = Pattern.compile(
            "# Match a valid Windows filename (unspecified file system).          \n" +
                    "^                                # Anchor to start of string.        \n" +
                    "(?!                              # Assert filename is not: CON, PRN, \n" +
                    "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                    "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                    "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                    "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                    "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                    "  $                              # and end of string                 \n" +
                    ")                                # End negative lookahead assertion. \n" +
                    "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                    "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                    "$                                # Anchor to end of string.            ",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);

    private boolean isValidFilename(String text) {
        return fileNamePattern.matcher(text).matches();
    }

    public static boolean validate(FilePath filePath) {
        if (filePath.getPath() == null) {
            return false;
        }
        if (filePath.getPath().length()>255) {
            return false;
        }
        return fileNamePattern.matcher(filePath.getPath()).matches();
    }

}
