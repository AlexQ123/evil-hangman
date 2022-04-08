# evil-hangman

I built an Evil Hangman program that actively cheats at Hangman. Instead of choosing a single word that the player tries to guess, the program maintains a set of words that it continuously pares down. It does the latter in such a way as to minimize the player’s chance of winning.

Inputs to the program:
- dictionary is the path to a text file with whitespace separated words (no numbers, punctuation, etc.) *in theory, this dictionary will contain all the words in the English language
- wordLength is an integer ≥ 2
- guesses is an integer ≥ 1

How the game works:
- At the beginning of each turn, the program displays the number of remaining guesses, an alphabetized list of letters guessed so far, and the partially constructed word.
- The user is prompted for his/her next letter guess. If the guess is not an upper or lower case letter, or the guess has already been made, the user is reprompted.
- The program reports the number of position(s) in which the letter appears. If the current word list doesn't contain any word with that letter, the number of remaining guesses is decremented. If all words in the list contain at least one word with that letter, the program indicates such and updates the word.
- These steps are repeated until there are no more turns left.

The “Evil” Algorithm
1. Begin with the set of English words, B, read in from the indicated input file.
2. Assuming the user inputs a length l, Create a subset of words, L, such that every word in B of length l is in L and L ⊆ S.
3. Each time the user guesses a letter:
  a. Partition the word list into "word groups" based on the positions of the guessed letter in the words.
    - For example, let the current word list be [ALLY, BEST, COOL, DEAL, ECHO, ELSE, FLEW, GOOD, HEAL, HOPE, LAZY]. Assume the player guesses the letter “E.” The program partitions the word list into the following six word families:
      1.       E---         contains ECHO.
      2.       -E--         contains BEST, DEAL, HEAL.
      3.       --E-         contains FLEW.
      4.       ---E         contains HOPE.
      5.       E--E         contains ELSE.
      6.       ----         contains ALLY, COOL, GOOD, LAZY.
  b. Choose the largest of these word groups to replace L.
    - In the example above, the largest word group is of the form ----.
    - If two or more of the groups are of the same size, choose the one to return according to the following priorities:
      1. Choose the group in which the letter does not appear at all.
      2. If each group has the guessed letter, choose the one with the fewest letters.
      3. If this still has not resolved the issue, choose the one with the rightmost guessed letter (e.g. given the patterns E--E and -E-E, the second group would be chosen)
      4. If there is still more than one group, choose the one with the next rightmost letter. Repeat this step (step 4) until a group is chosen.
