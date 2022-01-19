package hangman;

import java.io.*;
import java.util.*;

public class EvilHangman {

    public static void main(String[] args) {

        // error checking command line parameters
        String dictionaryFileName = "";
        int wordLength = 0;
        int numGuesses = 0;

        // user must provide 3 arguments
        if (args.length == 3) {
            // dictionary error checking done later
            dictionaryFileName = args[0];
            // make sure user enters integers
            try {
                wordLength = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                System.err.println("Please enter an integer word length.");
            }
            try {
                numGuesses = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                System.err.println("Please enter an integer guess number.");
            }
        }
        else {
            System.err.println("Please enter the proper number of arguments.");
        }

        // wordLength must be >= 2, numGuesses must be >= 1
        if (wordLength < 2) {
            System.err.println("Please enter a word length of 2 or greater.");
        }

        if (numGuesses < 1) {
            System.err.println("Please enter a guess number of 1 or greater.");
        }

        // after error checking command line arguments, try to start game
        File file = new File(dictionaryFileName);
        IEvilHangmanGame game = new EvilHangmanGame();

        try {
            game.startGame(file, wordLength);
        }
        catch (IOException e) {
            System.err.println("There was an error reading in your file.");
        }
        catch (EmptyDictionaryException e) {
            System.err.println("The dictionary does not contain any words of the provided word length.");
        }

        // if the game successfully starts
        // initialize some values needed for output
        Set<String> possibleWords = new HashSet<>();
        char guess = 0;
        String userInput = "";
        StringBuilder wordSoFar = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            wordSoFar.append("-");
        }

        while (numGuesses > 0) {

            // output number of remaining guesses and guessed letters
            System.out.println("You have " + numGuesses + " guesses left");
            System.out.print("Used letters:");
            SortedSet<Character> guessedLetters = game.getGuessedLetters();
            for (char letter : guessedLetters) {
                System.out.print(" " + letter);
            }
            System.out.print("\n");

            // output the word they've guessed so far (if it's the first time, it will output all '-')
            System.out.print("Word: " + wordSoFar + "\n");

            // read in user's guess
            System.out.print("Enter guess: ");
            Scanner scanner = new Scanner(System.in);
            userInput = scanner.nextLine();
            // if the user enters more than one character, reject
            while (userInput.length() != 1) {
                System.out.println("Invalid input! Enter guess: ");
                userInput = scanner.nextLine();
            }
            // if the character is not an upper or lower case letter, reject
            while (!Character.isLetter(userInput.charAt(0)) || Character.isWhitespace(userInput.charAt(0))) {
                System.out.print("Invalid input! Enter guess: ");
                userInput = scanner.nextLine();
            }

            // now that you know the input is valid, make the guess
            guess = Character.toLowerCase(userInput.charAt(0));
            try {
                possibleWords = game.makeGuess(guess);
            }
            catch (GuessAlreadyMadeException e) {
                System.err.println("Oops! You already guessed that letter!");
            }

            // now that you have the possible words remaining, if the words don't have the user's guess
            String inPossibleWords = possibleWords.iterator().next();
            if (!inPossibleWords.contains(Character.toString(guess))) {
                System.out.println("Sorry, there are no " + guess + "'s\n\n");
                numGuesses--;
            }
            // else, if the word does contain the user's guess
            else {
                int charCount = 0;
                for (int i = 0; i < inPossibleWords.length(); i++) {
                    if (inPossibleWords.charAt(i) == guess) {
                        charCount++;
                    }
                }
                if (charCount > 1) {
                    System.out.println("Yes, there are " + charCount + " " + guess + "'s\n\n");
                }
                else {
                    System.out.println("Yes, there is " + charCount + " " + guess + "\n\n");
                }
            }

            // update wordSoFar if necessary
            for (int i = 0; i < inPossibleWords.length(); i++) {
                if (inPossibleWords.charAt(i) == guess) {
                    wordSoFar.replace(i, i+1, Character.toString(guess));
                }
            }

            // if wordSoFar has no more '-', the user has won
            if (!wordSoFar.toString().contains("-")) {
                System.out.println("You win! You guessed the word: " + wordSoFar);
                break;
            }

            // once numGuesses reaches zero, the user loses
            if (numGuesses == 0) {
                System.out.println("You lose!");
                System.out.println("The word was: " + inPossibleWords);
            }

        }

    }


}
