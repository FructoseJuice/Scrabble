# Scrabble

### Object design diagram

The object design document should be in PDF format.
First page/slide is object diagram, with description of objects on the next page(s).

On more complicated projects, you may need additional diagrams to
clearly describe subcomponents.

### Score Checker
This program takes in one command line argument to run. The jar contains all of the dictionaries
that were provided for this project so you simply only need to provide the name of the 
dictionary that you wish to use.

Example:
java -jar scorechecker.jar sowpods.txt
java -jar scorechecker.jar twl06.txt

Next, scorechecker requires the user to input two boards to the CLI. You must input the boards in
the form:

BOARD_SIZE
...board

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