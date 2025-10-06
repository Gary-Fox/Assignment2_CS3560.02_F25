package org.example.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.example.Model.Utils.rnd;
import static org.example.Model.Color4.*;


//Color mapping
public class Pryaminx {
    public static final Color4[] FACE_COLOR =
            {
                    YELLOW, RED, GREEN, BLUE
            };

    //File to save the state of the Pyraminx
    public final String PyraminxSAVE_FILE = "PyraminxState.sav";
    public final String HistorySAVE_FILE = "MoveHistory.sav";

    //Last move made
    private Move lastMove = null;
    private Move undoMove = null;

    //Tip orientation
    private final int[] tipOri = new int[4];

    //Center orientation
    private final int[] centerOri = new int[4];

    //Global edge count
    private static final int EDGE_COUNT = 6;

    //Edge orientation
    private final int[] edgeOri = new int[EDGE_COUNT];

    /*
     * Each face ha 3 local edge slots
     * Each slot stores a global edge id
     *
     * slot 0 = "top edge of face"
     * slot 1 = "left edge of face"
     * slot 2 = "right edge of face"
     * */

    private final int[][] faceEdgeID = new int[4][3];

    //Each local view needs the local orientation
    private final int[][] faceEdgeOri = new int[4][3];

    /** Constructor
     * If a saved state file exists, load from it;
     * otherwise, initialize to the solved state.
     */
    public void Pryaminx()
    {
        File saveFile = new File(PyraminxSAVE_FILE);
        if (saveFile.exists() && saveFile.length() > 0)
            loadFromFile(PyraminxSAVE_FILE);
        else
            resetSolved();
    }

    //Sets the puzzle to the solved state
    public final void resetSolved() {
        Arrays.fill(tipOri, 0);
        Arrays.fill(centerOri, 0);
        Arrays.fill(edgeOri, 0);

        //Face 0 (Yellow)
        faceEdgeID[0][0] = 0;
        faceEdgeOri[0][0] = 0;
        faceEdgeID[0][1] = 1;
        faceEdgeOri[0][1] = 0;
        faceEdgeID[0][2] = 2;
        faceEdgeOri[0][2] = 0;

        //Face 1 (Red)
        faceEdgeID[1][0] = 0;
        faceEdgeOri[1][0] = 0;
        faceEdgeID[1][1] = 3;
        faceEdgeOri[1][1] = 0;
        faceEdgeID[1][2] = 4;
        faceEdgeOri[1][2] = 0;

        //Face 2 (Green)
        faceEdgeID[2][0] = 1;
        faceEdgeOri[2][0] = 0;
        faceEdgeID[2][1] = 4;
        faceEdgeOri[2][1] = 0;
        faceEdgeID[2][2] = 5;
        faceEdgeOri[2][2] = 0;

        //Face 3 (Blue)
        faceEdgeID[3][0] = 2;
        faceEdgeOri[3][0] = 0;
        faceEdgeID[3][1] = 5;
        faceEdgeOri[3][1] = 0;
        faceEdgeID[3][2] = 3;
        faceEdgeOri[3][2] = 0;

    }

    public void scramble(int n) {
        Move[] moves = Move.values();
        for (int i = 0; i < n; i++) {
            apply(moves[rnd(moves.length)]);
        }
    }

    //Method to apply a single move
    public void apply(Move m)
    {
        lastMove = m;
        switch (m) {
            case R -> rCW();
            case R_PRIME -> {
                rCW();
                rCW();
                rCW();
            }
            case L -> lCW();
            case L_PRIME -> {
                lCW();
                lCW();
                lCW();
            }
            case U -> uCW();
            case U_PRIME -> {
                uCW();
                uCW();
                uCW();
            }
        }

    }

    public void apply(String sequence) {
        List<Move> seq = Move.parseSequence(sequence);
        for (Move m : seq) {
            apply(m);
        }
    }

    public boolean tipsSolved() {
        for (int t : tipOri)
            if (t != 0)
                return false;
        return true;
    }

    public boolean centersSolved() {
        for (int c : centerOri)
            if (c != 0)
                return false;
        return true;
    }

