# Scrabble

### Object design diagram

The object design document should be in PDF format.
First page/slide is object diagram, with description of objects on the next page(s).

On more complicated projects, you may need additional diagrams to
clearly describe subcomponents.

## Score Checker
This program takes in one command line argument to run. The jar contains all of the dictionaries
that were provided for this project so you simply only need to provide the name of the 
dictionary that you wish to use. Otherwise, you can specify a specific path on your machine to 
load a dictionary file from.

Example:
1. java -jar scorechecker.jar sowpods.txt
2. java -jar scorechecker.jar twl06.txt
3. java -jar scorechecker.jar $PATH_TO_DICTIONARY_FILE.txt

Next, scorechecker requires the user to input two boards to the CLI. You must input the boards in
the form:

{BOARD_SIZE}
{BOARD}

Example:
```
7
3. .. .. 2. .. .. 3. 
.. .3 .. .. .. .3 ..
.. ..  a  d .2 .. ..
2. ..  u  h .. .. 2.
.. ..  l  o .2 .. ..
..  m  a  t .. .3 ..
 r  e  S  i  d .. 3.
```
The first board should be the original board, and the second board should the result board.

This program will first check if the boards are compatible.
If the boards are not compatible, it will give you output describing where the critical
differences between the two boards lay. This compatibility check will also ensure the legality
of any new words on the result board.

If they are compatible, the program will then score the new tiles in the result board.

After this process is complete, the program will loop and request new boards to test, this
will continue until either the user inputs incorrectly formatted input (in which case the program
will error out, forcing a restart), or the user forcefully exits the program.

## Solver
This program takes in one command line argument to run. The jar contains all originally provided
dictionaries, however you can specify the path to a dictionary file on your computer as well.

Example:
1. java -jar solver.jar sowpods.txt
2. java -jar solver.jar $PATH_TO_DICTIONARY_FILE.txt

Next, the solver requires the user to input a board size, board, and tray to the CLI. You must input these
in the form:

{BOARD SIZE}
{BOARD}
{TRAY}

Example:
```
7
3. .. .. 2. .. .. 3. 
.. .3 .. .. .. .3 ..
.. ..  a  d .2 .. ..
2. ..  u  h .. .. 2.
.. ..  l  o .2 .. ..
..  m  a  t .. .3 ..
 r  e  S  i  d .. 3.
bals*kh
```

The solver will then give you the best move from the given board and tray.

## Scrabble
### Running scrabble
This program requires 1 command line argument to run, however you can give it two. The first argument
pertains to dictionaries files, which I have already described how to provide in the above two programs.
The second argument will specify the board size to use.

The available options for board sizes include: 7, 15, and 21.
1. If you don't give an argument for the board size then it will default to 15.
2. If you give an argument for the board size that is not 7, 15, or 21, then the program will default
to the 21 sized board.

java -jar scrabble.jar {ORIGINAL_DICTIONARY_NAME / DICTIONARY_PATH} {BOARD_SIZE}

EXAMPLE:
1. java -jar scrabble.jar sowpods.txt
2. java -jar scrabble.jar sowpods.txt 7
3. java -jar scrabble.jar $DICTIONARY_PATH.txt 219871923

### How to play
The game will start with the player taking the first move. However, the AI is fully capable on playing on an
empty board if the player decides to skip. 

#### Placing tiles
In order to place tiles on the board, you must first select the tile in the tray, and then click
on the board space that you would like to place the tile on. 

#### Resetting play / Putting tiles back into tray
If you are unhappy with where you have placed tiles, you can simply press the reset button to put all tiles 
back into your tray. This will not end your play.

#### Swapping out tiles
If you need to swap out tiles, you must right-click on the tiles you would like to swap. The program will
respond by flipping these tiles in your tray. Once you click the swap button, all flipped tiles will be swapped
and your turn will be ended.

#### Submitting a play
In order to submit a play, you must have first placed tiles on the board. Once you have done this, simply
press the submit button. If your play is legal, then the AI will now move. If it was not legal, your tiles
will be returned back into your hand.

#### Skipping your play
Simply press the skip button during your turn.

#### Misc.
If you have tiles on the board, or flipped tiles, and you decide to skip or swap tiles, you do not need
to reset your play. Tiles will automatically be returned to your hand if you take any action besides submitting
your play.