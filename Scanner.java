package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {
    // Storing source code in string
    private final String source;
    // List for holding tokens of the source code
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    // Keeps track of line number of current
    private int line = 1;

    public Scanner (String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // Function returns true if we've ran through all the characters
    private boolean isAtEnd() {
        return current >= source.length;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                // When newline character is encountered, increment line number
                line++;
                break;
            case '"': string(); break;
            default:
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    private void string() {
        // Keep scanning string until ending quote(") is found or end of file
        while (peek() != '"' && !isAtEnd()) {
            // For multi line strings increment line number
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        // Unterminated String
        if (isAtEnd()) {
            Lox.error(line, "Unterminated String.");
            return;
        }
        // get closing "
        advance();

        // Trim the surrounding quotes, start + 1 will be the first char and
        // current - 1 will be the last char
        String value = source.substring(start + 1, current - 1);
        addToken((STRING, value));
    }

    // Check for operators with multiple characters such as !=, >=, etc
    private boolean match(char expected) {
        if (isAtEnd()) {
            return False
        }
        if (source.charAt(current) != expected) {
            return False
        }
        current++;
        return true
    }
    // Used for looking ahead of current character until end of line
    private char peek() {
        if (isAtEnd()) {
            return '\0'
        }
        return source.charAt(current);
    }

    // Gets next character in source file and returns it
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    // Create new token for current lexeme
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    // Overloaded method for handling literal tokens
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}