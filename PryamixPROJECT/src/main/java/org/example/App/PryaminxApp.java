package org.example.App;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
//Our imports
import org.example.Model.Move;
import org.example.Model.Pryaminx;

import javax.script.CompiledScript;
import java.io.IOException;
import java.lang.reflect.Array;

/* TO DO:
* 2D net visualization (failed)
 */

public class PryaminxApp extends Application
{
    private final Pryaminx pry = new Pryaminx();
    private final TextArea log = new TextArea();
    private final TextArea history = new TextArea();
    private boolean firstRun = true;
    StringBuilder histText = new StringBuilder();
    private final Pane puzzleWindow = new Pane();
    final int size = 30;
    int rState, bState, yState, gState = 0;


    @Override
    public void start(Stage stage) throws Exception
    {
     stage.setTitle("Pyraminx Simulator - Abstraction implementation");

     //controlls
        Button btnReset = new Button("Reset (Solved)");
        Button btnScramble = new Button("Scramble");

        Button btnR = new Button("R");
        Button btnRPrime = new Button("R'");
        Button btnL = new Button("L");
        Button btnLPrime = new Button("L'");
        Button btnU = new Button("U");
        Button btnUPrime = new Button("U'");

        Button btnTips = new Button("Solve tips");
        Button btnCenters = new Button("Solve centers");
        Button btnEdges = new Button("Solve edges");
        Button btnSecond = new Button("Solve second layer");

        Button btnUndo = new Button("Undo");
        Button btnRedo = new Button("Redo");
        Button btnClearHistory = new Button("Clear");

        TextField seqField = new TextField("U' L' U L");
        Button btnRunSeq = new Button("Run sequence");

        //Layout
        HBox topRow = new HBox(10, btnReset, btnScramble);
        HBox moveRow = new HBox(10, btnR, btnRPrime, btnL, btnLPrime, btnU, btnUPrime);
        HBox stepRow = new HBox(10, btnTips, btnCenters, btnEdges, btnSecond);
        HBox seqRow = new HBox(10, new Label("Algorithm:"), seqField, btnRunSeq);
        //Undo/Redo row
        HBox undoRedoRow = new HBox(10, btnClearHistory, btnUndo, btnRedo);
        undoRedoRow.setAlignment(Pos.CENTER_RIGHT);

        topRow.setPadding(new Insets(10));
        moveRow.setPadding(new Insets(10));
        stepRow.setPadding(new Insets(10));
        seqRow.setPadding(new Insets(10));

        log.setEditable(false);
        log.setPrefRowCount(8);
        log.setWrapText(true);

        history.setEditable(false);
        history.setPrefRowCount(10);
        history.setPrefColumnCount(10);
        history.setWrapText(true);

        //Making RED face
        Rectangle R0, R1, R2, R3, R4, R5, R6, R7, R8;
        R0 = MakeTile(Color.RED);
        R1 = MakeTile(Color.RED);
        R2 = MakeTile(Color.RED);
        R3 = MakeTile(Color.RED);
        R4 = MakeTile(Color.RED);
        R5 = MakeTile(Color.RED);
        R6 = MakeTile(Color.RED);
        R7 = MakeTile(Color.RED);
        R8 = MakeTile(Color.RED);
        Rectangle[] rFace = {R0, R1, R2, R3, R4, R5, R6, R7, R8};
        Pane redFACE = CompilePane(rFace, true);

        //Making BLUE face
        Rectangle B0, B1, B2, B3, B4, B5, B6, B7, B8;
        B0 = MakeTile(Color.BLUE);
        B1 = MakeTile(Color.BLUE);
        B2 = MakeTile(Color.BLUE);
        B3 = MakeTile(Color.BLUE);
        B4 = MakeTile(Color.BLUE);
        B5 = MakeTile(Color.BLUE);
        B6 = MakeTile(Color.BLUE);
        B7 = MakeTile(Color.BLUE);
        B8 = MakeTile(Color.BLUE);
        Rectangle[] bFace = {B0, B1, B2, B3, B4, B5, B6, B7, B8};
        Pane blueFACE = CompilePane(bFace, true);

        //Making YELLOW face (not the racial stereotype)
        Rectangle Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8;
        Y0 = MakeTile(Color.YELLOW);
        Y1 = MakeTile(Color.YELLOW);
        Y2 = MakeTile(Color.YELLOW);
        Y3 = MakeTile(Color.YELLOW);
        Y4 = MakeTile(Color.YELLOW);
        Y5 = MakeTile(Color.YELLOW);
        Y6 = MakeTile(Color.YELLOW);
        Y7 = MakeTile(Color.YELLOW);
        Y8 = MakeTile(Color.YELLOW);
        Rectangle[] yFace = {Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8};
        Pane yellFACE = CompilePane(yFace, false);

        //Making GREEN face
        Rectangle G0, G1, G2, G3, G4, G5, G6, G7, G8;
        G0 = MakeTile(Color.GREEN);
        G1 = MakeTile(Color.GREEN);
        G2 = MakeTile(Color.GREEN);
        G3 = MakeTile(Color.GREEN);
        G4 = MakeTile(Color.GREEN);
        G5 = MakeTile(Color.GREEN);
        G6 = MakeTile(Color.GREEN);
        G7 = MakeTile(Color.GREEN);
        G8 = MakeTile(Color.GREEN);
        Rectangle[] gFace = {G0, G1, G2, G3, G4, G5, G6, G7, G8};
        Pane greenFACE = CompilePane(gFace, true);

        //Red left of Blue, Yellow below Blue, Green right of Blue.
        VBox tempBY = new VBox(blueFACE,yellFACE);
        HBox tempRBYG = new HBox(redFACE, tempBY, greenFACE);

        puzzleWindow.getChildren().add(tempRBYG);
       // puzzleWindow.getChildren().add(redFACE);
       // puzzleWindow.getChildren().add(blueFACE);



        VBox game = new VBox(10, topRow, moveRow, stepRow, seqRow, new Label("State"), log, puzzleWindow);
        VBox hist = new VBox(10, new Label("History"), history, undoRedoRow);
        hist.alignmentProperty().setValue(Pos.TOP_RIGHT);
        HBox root = new HBox(10, game, hist);
        game.setPadding(new Insets(10));
        root.alignmentProperty().setValue(Pos.CENTER);
        HBox.setMargin(root, new Insets(5));

        //Wiriing
        btnReset.setOnAction(e ->
        {
            pry.resetSolved();
            writeState("Reset to solved state.");
        });

        btnScramble.setOnAction(e ->
        {
            pry.scramble(20);
            writeState("Scrambled the puzzle.");
        });

        //Movement buttons
        btnR.setOnAction(e ->
        {
            pry.apply(Move.R);
            writeState("R");

            for(int i=0; i<5; i++)
            {
                rFace[i].setFill(Color.GREEN);
            }
        });
        btnRPrime .setOnAction(e -> { pry.apply(Move.R_PRIME); writeState("R'"); });
        btnL.setOnAction(e -> { pry.apply(Move.L); writeState("L"); });
        btnLPrime.setOnAction(e -> { pry.apply(Move.L_PRIME); writeState("L'"); });
        btnU.setOnAction(e -> { pry.apply(Move.U); writeState("U"); });
        btnUPrime.setOnAction(e -> { pry.apply(Move.U_PRIME); writeState("U'"); });

        //Solving buttons
        btnTips.setOnAction(e ->
        {
            pry.solveTips();
            writeState("Step 1: solveTips()");
            //Red Tips
            rFace[0].setFill(Color.RED);
            rFace[8].setFill(Color.RED);
            rFace[4].setFill(Color.RED);
            //Blue tips
            bFace[0].setFill(Color.BLUE);
            bFace[8].setFill(Color.BLUE);
            bFace[4].setFill(Color.BLUE);
            //Yellow tips
            yFace[0].setFill(Color.YELLOW);
            yFace[8].setFill(Color.YELLOW);
            yFace[4].setFill(Color.YELLOW);
            //Green Tips
            gFace[0].setFill(Color.GREEN);
            gFace[8].setFill(Color.GREEN);
            gFace[4].setFill(Color.GREEN);
        });
        btnCenters.setOnAction(e ->
        {
            pry.solveCenters();
            writeState("Step 2: solveCenters()");
            //Red centers
            rFace[1].setFill(Color.RED);
            rFace[6].setFill(Color.RED);
            rFace[3].setFill(Color.RED);
            //Blue centers
            bFace[1].setFill(Color.BLUE);
            bFace[6].setFill(Color.BLUE);
            bFace[3].setFill(Color.BLUE);
            //Yellow centers
            yFace[1].setFill(Color.YELLOW);
            yFace[6].setFill(Color.YELLOW);
            yFace[3].setFill(Color.YELLOW);
            //Green centers
            gFace[1].setFill(Color.GREEN);
            gFace[6].setFill(Color.GREEN);
            gFace[3].setFill(Color.GREEN);
        });

        //solving edges and second layer
        btnEdges.setOnAction(e ->
        {
            pry.solveEdges();
            writeState("Step 3: solveEdges()");
            //Red edges
            //rFace[5].setFill(Color.RED);
            rFace[7].setFill(Color.RED);
            //Blue edges
            bFace[5].setFill(Color.BLUE);
            bFace[7].setFill(Color.BLUE);
            //Green edges
            gFace[5].setFill(Color.GREEN);
            gFace[7].setFill(Color.GREEN);
            //Yellow edges
            yFace[7].setFill(Color.YELLOW);

        });
        btnSecond.setOnAction(e -> { pry.solveSecondLayer(); writeState("Step 4: solveSecondLayer()"); });
        //Running sequence
        btnRunSeq.setOnAction(e ->
        {
            String seq = seqField.getText();
            try
            {
                pry.apply(seq);
                writeState("Applied sequence:" + seq);
            } catch (Exception ex){
                append("Error: " + ex.getMessage());
            }
        });

        //History buttons
       btnUndo.setOnAction(e -> {
            Move undoMove = pry.undo();
            if (undoMove != null) {
                //pry.apply(undoMove);
                writeState("Undo: " + undoMove);
            } else {
                append("Nothing to undo.");
            }
        });

        btnRedo.setOnAction(e -> {
            Move redoMove = pry.redo();
            if (redoMove != null) {
               // pry.apply(redoMove);
                writeState("Redo: " + redoMove);
            } else {
                append("Nothing to redo.");
            }
        });

        btnClearHistory.setOnAction(e -> {
                    pry.clearHistory();
                    histText.setLength(0);
                    writeState("Cleared history");
                });



        //initial State
        //writeState("Ready");
        //Loading save file if it exists
        String initialState = pry.loadFromFile(pry.PyraminxSAVE_FILE);
        String initialHistory = pry.loadFromFile(pry.HistorySAVE_FILE);
        log.setText(initialState);
        history.setText(initialHistory);
        histText.append(initialHistory);
        firstRun = false;
        //Stage setup
        stage.setScene(new Scene(root, 720, 710));
        stage.show();

        }

