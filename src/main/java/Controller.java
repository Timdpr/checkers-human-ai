package main.java;

import com.jfoenix.controls.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import main.java.model.*;

/**
 * TODO: Give explanation on rejecting invalid user moves
 * TODO: Give rules in help window
 * TODO: Fix multi-jump detection - backwards jumps are seemingly allowed
 *
 * @author tp275
 */
public class Controller implements Initializable {

    @FXML private StackPane rootStackPane;
    @FXML private Pane boardPane;
    @FXML private Pane row_a;
    @FXML private Pane row_b;
    @FXML private Pane row_c;
    @FXML private Pane row_d;
    @FXML private Pane row_e;
    @FXML private Pane row_f;
    @FXML private Pane row_g;
    @FXML private Pane row_h;
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

    private Point2D offset;

    private Circle selectedPiece;
    private Rectangle selectedRectangle;
    private Rectangle originRectangle;
    
    private boolean movingPiece;
    private double startLayoutX;
    private double startLayoutY;

    private AI ai = new AI();
    private boolean aiTurn;

    private Point humanMoveOrigin;

    private MoveGenerator moveGenerator = new MoveGenerator();
    private MoveGenerator humanMoveGenerator = new MoveGenerator();
    private MoveGenerator aiMoveGenerator = new MoveGenerator();
    private Board internalBoard;

    @FXML private JFXCheckBox highlightCheckBox;

