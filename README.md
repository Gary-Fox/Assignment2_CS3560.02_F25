# Pyraminx Puzzle Game

## Team Members
- Jacob Carrasco (Gary Fox)

## Demo Video Link
<https://youtu.be/0GDdhtu-jp8>

## How to Run
1. Clone repository: `git clone https://github.com/Gary-Fox/Assignment2_CS3560.02_F25`
2. Open in IntelliJ/Eclipse
3. Add an application configuration, name it whatever you like
3. Configure VM options: `--module-path "C:\Users\[Directory path to your JAVAFX files]\Java\openjfx-24_windows-x64_bin-sdk\javafx-sdk-24\lib" --add-modules javafx.controls,javafx.fxml`
4. Make sure your configuration's Main Class section is set to: org.example.App.PryaminxApp
5. Run '[whatever you named the configuration]' in IntelliJ/Eclipse

## Features Implemented
- [X] Correct state representation (all stickers tracked)
- [X] Standard move notation (R, U, F, L, D, B with ', 2)
- [X] Scramble function (random valid moves)
- [X] Undo/redo move history
- [ ] 2D net visualization (updates with moves)
- [X] Move history display
- [X] Save/load puzzle state
- [X] Reset to solved

## Controls
- Operate buttons by:
	- Using the left mouse button to click on them
	- Use Tab to navigate the buttons, and select one by pressing the Enter key
- Use the keyboard to type in a sequence of moves within the proper text field

## Known Issues
- Issue 1: Undo does not traverse the entire history of moves, instead going back to the most recent move.
- Issue 2: Redo is also not based on the saved or displayed history, and can constantly "redo" the same move over and over until updated by Undo
- Issue 3: 2D net visualization is semi-static, partially complete, and not practicing abstraction very well
- Issue 4: Undo and Redo only work on single moves (R R' L L' U U')
- Issue 5: Undo and Redo do not work until the player inputs movement, regardless of MoveHistory.sav
- Issue 6: MoveHistory.sav and PyraminxState.sav are vulnerable to alterations by user

## External Libraries
- JavaFX
- Java.Script
- Java.io
-Java.lang

-Java.util