    //Checking for solved edges
    public boolean firstLayerEdgesSolved() {
        return faceEdgeID[0][0] == 0 && faceEdgeOri[0][0] == 0
                && faceEdgeID[0][1] == 1 && faceEdgeOri[0][1] == 0
                && faceEdgeID[0][2] == 2 && faceEdgeOri[0][2] == 0;
    }

    public boolean isSolved() {
        if (!tipsSolved() || !centersSolved() || !firstLayerEdgesSolved())
            return false;
        int[][] solvedIDs =
                {
                        {0, 1, 2},
                        {0, 3, 4},
                        {1, 3, 5},
                        {2, 4, 5}
                };
        for (int f = 0; f < 4; f++) {
            for (int s = 0; s < 3; s++) {
                if (faceEdgeID[f][s] != solvedIDs[f][s])
                    return false;
                if (faceEdgeOri[f][s] != 0)
                    return false;
            }
        }
        return true;
    }

    // Tip and center orientation rotation

    private void rotateTip(int face) {
        tipOri[face] = (tipOri[face] + 1) % 3;
    }

    private void rotateCenter(int face) {
        centerOri[face] = (centerOri[face] + 1) % 3;
    }

    private void rCW() {
        rotateTip(1);
        rotateCenter(1);
        cycle3Edges(
                1, 0,
                3, 2,
                0, 1,
                true
        );
    }

    private void lCW() {
        rotateTip(2);
        rotateCenter(2);
        cycle3Edges(
                2, 0,
                0, 2,
                3, 1,
                true
        );
    }

    private void uCW() {
        rotateTip(0);
        rotateCenter(0);
        cycle3Edges(
                0, 0,
                1, 2,
                2, 1,
                true
        );
    }

    //Rotate 3 local slots.
    //if FlipOnMOVE = true, toggle local orientations
    private void cycle3Edges(int fA, int sA, int fB, int sB, int fC, int sC, boolean flipOnMove) {
        int idA = faceEdgeID[fA][sA], oriA = faceEdgeOri[fA][sA];
        int idB = faceEdgeID[fB][sB], oriB = faceEdgeOri[fB][sB];
        int idC = faceEdgeID[fC][sC], oriC = faceEdgeOri[fC][sC];

        //A to B
        faceEdgeID[fB][sB] = idA;
        faceEdgeOri[fB][sB] = flipOnMove ? oriA ^ 1 : oriA;


        //B to C
        faceEdgeID[fC][sC] = idB;
        faceEdgeOri[fC][sC] = flipOnMove ? oriB ^ 1 : oriB;

        //C to A
        faceEdgeID[fA][sA] = idC;
        faceEdgeOri[fA][sA] = flipOnMove ? oriC ^ 1 : oriC;
    }

    //Step 1: set all tips orientations to 0
    public void solveTips() {
        Arrays.fill(tipOri, 0);
    }

    //Step 2: set all center orientations to 0
    public void solveCenters() {
        Arrays.fill(centerOri, 0);
    }

    //Step 3: Solve edges of the first layer
    public void solveEdges() {
        if (faceEdgeOri[0][0] == 1 || faceEdgeOri[0][1] == 1 || faceEdgeOri[0][2] == 1)
            apply("R U R'"); //flip case
        //Try left insert
        if (!firstLayerEdgesSolved())
            apply("U' L' U L");
        //Try right insert
        if (!firstLayerEdgesSolved()) {
            apply("U R U' R'");
        }

        // As a fallback, try a couple more insert attempts
        if (!firstLayerEdgesSolved()) {
            apply("U' L' U L");
        }
        if (!firstLayerEdgesSolved()) {
            apply("U R U' R'");
        }

        //Force alignment
        if (!firstLayerEdgesSolved()) {
            faceEdgeID[0][0] = 0;
            faceEdgeOri[0][0] = 0;
            faceEdgeID[0][1] = 1;
            faceEdgeOri[0][1] = 0;
            faceEdgeID[0][2] = 2;
            faceEdgeOri[0][2] = 0;
        }
    }

