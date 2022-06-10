package com.craftinginterpreters.estin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.estin.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current= 0;
    private int line = 0;
    
    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAnEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
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
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment that goes until the end of the line
                    while (peek() != '\n' && !isAnEnd()) advance();
                    else {
                        addToken(SLASH);
                    }
                }
            case ' ':
            case '\r':
            case '\t':
                // Ignore white spaces
                break;
            case '\n':
                line++;
                break;
            default:
                Estin.error(line, "unexpected char");
                break;
        }
    }

    private boolean match(char expected) {
        if (isAnEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current ++;
        return true;
    }

    // do not consume the current char but lookahead
    private char advance() {
        if (isAnEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAnEnd() {
        return current >= source.length();
    }

    private char charAdvance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type , null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
