/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.csv;

import static org.apache.commons.csv.Constants.BACKSPACE;
import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.END_OF_STREAM;
import static org.apache.commons.csv.Constants.FF;
import static org.apache.commons.csv.Constants.LF;
import static org.apache.commons.csv.Constants.TAB;
import static org.apache.commons.csv.Constants.UNDEFINED;

import java.io.IOException;

/**
 * Abstract lexer class; contains common utility routines shared by lexers
 */
abstract class Lexer {

    private final Character delimiter;
    private final Character escape;
    private final Character encapsulator;
    private final Character commmentStart;

    final boolean ignoreSurroundingSpaces;
    final boolean ignoreEmptyLines;

    final CSVFormat format;

    /** The input stream */
    final ExtendedBufferedReader in;

    Lexer(final CSVFormat format, final ExtendedBufferedReader in) {
        this.format = format;
        this.in = in;
        this.delimiter = format.getDelimiter();
        this.escape = format.getEscape();
        this.encapsulator = format.getEncapsulator();
        this.commmentStart = format.getCommentStart();
        this.ignoreSurroundingSpaces = format.getIgnoreSurroundingSpaces();
        this.ignoreEmptyLines = format.getIgnoreEmptyLines();
    }

    int getLineNumber() {
        return in.getLineNumber();
    }

    // TODO escape handling needs more work
    int readEscape() throws IOException {
        // assume c is the escape char (normally a backslash)
        final int c = in.read();
        switch (c) {
        case 'r':
            return CR;
        case 'n':
            return LF;
        case 't':
            return TAB;
        case 'b':
            return BACKSPACE;
        case 'f':
            return FF;
        case END_OF_STREAM:
            throw new IOException("EOF whilst processing escape sequence");
        default:
            return c;
        }
    }

    void trimTrailingSpaces(final StringBuilder buffer) {
        int length = buffer.length();
        while (length > 0 && Character.isWhitespace(buffer.charAt(length - 1))) {
            length = length - 1;
        }
        if (length != buffer.length()) {
            buffer.setLength(length);
        }
    }

    /**
     * @return true if the given char is a whitespace character
     */
    boolean isWhitespace(final int c) {
        return c != format.getDelimiter() && Character.isWhitespace((char) c);
    }

    /**
     * Greedy - accepts \n, \r and \r\n This checker consumes silently the second control-character...
     *
     * @return true if the given character is a line-terminator
     */
    boolean isEndOfLine(int c) throws IOException {
        // check if we have \r\n...
        if (c == CR && in.lookAhead() == LF) {
            // note: does not change c outside of this method !!
            c = in.read();
        }
        return c == LF || c == CR;
    }

    /**
     * Checks if the current character represents the start of a line: a CR, LF or is at the start of the file.
     *
     * @param c
     * @return true if the character is at the start of a line.
     */
    boolean isStartOfLine(final int c) {
        return c == LF || c == CR || c == UNDEFINED;
    }

    /**
     * @return true if the given character indicates end of file
     */
    boolean isEndOfFile(final int c) {
        return c == END_OF_STREAM;
    }

    abstract Token nextToken(Token reusableToken) throws IOException;

    boolean isDelimiter(final int c) {
        return c == delimiter;
    }

    boolean isEscape(final int c) {
        return escape != null && c == escape;
    }

    boolean isEncapsulator(final int c) {
        return encapsulator != null && c == encapsulator;
    }

    boolean isCommentStart(final int c) {
        return commmentStart != null && c == commmentStart;
    }
}