    /**
     * Called after window has finished loading.
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
     *
     * @param mouseEvent
     */
    @FXML
    public void startMovingPiece(MouseEvent mouseEvent) {
        selectedPiece = (Circle)mouseEvent.getSource(); // get the Circle object being moved

        // Set humanMoveOrigin to the location of the piece (can't use helper method here, must stay in local method)
        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint);
        originRectangle = pickRectangle(mousePointScene.getX(), mousePointScene.getY());
        String id = originRectangle.getId();
        id = id.replace("s", "");
        String[] xy = id.split("_");
        humanMoveOrigin =  new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));

        startLayoutX = selectedPiece.getLayoutX();
        startLayoutY = selectedPiece.getLayoutY();
        selectedPiece.setOpacity(0.4d);
        offset = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        movingPiece = true;
    }

    /**
     *
     * @param mouseEvent
     */
    @FXML
    public void movePiece(MouseEvent mouseEvent) {
        Point2D mousePoint = new Point2D(mouseEvent.getX()-30, mouseEvent.getY()-30);
        Point2D mousePointScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        if( !inBoard(mousePointScene) || aiTurn) { // check for piece being in board or it not being human's turn
            return;  // don't relocate() b/c will resize Pane
        }
        if (highlightCheckBox.isSelected()) {
            highlightValidMove(mouseEvent);
        }
        Point2D mousePoint_p = selectedPiece.localToParent(mousePoint);
        selectedPiece.relocate(mousePoint_p.getX()-offset.getX(), mousePoint_p.getY()-offset.getY());
    }

    private void highlightValidMove(MouseEvent mouseEvent) {
        // Make square green if dropping the piece here would be a valid move
        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint);
        Rectangle r = pickRectangle(mousePointScene.getX(), mousePointScene.getY());
        if (r == null) {
            System.out.println("Rectangle selection was null in movePiece.");
        } else {
            Point destination = idToPoint(r.getId());
            if (findValidMoves('r').contains(new Move(humanMoveOrigin, destination))) {
                highlightSquareGreen(mouseEvent);
            }
        }
    }

    /**
     * Checks whether the given point is within the bounds of the board's base pane
     * @param pt The point to check
     * @return True if within bounds, else false
     */
    private boolean inBoard(Point2D pt) {
        Point2D panePt = boardPane.sceneToLocal(pt);
        return panePt.getX()-offset.getX() >= 0.0d
                && panePt.getY()-offset.getY() >= 0.0d
                && panePt.getX() <= boardPane.getWidth()
                && panePt.getY() <= boardPane.getHeight();
    }

    /**
     *
     * @param mouseEvent
     */
    public void finishMovingPiece(MouseEvent mouseEvent) {
        offset = new Point2D(0.0d, 0.0d);
        // Get cursor's location in the scene
        Point2D mousePointScene = selectedPiece.localToScene(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        Rectangle r = pickRectangle( mousePointScene.getX(), mousePointScene.getY() ); // get rectangle under cursor

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        if( r != null && !aiTurn) {
            Point2D rectScene = r.localToScene(r.getX(), r.getY()); // get pt in scene that the rectangle is at
            Point2D parent = boardPane.sceneToLocal(rectScene.getX(), rectScene.getY());
            Point destination = idToPoint(r.getId()); // get board location of the piece's location

            // Check move is valid by finding valid moves for red and checking that moves.indexOf(move) returns > -1
            ArrayList<Move> moves = findValidMoves('r');
            int moveIndex = moves.indexOf(new Move(humanMoveOrigin, destination));

            ///// HUMAN MOVE: /////
            if (moveIndex >= 0) { // if human move was valid:
                timeline = getValidMoveTimeline(timeline, parent); // create move animation
                Move playerMove = moves.get(moveIndex);
                internalBoard = internalBoard.updateLocation(playerMove); // update piece's location in internal board
                if (playerMove.hasPieceToRemove()) {
                    removePiece(selectCircle(playerMove.getPieceToRemove().x, playerMove.getPieceToRemove().y));
                }
                timeline.play(); // play animation
                System.out.println("Board after human move:");
                internalBoard.printBoard();
                checkForWin('r');

                selectedPiece.setLayoutX(indexToPixel(pixelToIndex(selectedPiece.getLayoutX())-1));
                selectedPiece.setLayoutY(indexToPixel(pixelToIndex(selectedPiece.getLayoutY())-1));
                System.out.println("######## LayoutY: " + selectedPiece.getLayoutY());
                if (selectedPiece.getLayoutY() == 30.0) {
                    makeKing(selectedPiece, 'r');
                }
                // if player's move was a jump move, and there is an available jump move with it's origin at the original move's destination
                if (playerMove.hasPieceToRemove() && (humanMoveGenerator.detectMultiMove(internalBoard, 'r', playerMove.getDestination()).size() > 0)) {
                    aiTurn = false;
                    turnText.setText(" Human multi-jump!");
                } else {
                    aiTurn = true;
                }

            } else { // if move is not valid, move the circle back to its original position
                timeline = getInvalidMoveTimeline(timeline);
                timeline.play();
            }
        // return circle's opacity to 0 if move unsuccessful because it was the ai's turn
        } else {
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                    )
            );
            timeline.play();
        }
        movingPiece = false;

        ///// AI MOVE: /////
        if (aiTurn) {
            AIMove();
        }
    }



    /**
     *
     */
    private void AIMove() {
        turnText.setText(" AI");
        Move aiMove = ai.play(new Board(internalBoard.getBoard()), (int)sliderDifficulty.getValue(), findValidMoves('w'));
        internalBoard = internalBoard.updateLocation(aiMove);
        System.out.println("\nBoard after AI's move:");
        internalBoard.printBoard();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updatePiece(aiMove);
        checkForWin('w');
        if ((aiMoveGenerator.detectMultiMove(internalBoard, 'w', aiMove.getDestination()).size() > 0) && aiMove.hasPieceToRemove()) {
            aiTurn = true;
            turnText.setText(" AI multi-jump!");
            AIMove();
        } else {
            aiTurn = false;
            turnText.setText(" Human");
        }
    }

    /**
     *
     * @param evt
     * @return
     */
    private Rectangle pickRectangle(MouseEvent evt) {
        return pickRectangle(evt.getSceneX(), evt.getSceneY());
    }

    /**
     *
     * @param sceneX
     * @param sceneY
     * @return
     */
    private Rectangle pickRectangle(double sceneX, double sceneY) {
        Rectangle pickedRectangle = null;
        for( Pane row : panes ) {
            Point2D mousePoint = new Point2D(sceneX, sceneY);
            Point2D mpLocal = row.sceneToLocal(mousePoint);
            if( row.contains(mpLocal) ) {
                for( Node cell : row.getChildrenUnmodifiable() ) {
                    Point2D mpLocalCell = cell.sceneToLocal(mousePoint);
                    if( cell.contains(mpLocalCell) ) {
                        pickedRectangle = (Rectangle)cell;
                        break;
                    }}
                break;
            }}
        return pickedRectangle;
    }

    /**
     *
     * @param evt
     */
    public void checkReleaseOutOfBoard(MouseEvent evt) {
        Point2D mousePoint_s = new Point2D(evt.getSceneX(), evt.getSceneY());
        if( !inBoard(mousePoint_s) ) {
            leaveBoard(evt);
            evt.consume();
        }
    }

    /**
     *
     * @param evt
     */
    public void highlightSquare(MouseEvent evt) {
        Rectangle r = pickRectangle(evt);
        if( r == null ) {
            if( selectedRectangle != null ) {
                // deselect previous
                selectedRectangle.setEffect( null );
            }
            selectedRectangle = null;
            return;  // might be out of area but w/i scene
        }
        if( r != selectedRectangle ) {
            if( selectedRectangle != null ) {
                // deselect previous
                selectedRectangle.setEffect( null );
            }
            // new selection
            selectedRectangle = r;
            selectedRectangle.setEffect(new InnerShadow());
        }
    }

    /**
     *
     * @param mouseEvent
     */
    private void highlightSquareGreen(MouseEvent mouseEvent) {
        Rectangle r = pickRectangle(mouseEvent);
        if( r != selectedRectangle ) {
            if( selectedRectangle != null ) {
                // deselect previous
                selectedRectangle.setEffect( null );
            }
            // new selection
            selectedRectangle = r;
            ColorInput color = new ColorInput();
            color.setPaint(Color.color(0,1,0, 0.6));
            color.setHeight(60);
            color.setWidth(60);
            selectedRectangle.setEffect(color);
        }
    }

    /**
     *
     * @param mouseEvent
     */
    public void leaveBoard(MouseEvent mouseEvent) {
        if( movingPiece ) {
            final Timeline timeline = new Timeline();
            offset = new Point2D(0.0d, 0.0d);
            movingPiece = false;
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
     *
     * @param actionEvent
     */
    public void displayHelp(ActionEvent actionEvent) {
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
     *
     * @param id
     * @return
     */
    private Point idToPoint(String id) {
        String idCopy = id;
        idCopy = idCopy.replace("s", "");
        String[] xy = idCopy.split("_");
        return new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }

    /**
     * @param row
     * @param col
     * @return
     */
    private Circle selectCircle(int row, int col) {
        for (Circle c : circles) {
            if (c.getLayoutY() == indexToPixel(row) && c.getLayoutX() == indexToPixel(col)) {
                int i = Arrays.asList(boardPane.getChildren().toArray()).indexOf(c);
                try {
                    return (Circle) Arrays.asList(boardPane.getChildren().toArray()).get(i);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    System.out.println("Circle was not found in array");
                }
            }
        }
        return null;
    }

    /**
     *
     * @param move
     */
    private void updatePiece(Move move) {
        Circle circle = selectCircle(move.getOrigin().x, move.getOrigin().y);
        circle.setLayoutY(indexToPixel(move.getDestination().x));
        circle.setLayoutX(indexToPixel(move.getDestination().y));
        if (move.hasPieceToRemove()) {
            removePiece(selectCircle(move.getPieceToRemove().x, move.getPieceToRemove().y));
        }
        if (circle.getLayoutY() == 450.0) {
            makeKing(circle, 'w');
        }
    }

    /**
     *
     * @param circle
     */
    private void removePiece(Circle circle) {
        try {
            boardPane.getChildren().remove(circle);
            circle.setOpacity(0.0);
            circle.setRadius(0.0);
            circle.setLayoutX(-99.0);
            circle.setLayoutY(-99.0);
            circle.setDisable(true);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException, can't find circle to remove");
        }
    }

    /**
     *
     * @param circle
     */
    private void makeKing(Circle circle, char player) {
        Image crown = new Image((player == 'r') ? "main/res/crown_r.png" : "main/res/crown_w.png");
        circle.setFill(new ImagePattern(crown));
    }

    /**
     *
     * @param i
     * @return
     */
    private int indexToPixel(int i) {
        return ((i+1)*60)-30;
    }

    /**
     *
     * @param i
     * @return
     */
    private int pixelToIndex(double i) {
        return (int)Math.round((i-30)/60)+1;
    }

    /**
     *
     * @param timeline
     * @param parent
     * @return
     */
    private Timeline getValidMoveTimeline(Timeline timeline, Point2D parent) {
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(selectedPiece.layoutXProperty(), parent.getX() + 30),
                        new KeyValue(selectedPiece.layoutYProperty(), parent.getY() + 30),
                        new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                ));
        return timeline;
    }

    /**
     *
     * @param timeline
     * @return
     */
    private Timeline getInvalidMoveTimeline(Timeline timeline) {
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(selectedPiece.layoutXProperty(), selectedPiece.getLayoutX() - (selectedPiece.getLayoutX() - originRectangle.getLayoutX()-30)),
                        new KeyValue(selectedPiece.layoutYProperty(), selectedPiece.getLayoutY() - (selectedPiece.getLayoutY() - idToPoint(originRectangle.getId()).x*60-30)),
                        new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                ));
        return timeline;
    }

    /**
     *
     */
    public void checkForWin(char colour) {
        if (findValidMoves(colour).size() == 0) {
            if (colour=='w') {
                createFinishedPopup("Congratulations!", "AI is out of moves.\nYou win! The robot uprising has been crushed!");
            } else {
                createFinishedPopup("Commiserations!", "You are out of moves!\nYou lose. Please welcome your new masters.");
            }
        }
        if (internalBoard.winCheck() != 0) {
            aiTurn = true; // pause the game
            if (internalBoard.winCheck() > 0) {
                createFinishedPopup("Congratulations!", "You win! The robot uprising has been crushed!");
            } else {
                createFinishedPopup("Commiserations!", "You lose. Please welcome your new masters.");
            }
        }
    }

    /**
     *
     * @param title
     * @param body
     */
    private void createFinishedPopup(String title, String body) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text(title));
        dialogLayout.setBody(new Text(body));
        JFXDialog dialog = new JFXDialog(rootStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton buttonRestart = new JFXButton("Play again");
        JFXButton buttonExit = new JFXButton("Exit");
        buttonExit.setButtonType(JFXButton.ButtonType.FLAT);
        buttonExit.setStyle("-fx-background-color:#DCDCDC");
        buttonExit.setOnAction(event -> Platform.exit());
        buttonRestart.setButtonType(JFXButton.ButtonType.FLAT);
        buttonRestart.setStyle("-fx-background-color:#DCDCDC");
        buttonRestart.setOnAction(event -> restart());
        dialogLayout.setActions(buttonExit, buttonRestart);
        dialog.show();
    }

    /**
     *
     */
    private void restart(){
        try {
            FXMLLoader.load(getClass().getResource("main/res/sample.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Move> findValidMoves(char colour) {
        if (colour == 'r') {
            return humanMoveGenerator.findValidMoves(internalBoard, colour);
        } else {
            return aiMoveGenerator.findValidMoves(internalBoard, colour);
        }

    }
}
