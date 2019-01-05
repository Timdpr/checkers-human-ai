package main.java;

import com.jfoenix.controls.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.model.AI;
import main.java.model.Board;
import main.java.model.Move;
import main.java.model.MoveGenerator;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * The Controller class, neatly wedged in-between the .fxml view and the /model game model.
 * Takes input from the player interacting with the GUI, updates and plays the internal model including controlling an
 * AI, and updates the GUI with the model's updated state.
 *
 * Having only one controller per view means that this class has become a little monolithic, however I have done my best
 * to make it clear and relatively modularised.
 *
 * @author tp275
 */
public class Controller implements Initializable {

    @FXML private StackPane rootStackPane;
    @FXML private Pane boardPane;

    @FXML private Pane row_a; @FXML private Pane row_b; @FXML private Pane row_c; @FXML private Pane row_d;
    @FXML private Pane row_e; @FXML private Pane row_f; @FXML private Pane row_g; @FXML private Pane row_h;
    private final List<Pane> panes = new ArrayList<>();

    @FXML private Circle c1; @FXML private Circle c2; @FXML private Circle c3; @FXML private Circle c4;
    @FXML private Circle c5; @FXML private Circle c6; @FXML private Circle c7; @FXML private Circle c8;
    @FXML private Circle c9; @FXML private Circle c10; @FXML private Circle c11; @FXML private Circle c12;
    @FXML private Circle c13; @FXML private Circle c14; @FXML private Circle c15; @FXML private Circle c16;
    @FXML private Circle c17; @FXML private Circle c18; @FXML private Circle c19; @FXML private Circle c20;
    @FXML private Circle c21; @FXML private Circle c22; @FXML private Circle c23; @FXML private Circle c24;
    private List<Circle> circles = new ArrayList<>();

    @FXML private Text turnText;
    @FXML private JFXSlider sliderDifficulty;
    @FXML private JFXCheckBox highlightCheckBox;

    private Circle selectedPiece;
    private Rectangle selectedRectangle;
    private Rectangle originRectangle;
    
    private boolean pieceIsMoving;
    private Point humanMoveOrigin;

    private Point2D offset;
    private double startLayoutX;
    private double startLayoutY;

    private final AI ai = new AI();
    private boolean aiTurn;
    private Board internalBoard;
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private boolean win = false;