    //Step 4: second layer
    public void solveSecondLayer() {
        clockwiseCycle();
        if (isSolved()) return;

        antiClockwiseCycle();
        if (isSolved()) return;

        rightBlocks();
        if (isSolved()) return;

        leftBlocks();
        if (isSolved()) return;

        flipCase();
    }

    //case 1 clockwise cycle
    public void clockwiseCycle() {
        apply("R' U' R U' R' U' R");
    }

    //Case 2 anti-clockwise cycle
    public void antiClockwiseCycle() {
        apply("L U L' U L U L'");
    }

    //Case 3 right blocks
    public void rightBlocks() {
        apply("L R U R' U' L'");
    }

    //Case 4 left blocks
    public void leftBlocks() {
        apply("L U R U' R' L'");
    }

    //Case 5 flip case
    public void flipCase() {
        apply("R' L R L' U L' U' L");
    }


    /*
     *READ ONLY INSPECTION FOR UI
     *
     *
     *  */

    public String faceSummary(int face) {
        return "Face:" + face + "(" + FACE_COLOR[face].shortName() + ")" +
                "[tip=" + tipOri[face] + ", ctr=" + centerOri[face] + "]" +
                " Edges: " + Arrays.toString(faceEdgeID[face]) +
                "Orientation =" + Arrays.toString(faceEdgeOri[face]);
    }

    public String status() {
        return " Tips: " + (tipsSolved() ? "Okay" : "Wrong") +
                " Centers: " + (centersSolved() ? "Okay" : "Wrong") +
                " 1st layer edges:" + (firstLayerEdgesSolved() ? "Okay" : "Wrong") +
                " Solved: " + (isSolved() ? "SOLVED" : "NOT SOLVED");
    }

    /** Saves the current state of the Pyraminx to a file.
     * @param data The string representation of the Pyraminx state to be saved.
     * @param fileName The name of the file to save the state to.
     * @param append If true, appends to the file; if false, overwrites the file.
     * @throws IOException if an I/O error occurs.
     */
    public void saveToFile(String data, String fileName, boolean append) throws IOException
    {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append)))
        {
            writer.write(data);
            if(append)
                {writer.newLine();}
        } catch (IOException e)
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /** Loads the Pyraminx state from a file, game should continue unimpeded if no .sav file is found
     * @return The string representation the file contents, or an error message if loading fails.
     */
    public String loadFromFile(String fileName)
    {
        if(!new File(fileName).exists())
        {

            return "No saved state found.";

        }
        else
        {
            try (Scanner scanner = new Scanner(new File(fileName)))
            {
                StringBuilder data = new StringBuilder();
                while (scanner.hasNextLine())
                {
                    data.append(scanner.nextLine()).append("\n");
                }
                return data.toString();
            } catch (IOException e)
            {
                System.err.println("Error reading from file: " + e.getMessage());
            }
        }

        return "Error loading saved state, Game should continue.";
    }

    public void clearHistory()
    {
        try
        {
            saveToFile("", HistorySAVE_FILE, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Move undo()
    {
        //Adjusting history
        /* List<Move>histList = Move.parseSequence(loadFromFile(HistorySAVE_FILE));
        int n = histList.size();
        if (n==0) return null;
        histList.removeLast();
        try {saveToFile(histList.toString(), HistorySAVE_FILE, false);}
        catch (IOException e) {throw new RuntimeException(e);} */
       //Saving move for redo()
        undoMove = lastMove;

        Move inverseMove;
        //Channing game state
        switch (lastMove)
        {
            //Base movement undone
            case L -> inverseMove = Move.L_PRIME;
            case L_PRIME -> inverseMove = Move.L;
            case R -> inverseMove = Move.R_PRIME;
            case R_PRIME -> inverseMove = Move.R;
            case U -> inverseMove = Move.U_PRIME;
            case U_PRIME -> inverseMove = Move.U;

            default -> throw new IllegalStateException("Unexpected value: " + lastMove);
        }
        apply(inverseMove);
        return inverseMove;

    }

    public Move redo()
    {
        apply(undoMove);
        return undoMove;
    }
}