        /** Updates the user, updates PyraminxState.sav, updates MoveHistory.sav
         * @param header the most recent action by the user as a string
         *
         * */
    private void writeState(String header)
        {
            //Updating state to logVbox
            StringBuilder sb = new StringBuilder();
            sb.append(header).append('\n');
            for(int f=0; f<4; f++)
            {
                sb.append(pry.faceSummary(f)).append('\n');
            }
            sb.append("Status: ").append(pry.status()).append('\n');
            log.setText(sb.toString());
            //Updating state to PyraminxState.sav
            if (!firstRun)
            {
                try
                {
                    pry.saveToFile(sb.toString(), pry.PyraminxSAVE_FILE, false);
                } catch (IOException e) {
                    append("Error saving state: " + e.getMessage());
                }
            }

            //update historyVbox
            histText.append(header).append('\n');
            history.setText(histText.toString());
            //update MoveHistory.sav
            try
            {
                pry.saveToFile(header, pry.HistorySAVE_FILE, true);
            } catch (IOException e) {
                append("Error saving history: " + e.getMessage());
            }
        }

        //For inter-method appending
        private void append(String text)
        {
            log.appendText(text + '\n');

        }

        //Actually starts the program
        public static void main(String[] args)
        {
            launch(args);
        }

        //Creates single rectangles that will create pyraminx faces
        public Rectangle MakeTile(Color b)
        {
            Rectangle a = new Rectangle();
            //final int size = 30;
            final int border = size/10;
            a.setHeight(size);
            a.setWidth(size);
            a.setFill(b);
            a.setStroke(Color.BLACK);
            a.setStrokeWidth(border);

            return a;
        }

