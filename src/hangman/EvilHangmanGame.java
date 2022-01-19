package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private Set<String> wordSet;
    private SortedSet<Character> guessed;
    private Map<String, Set<String>> partition;

    public EvilHangmanGame() {
        guessed = new TreeSet<>();
        wordSet = new HashSet<>();
        partition = new HashMap<>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {

        // clear wordSet, guessed, and partition
        wordSet.clear();
        guessed.clear();
        partition.clear();

        // if the file doesn't exist, throw exception
        if (!dictionary.exists()) {
            throw new IOException();
        }

        // scan in words
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String in = scanner.next();
            // we're only interested if the length matches wordLength
            if (in.length() == wordLength) {
                // if there is a bad character in the word, throw exception
                for (int i = 0; i < in.length(); i++) {
                    char toCheck = in.charAt(i);
                    if (!Character.isLetter(toCheck)) {
                        throw new IOException();
                    }
                }
                // otherwise, add the word into wordSet
                String toAdd = in.toLowerCase();
                wordSet.add(toAdd);
            }
        }

        // if wordSet is empty, throw EmptyDictionaryException
        if (wordSet.isEmpty()) {
            throw new EmptyDictionaryException();
        }

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        // case insensitive
        guess = Character.toLowerCase(guess);

        // if the character has already been guessed, throw exception
        if (guessed.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }

        // if not, add it to the set of guessed characters
        guessed.add(guess);

        // clear partition, then partition the wordSet
        partition.clear();
        partition(guess);

        // set the current biggest set to the first set in partition
        Set<String> resultSet = partition.values().iterator().next();
        // for each set in partition, check to see if it has a larger size and make that the new resultSet if it does
        for (Set<String> toCheck : partition.values()) {
            if (toCheck.size() > resultSet.size()) {
                resultSet = toCheck;
            }
            // if the two sets are the same size, choose the one in which the letter does not appear at all
            else if (toCheck.size() == resultSet.size()) {

                // does the guessed letter appear in a word from resultSet? if so, how many times?
                boolean appearsInResult = false;
                int resultCount = 0;
                String resultWord = resultSet.iterator().next();
                for (int i = 0; i < resultWord.length(); i++) {
                    if (resultWord.charAt(i) == guess) {
                        appearsInResult = true;
                        resultCount++;
                    }
                }

                // does the guessed letter appear in a word from toCheck? if so, how many times?
                boolean appearsInCheck = false;
                int checkCount = 0;
                String checkWord = toCheck.iterator().next();
                for (int i = 0; i < checkWord.length(); i++) {
                    if (checkWord.charAt(i) == guess) {
                        appearsInCheck = true;
                        checkCount++;
                    }
                }

                if (!appearsInCheck) {
                    resultSet = toCheck;
                }
                // if both have the guessed letter, choose the one with the fewest letters
                else if (appearsInResult && appearsInCheck) {
                    if (checkCount < resultCount) {
                        resultSet = toCheck;
                    }
                    /* if they both have the letter the same amount of times, choose the one with the rightmost guessed
                    letter. if their first instance of the guessed letter is at the same spot, check the next instance
                    of the guessed letter in both words, choosing the one that is rightmost */
                    else if (checkCount == resultCount) {
                        ArrayList<Integer> resultIndices = findIndices(resultWord, guess);
                        ArrayList<Integer> checkIndices = findIndices(checkWord, guess);
                        for (int i = 0; i < resultIndices.size(); i++) {
                            if (checkIndices.get(i) > resultIndices.get(i)) {
                                resultSet = toCheck;
                            }
                        }
                    }
                }
            }
        }

        wordSet = resultSet;
        return resultSet;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessed;
    }

    public void partition(char letter) {
        // for each word in wordSet, calculate its SubsetKey and add entry to map
        for (String word : wordSet) {
            String subsetKey = getSubsetKey(word, letter);
            /* if the map already has this key entry, copy the existing set, add the word into it,
            and replace the existing set with this new set */
            if (partition.containsKey(subsetKey)) {
                Set<String> toInsert = partition.get(subsetKey);
                toInsert.add(word);
                partition.put(subsetKey, toInsert);
            }
            else {
                Set<String> toInsert = new HashSet<>();
                toInsert.add(word);
                partition.put(subsetKey, toInsert);
            }
        }
    }

    public String getSubsetKey(String word, char letter) {
        StringBuilder subsetKey = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                subsetKey.append(letter);
            }
            else {
                subsetKey.append("-");
            }
        }
        return subsetKey.toString();
    }

    public ArrayList<Integer> findIndices(String word, char letter) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                result.add(i);
            }
        }
        return result;
    }
}
