# Project Report

## Design Decisions

### Architecture
Explain your MVC structure:
- The model was shown in class. It handles the logic of the game without updating the player, that is handled by the application itself. Game logic is kept separate from the application implementation, outside of simply noting the machine on which move(s) the player would like to make. There is some extensive code regarding the saving/loading of files, but the actual programming to make it possible is kept to the Method structures.
- I did not create any Interfaces for this project. The methods that were abstracted were part of the model architecture, which was done for me by the professor.
- Due to working with JavaFX in a previous project, it was implemented in the example code to begin with.

### Data Structures
- As a series of 2D arrays, where the first index represents the face, and the second a part of the tips, orientation, or other components of the Pyraminx puzzle
- They were provided within the example code. It is also the most straightforward method, as I am familair with arrays and would not need to learn too much notation or conceive of different data types. The use of enums for Color and Move notations would have been quite helpful in implementing the 2D net representation as well.

### Algorithms
- Undo utilizes a variable within the Pyraminx model to keep track of the last move the player made, which then makes it the "undo move," which is passed into a switch case to gain the antithesis of the move. The "undo move" variable is also passed to redo, ensuring we cannot redo something until it is undone. O(1), it only requires a few evaluation statements, switch case reduces runtime complexity. Redo is also O(1), as it merely calls Undo. 
- SaveToFile uses a buffed filewriter object to scan each line of the file and append(or overwrite) to a locally stored file, typically PyraminxStat.sav and MoveHistory.sav. The parameters are established so the function could potentially write and create any file, and this file can be appended to or overwritten as the client sees fit. O(n,) as the size of the input on file largely determines for how long the method will run. LoadFromFile works largely the same (aside from reading a file while returning the input as a string), so it is also O(n).

## Challenges Faced
1. **Challenge 1:** I only realized after already making the file-reading and file-writing methods that the project required the history of player movement to be saved along with the game state.
   - **Solution:** Re-writing both methods to have a filename parameter, along with a flag to adjust appending or overwriting for the file-writing method.Rewriting
   
2. **Challenge 2:** I had trouble running JavaFX on my IDE
   - **Solution:** I utilized help from students, my professor, and a lot of googling.

3. **Challenge 3:** I had a hard time conceptualizing the movements in my head
	- **Solution:** Looking up website tutorials on Pyraminx puzzles helped marginally

4. **Challenge 4:** I couldn't figure out a way to wire the movements made with the 2d net representation
	- **Solution:** ... 

## What We Learned
- It is helpful to separate the function of the program from its visual/application implementation
- It's never too late to ask for help
- It's better to work 1 hour a day for 7 days on a project rather than 8 hours in a single day
- BuffedWriter and FileWriter are useful for dynamically switching from appending to overwriting
- More about JavaFX, namely: HBoxes, VBoxes, Panes, and the Shape library.
- Measure twice, cut once (Plan your methods/algorithms before implementing them.)

## If We Had More Time
- I would have loved to complete the assignment
- Added a properly implemented 2d Net representation of the puzzle
- Added a viewer model that facilitates the 2d net representation instead of trying to do it all through javaFX
- Make the undo/redo buttons traversal based on the history file
- Add Junit testing
- Do projects over the course of 2 weeks rather than 3 days whenever possible; if not possible, make it possible.