        /**This method places 8 squares into the shape of a triangle
         * @param Face an array of squares
         * @param rightsideup Whether the triangle needs to be upside down or not
         * @return the triangle made up of 8 squares as a plane
         * */
    private Pane CompilePane(Rectangle[] Face, boolean rightsideup)
    {
        Pane row1 = new Pane();
        Pane row2 = new Pane();

        if (rightsideup)
        {

            for (int i=0; i < 5; i++)
            {
                row1.getChildren().add(Face[i]);
                Face[i].setX(size + i*size);
                Face[i].setY(size*3);
            }
            for(int i=5; i < 8; i++)
            {
                row2.getChildren().add(Face[i]);
                Face[i].setX(size*2 + (i-5)*size);
                Face[i].setY(size*2);
            }
            Face[8].setX(size*3);
            Face[8].setY(size);
        }
        else
        {
            for (int i=0; i < 5; i++)
            {
                row1.getChildren().add(Face[i]);
                Face[i].setX(size + i*size);
                Face[i].setY(size);
            }
            for(int i=5; i < 8; i++)
            {
                row2.getChildren().add(Face[i]);
                Face[i].setX(size*2 + (i-5)*size);
                Face[i].setY(size*2);
            }
            Face[8].setX(size*3);
            Face[8].setY(size*3);

        }
        return new Pane(row1,row2,Face[8]);
    }


}