    /**
     * Called after window has finished loading.
     * Sets up gui pieces, sets the turn to the human player, and loads a new internal board.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panes.addAll(Arrays.asList(row_a, row_b, row_c, row_d, row_e, row_f, row_g, row_h));
        circles.addAll(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18,
                c19, c20, c21, c22, c23, c24));
        aiTurn = false;
        internalBoard = new Board();
    }

    /**
     * When a piece is first clicked on the gui board, this runs. The corresponding Circle object and the Rectangle it
     * is on are found, as well as it's location in the scene (needed for snapping back), and its opacity is lowered.
     * @param mouseEvent the mouse press event
     */
    @FXML
    public void startMovingPiece(MouseEvent mouseEvent) {
        if (aiTurn) {
            return;
        }
        selectedPiece = (Circle)mouseEvent.getSource(); // get the Circle object being moved

        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY()); // get mouse location
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint); // get mouse location in the boardpane
        originRectangle = pickRectangle(mousePointScene.getX(), mousePointScene.getY()); // pick rectangle using above
        humanMoveOrigin = idToPoint(originRectangle.getId()); // use rectangle's ID to set humanMoveOrigin to the location of the piece

        startLayoutX = selectedPiece.getLayoutX(); // get the circle's initial location
        startLayoutY = selectedPiece.getLayoutY();
        selectedPiece.setOpacity(0.4d); // make the circle translucent
        offset = new Point2D(mouseEvent.getX(), mouseEvent.getY()); // get the mouse offset
        pieceIsMoving = true;
    }

    /**
     * Called while the piece is being dragged.
     * @param mouseEvent the mouse drag event
     */
    @FXML
    public void movePiece(MouseEvent mouseEvent) {
        Point2D mousePoint = new Point2D(mouseEvent.getX()-30, mouseEvent.getY()-30);
        Point2D mousePointScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        if(notInBoard(mousePointScene) || aiTurn) { // check for piece being in board or it not being human's turn
            return;  // don't relocate() b/c will resize Pane
        }
        if (highlightCheckBox.isSelected()) { // if toggled, highlight the hovered-over rectangle in green if valid move
            highlightValidMove(mouseEvent);
        }
        Point2D mousePointParent = selectedPiece.localToParent(mousePoint);
        selectedPiece.relocate(mousePointParent.getX()-offset.getX(), mousePointParent.getY()-offset.getY());
    }

    /**
     * Highlights the currently hovered-over rectangle in green if dropping the piece here wold be a valid move
     * @param mouseEvent the MouseEvent from movePiece
     */
    private void highlightValidMove(MouseEvent mouseEvent) {
        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint);
        Rectangle r = pickRectangle(mousePointScene.getX(), mousePointScene.getY());
        if (r == null) {
            System.out.println("Rectangle selection was null in highlightValidMove.");
        } else {
            Point destination = idToPoint(r.getId()); // get the board location using the rectangle's id
            if (findValidMoves('r').contains(new Move(humanMoveOrigin, destination))) {
                highlightSquareGreen(mouseEvent); // (^if this move would be valid)
            }
        }
    }

    /**
     * Called when a dragged piece is dropped or otherwise finishes moving.
     * This is essentially the main game loop, finishing the player's GUI move, updating the internal board and making
     * the AI's model and GUI move.
     * It's rather unfortunate that this method has so many responsibilities, but using JavaFX in this way makes it hard
     * not to have a method like this, and hopefully it is relatively clear and modularised.
     * @param mouseEvent the MouseEvent on drag finished
     */
    public void finishMovingPiece(MouseEvent mouseEvent) {
        if (aiTurn) {
            return;
        }
        offset = new Point2D(0.0d, 0.0d);
        // Get cursor's location in the scene
        Point2D mousePointScene = selectedPiece.localToScene(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        Rectangle rec = pickRectangle(mousePointScene.getX(), mousePointScene.getY()); // get rectangle under cursor

        ///// HUMAN MOVE: /////
        playHumanTurn(rec);
        pieceIsMoving = false;

        ///// AI MOVE: /////
        if (aiTurn && !win) {
            new Thread(this::playAITurn).start();
        }
    }

    /**
     * Finishes the human's turn after the dragged piece has been dropped. Checks for validity and uses Timeline
     * animation to finish the GUI move correctly, as well as fully updating the model.
     * @param rec the Rectangle that the gui piece landed on
     */
    private void playHumanTurn(Rectangle rec) {
        Timeline timeline = new Timeline(); // setup piece animation

        if (rec != null && !aiTurn) {
            Point2D rectScene = rec.localToScene(rec.getX(), rec.getY()); // get pt in scene that the rectangle is at
            Point2D parent = boardPane.sceneToLocal(rectScene.getX(), rectScene.getY());
            Point destination = idToPoint(rec.getId()); // get board location of the piece's location

            // Check move is valid by finding valid moves for red and checking that moves.indexOf(move) returns > -1
            ArrayList<Move> moves = findValidMoves('r');
            int moveIndex = moves.indexOf(new Move(humanMoveOrigin, destination));

            if (moveIndex >= 0) { // if human move was valid:
                timeline = getValidMoveTimeline(parent); // create move animation

                Move playerMove = moves.get(moveIndex); // get the move by taking it from the valid move list
                internalBoard = internalBoard.updateLocation(playerMove); // update piece's location in internal board

                if (playerMove.hasPieceToRemove()) { // if it was a jump move, delete intermediate piece from the GUI
                    for (Point pieceToRemove : playerMove.getPiecesToRemove()) {
                        if (selectCircle(pieceToRemove.x, pieceToRemove.y) != null) {
                            Timeline removalTimeline = new Timeline();
                            removalTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(selectCircle(pieceToRemove.x, pieceToRemove.y).opacityProperty(), 0.0d)));
                            removalTimeline.setOnFinished(e -> removePiece(pieceToRemove));
                            removalTimeline.play();
                        } else {
                            removePiece(selectCircle(pieceToRemove.x, pieceToRemove.y));
                        }
                    }
                }
                timeline.play(); // play animation

                checkForWin('r'); // check whether the player has won & run finish procedure if true

                checkForHumanKing(); // check (& execute) whether the moved piece needs to now be displayed as a king

                checkForHumanMultiJump(playerMove); // check (& handle) whether the player can take another jump

            } else { // if move is not valid, move the circle back to its original position
                timeline = getInvalidMoveTimeline();
                timeline.play();
            }
        } else { // return circle's opacity to 0 if move unsuccessful because it was the ai's turn
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                    )); timeline.play();
        }
    }

    /**
     * Checks whether the selected red piece is on the AI's home row and updates its image if so
     */
    private void checkForHumanKing() {
        selectedPiece.setLayoutX(indexToPixel(pixelToIndex(selectedPiece.getLayoutX())-1));
        selectedPiece.setLayoutY(indexToPixel(pixelToIndex(selectedPiece.getLayoutY())-1));
        if (selectedPiece.getLayoutY() == 30.0) {
            makeKing(selectedPiece, 'r');
        }
    }

    /**
     * Checks whether another jump can be made, given the current state of the board and the player's last move.
     * If so, sets the aiTurn to false so as to give the human another turn.
     * @param playerMove the human's last move
     */
    private void checkForHumanMultiJump(Move playerMove) {
        // if player's move was a jump move, and there is an available jump move with it's origin at the original move's destination
        if (playerMove.hasPieceToRemove() && (moveGenerator.detectMultiMove(internalBoard, 'r', playerMove.getDestination()).size() > 0)) {
            aiTurn = false;
            turnText.setText(" Human multi-jump!");
        } else {
            aiTurn = true;
        }
    }

    /**
     * Plays the AI's turn. Gets the 'best' valid move using minimax, updates model, GUI and checks for a win and multi-move
     */
    private void playAITurn() {
        turnText.setText(" AI is thinking...");

        Move aiMove = runMinimax(); // Minimax!
        internalBoard = internalBoard.updateLocation(aiMove); // update model board
        System.out.println(internalBoard.toString()); // TODO: Remove printing
        // TODO: Swapped these around:
        updateAIPiece(aiMove); // update GUI board
        // check (& execute) whether the AI has won
        Platform.runLater(() -> checkForWin('w'));
        aiTurn = false;
        turnText.setText(" Human");
    }

    /**
     * Executes the minimax algorithm with a copy of the current board, the slider's depth level and with a generated list
     * of valid moves for the AI given the current board state.
     * @return the 'best' AI move as determined by minimax
     */
    private Move runMinimax() {
        int timeLimitSeconds = (int)sliderDifficulty.getValue();
        return ai.playTimeLimited(new Board(internalBoard.getBoard()), timeLimitSeconds, findValidMoves('w'));
    }

    /**
     * Returns the Rectangle object under the given MouseEvent
     * @param evt the MouseEvent to fin the rectangle under
     * @return the Rectangle object under the given MouseEvent
     */
    private Rectangle pickRectangle(MouseEvent evt) {
        return pickRectangle(evt.getSceneX(), evt.getSceneY());
    }

    /**
     * Returns the Rectangle object under the given scene X and Y location
     * @param sceneX result of evt.getSceneX()
     * @param sceneY result of evt.getSceneY()
     * @return the Rectangle object under the given scene X and Y location
     */
    private Rectangle pickRectangle(double sceneX, double sceneY) {
        Rectangle rec = null;
        for (Pane row : panes) { // go through the row panes
            Point2D mousePoint = new Point2D(sceneX, sceneY);
            Point2D mousePointLocal = row.sceneToLocal(mousePoint);
            // if the given location is in a pane, get the rectangle under the local point
            if (row.contains(mousePointLocal)) {
                for (Node cell : row.getChildrenUnmodifiable()) {
                    Point2D mousePointLocalCell = cell.sceneToLocal(mousePoint);
                    if (cell.contains(mousePointLocalCell)) {
                        rec = (Rectangle)cell;
                        break;
                    }}
                break;
            }}
        return rec;
    }

    /**
     * Checks whether the drag 'release' event was outside of the board pane. Consumes the event if so.
     * @param mouseEvent the MouseEvent to check
     */
    public void checkReleaseOutOfBoard(MouseEvent mouseEvent) {
        Point2D mousePointScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        if(notInBoard(mousePointScene)) {
            leaveBoard(mouseEvent);
            mouseEvent.consume();
        }
    }

    /**
     * Checks whether the given point is outside the bounds of the board's base pane
     * @param pt The point to check
     * @return True if outside bounds, else false
     */
    private boolean notInBoard(Point2D pt) {
        Point2D panePt = boardPane.sceneToLocal(pt);
        return !(panePt.getX() - offset.getX() >= 0.0d)
                || !(panePt.getY() - offset.getY() >= 0.0d)
                || !(panePt.getX() <= boardPane.getWidth())
                || !(panePt.getY() <= boardPane.getHeight());
    }

    /**
     * Adds a shadow effect to the rectangle that the mouse is over
     * @param mouseEvent the MouseEvent under which to add the shadow effect
     */
    public void addShadowSquare(MouseEvent mouseEvent) {
        Rectangle r = pickRectangle(mouseEvent);
        if (r == null) {
            if (selectedRectangle != null) {
                // deselect the previous rectangle
                selectedRectangle.setEffect(null);
            }
            selectedRectangle = null;
            return;
        }
        if (r != selectedRectangle) {
            if (selectedRectangle != null) {
                // deselect the previous rectangle
                selectedRectangle.setEffect(null);
            }
            // make selectedRectangle the new rectangle and add shadow
            selectedRectangle = r;
            selectedRectangle.setEffect(new InnerShadow());
        }
    }

    /**
     * Adds a green colour to the rectangle that the mouse is over
     * @param mouseEvent the MouseEvent under which to add the colour
     */
    private void highlightSquareGreen(MouseEvent mouseEvent) {
        Rectangle r = pickRectangle(mouseEvent);
        if (r != selectedRectangle) {
            if (selectedRectangle != null) {
                // deselect the previous rectangle
                selectedRectangle.setEffect(null);
            }
            // make selectedRectangle the new rectangle and add colour
            selectedRectangle = r;
            ColorInput colour = new ColorInput();
            colour.setPaint(Color.rgb(15,180,70, 0.8));
            colour.setHeight(60);
            colour.setWidth(60);
            selectedRectangle.setEffect(colour);
        }
    }

    /**
     * Called when the mouse moves outside the board pane. Useful for when a piece is being dragged: it is then made to
     * snap back to its original position.
     * @param mouseEvent seemingly redundant but needed to fix bug where piece gets stuck
     */
    public void leaveBoard(MouseEvent mouseEvent) {
        if (pieceIsMoving) {
            final Timeline timeline = new Timeline();
            offset = new Point2D(0.0d, 0.0d);
            pieceIsMoving = false;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(selectedPiece.layoutXProperty(), startLayoutX),
                            new KeyValue(selectedPiece.layoutYProperty(), startLayoutY),
                            new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                    )
            );
            timeline.play();
        }
    }

    /**
     * Displays the help popup when the help button is pressed
     */
    public void displayHelp() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Checkers Help"));
        Text text = new Text("You, the human, are in control of the red pieces, and are tasked with eliminating " +
                "all of the dastardly AI's white pieces.\n\nYou go first, and can only move your pieces diagonally forward, " +
                "in two ways: to an empty adjacent square, and in a 'jump' over an enemy piece, onto an empty square 'behind' it. " +
                "This move HAS to be made if available (although you can choose which if multiple are available), and " +
                "'captures' the jumped piece, removing it from the board. If another jump move is immediately available " +
                "for the same piece, this must be made too in a multi-jump move.\n\nIf a player makes it to their opponent's " +
                "back row, the piece is crowned and becomes a king! A king can move both forwards and backwards. Very regal." +
                "\n\nA player can also be blocked and unable to move any piece, in which case they lose. Don't lose. Version " +
                "2.0 of this game will introduce artificial general intelligence, and you don't want it to see you as weak!");
        text.setWrappingWidth(480);
        dialogLayout.setBody(text);
        JFXDialog dialog = new JFXDialog(rootStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton buttonExit = new JFXButton("Okay");
        buttonExit.setButtonType(JFXButton.ButtonType.FLAT);
        buttonExit.setStyle("-fx-background-color:#DCDCDC");
        buttonExit.setOnAction(event -> dialog.close());
        dialogLayout.setActions(buttonExit);
        dialog.show();
    }

    /**
     * Converts a rectangle's ID to a Point corresponding to its location in the model board
     * @param id the rectangle's id, as s0_0
     * @return the rectangle's board location as a Point
     */
    private Point idToPoint(String id) {
        String idCopy = id;
        idCopy = idCopy.replace("s", "");
        String[] xy = idCopy.split("_");
        return new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }

    /**
     * Uses a board-level row and column input to select the corresponding Circle piece from the GUI
     * @param row the row that the Circle is on
     * @param col the column that the Circle is on
     * @return the Circle at row, col on the GUI
     */
    private Circle selectCircle(int row, int col) {
        for (Circle c : circles) {
            if (c.getLayoutY() == indexToPixel(row) && c.getLayoutX() == indexToPixel(col)) { // if Circle has correct x,y
                List<Node> boardPaneCircleList = boardPane.getChildren().subList(8, boardPane.getChildren().size());
                for (Node n : boardPaneCircleList) {
                    Circle circle = (Circle) n;
                    if (circle.getId().equals(c.getId())) {
                        return circle;
                    } else {
                        int i = Arrays.asList(boardPane.getChildren().toArray()).indexOf(c); // get actual Circles from boardPane
                        try {
                            return (Circle) Arrays.asList(boardPane.getChildren().toArray()).get(i);
                        } catch (ArrayIndexOutOfBoundsException e) {
//                            System.out.println("Circle was not found in array");
                            deleteBoardPaneCirclesNotInCircleList();
                        }
                    }
                }
            }
        } return null;
    }

    private void deleteBoardPaneCirclesNotInCircleList() {
        List<Node> boardPaneCircleList = boardPane.getChildren().subList(8, boardPane.getChildren().size());
        for (Node n : boardPaneCircleList) {
            Circle circle = (Circle) n;
            if (!circles.contains(circle)) {
                removePiece(circle);
            }
        }
    }

    /**
     * Updates the GUI with the given AI Move
     * @param move the Move to update the GUI with
     */
    private void updateAIPiece(Move move) {
        Circle circle = selectCircle(move.getOrigin().x, move.getOrigin().y); // get Circle in GUI from Move's x,y
        ArrayList<Timeline> timelineList = new ArrayList<>();

        if (move.getPreviousMoves() != null && move.getPreviousMoves().size() > 0) {
            for (Move previousMove : move.getPreviousMoves()) {
                Timeline timeline = getAIMoveTimeline(circle, previousMove);
                timelineList.add(timeline);
            }
        }

        Timeline timeline = getAIMoveTimeline(circle, move);
        timeline.setOnFinished(e -> makeKingAndRemovePieces(move, circle));
        timelineList.add(timeline);

        Iterator timelineListIterator = timelineList.iterator();
        while (timelineListIterator.hasNext()) {
            Timeline tl = (Timeline) timelineListIterator.next();
            int index = timelineList.indexOf(tl) + 1;
            if (timelineListIterator.hasNext()) {
                tl.setOnFinished(e -> play(timelineList, index));
            }
        }
        play(timelineList, 0);
    }

    private void play(ArrayList<Timeline> timelines, int index) {
        if (index != 0) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timelines.get(index).play();
    }

    private void makeKingAndRemovePieces(Move move, Circle circle) {
        // Update the GUI if a piece should become a king
        boolean setKing = false;
        if (move.getPreviousMoves() != null) {
            for (Move previousMove : move.getPreviousMoves()) {
                if (previousMove.getDestination().x == 7) {
                    setKing = true;
                }
            }
        }
        if (move.kingPiece || setKing) { // || circle.getLayoutY() == 450.0) { // make GUI piece a king if on human's home row
            makeKing(circle, 'w');
        }

        // Remove all GUI pieces that were jumped over during the move
        if (move.hasPieceToRemove()) { // remove jumped piece if applicable
            ArrayList<Timeline> timelineList = new ArrayList<>();
            for (Point pieceToRemove : move.getPiecesToRemove()) { // = move.getPiecesToRemove().get(move.getPiecesToRemove().size()-1);
                if (selectCircle(pieceToRemove.x, pieceToRemove.y) != null) {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(selectCircle(pieceToRemove.x, pieceToRemove.y).opacityProperty(), 0.0d)));
                    timeline.setOnFinished(e -> removePiece(pieceToRemove));
                    timelineList.add(timeline);
                } else {
                    removePiece(selectCircle(pieceToRemove.x, pieceToRemove.y));
                }
            }
            for (Timeline timeline : timelineList) {
                timeline.play();
            }
        }
    }

    private void removePiece(Point pieceToRemove) {
        removePiece(selectCircle(pieceToRemove.x, pieceToRemove.y));
    }

    /**
     * Removes a Circle piece from the GUI
     * @param circle the Circle to remove
     */
    private void removePiece(Circle circle) {
        try {
            boardPane.getChildren().remove(circle);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException, can't find circle to remove");
        }
    }

    /**
     * Applies the correct king image to the given Circle, after checking the colour
     * @param circle the Circle to update
     * @param player the colour of the piece
     */
    private void makeKing(Circle circle, char player) {
        Image crown = new Image((player == 'r') ? "main/res/crown_r.png" : "main/res/crown_w.png");
        circle.setFill(new ImagePattern(crown));
    }

    /**
     * Converts a board-level (eg. 0-7) index to a pixel location in the GUI
     * @param i the index to convert
     * @return the index converted to a pixel location
     */
    private int indexToPixel(int i) {
        return ((i+1)*60)-30;
    }

    /**
     * Converts a GUI pixel location to a board-level (eg. 0-7)
     * @param i the pixel location to convert
     * @return the pixel location converted to an index
     */
    private int pixelToIndex(double i) {
        return (int)Math.round((i-30)/60)+1;
    }

    /**
     * Creates and returns a Timeline animation for a valid move.
     * @param parent the parent rectangle location
     * @return the full timeline, ready to play
     */
    private Timeline getValidMoveTimeline(Point2D parent) {
        Timeline timeline = new Timeline();
        return getTimeline(timeline, selectedPiece, parent);
    }

    /**
     * Creates and returns a Timeline animation for an invalid move (ie. moving back to the piece's original location)
     * @return the full timeline, ready to play
     */
    private Timeline getInvalidMoveTimeline() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(selectedPiece.layoutXProperty(), selectedPiece.getLayoutX() - (selectedPiece.getLayoutX() - originRectangle.getLayoutX()-30)),
                        new KeyValue(selectedPiece.layoutYProperty(), selectedPiece.getLayoutY() - (selectedPiece.getLayoutY() - idToPoint(originRectangle.getId()).x*60-30)),
                        new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                ));
        return timeline;
    }

    private Timeline getTimeline(Timeline timeline, Circle parent, Point2D destination) {
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(parent.layoutXProperty(), destination.getX() + 30),
                        new KeyValue(parent.layoutYProperty(), destination.getY() + 30),
                        new KeyValue(parent.opacityProperty(), 1.0d)
                ));
        return timeline;
    }

    private Timeline getAIMoveTimeline(Circle parent, Move move) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(getAIKeyFrame(parent, getAIDestinationPoint(move)));
        return timeline;
    }

    private KeyFrame getAIKeyFrame(Circle parent, Point2D destination) {
        return new KeyFrame(Duration.millis(200),
                new KeyValue(parent.layoutXProperty(), destination.getX()),
                new KeyValue(parent.layoutYProperty(), destination.getY()));
    }

    private Point2D getAIDestinationPoint(Move move) {
        return new Point2D(indexToPixel(move.getDestination().y), indexToPixel(move.getDestination().x));
    }

    /**
     * Checks whether the given player has won or lost, and shows a relevant popup if so
     * @param colour colour of the player
     */
    private void checkForWin(char colour) {
        // TODO: Run this in FX application thread!!
        if (internalBoard.winCheck() != 0) {
            this.win = true;
            if (internalBoard.winCheck() < 0) {
                createFinishedPopup("Congratulations!", "You win! The robot uprising has been crushed!");
            } else {
                createFinishedPopup("Commiserations!", "You lose. Please welcome your new masters.");
            }
        }
        else if (colour != 'r' && ((findValidMoves('w').size() == 0) || (findValidMoves('r').size() == 0))) {
            this.win = true;
            if (colour=='w') {
                createFinishedPopup("Congratulations!", "AI is out of moves.\nYou win! The robot uprising has been crushed!");
            } else {
                createFinishedPopup("Commiserations!", "You are out of moves!\nYou lose. Please welcome your new masters.");
            }
        }
    }

    /**
     * Creates the popup shown when the game is finished using the given text. Includes exit and restart buttons.
     * @param title the dialog heading text
     * @param body the dialog body text
     */
    private void createFinishedPopup(String title, String body) {
        aiTurn = true; // stop user being able to move pieces
        JFXDialogLayout dialogLayout = new JFXDialogLayout(); // create the dialog...
        dialogLayout.setHeading(new Text(title));
        dialogLayout.setBody(new Text(body));
        JFXDialog dialog = new JFXDialog(rootStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton buttonRestart = new JFXButton("Play again"); // add restart button
        JFXButton buttonExit = new JFXButton("Exit"); // add exit button
        buttonExit.setButtonType(JFXButton.ButtonType.FLAT);
        buttonExit.setStyle("-fx-background-color:#DCDCDC");
        buttonExit.setOnAction(event -> Platform.exit()); // set exit action w/lambda
        buttonRestart.setButtonType(JFXButton.ButtonType.FLAT);
        buttonRestart.setStyle("-fx-background-color:#DCDCDC");
        buttonRestart.setOnAction(event -> { // set restart action: loads the jar again and closes the current instance
            try {
                Runtime.getRuntime().exec("java -jar checkers.jar");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
        dialogLayout.setActions(buttonExit, buttonRestart);
        dialog.setOverlayClose(false);
        dialog.show();
    }

    /**
     * Uses the MoveGenerator to return a list of valid moves for the given piece colour
     * @param colour the side to return valid moves for
     * @return list of valid Moves for the given piece colour
     */
    private ArrayList<Move> findValidMoves(char colour) {
        return moveGenerator.findValidMoves(internalBoard, colour);
    }
}
