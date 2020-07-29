package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    public static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox[script]");
            System.exit(64)
        } else if (args.length == 1) {
            // Takes command line argument and runs it
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    // Reading file from command line using path to file given
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) {
            System.exit(65);
        }
    }

    // Getting prompts from command line
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();

            // When crtl+D is entered the loop will break
            if (line == null) {
                break;
            }
            run(line);
            // Reset error flag after execution
            hadError = false
        }
    }
    // Running actual file
    private static run(String source) {
        Scanner input = new Scanner(source);
        // Scanning expressions for parsing
        List<Token> tokens = input.scanTokens();
        // Just printing for now **TAKE OUT LATER**
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // Generates error found
    public static void error(int line, String message) {
        report(line, "", message);
    }

    // Error message reporting function
    private static void report(int line, String where, String message) {
        // Alerts user to syntax error on given line
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        // Set error flag to true
        hadError = true;
    }
}