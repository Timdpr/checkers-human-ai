package main.java;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import main.java.model.*;

/**
 * Methods related to 'drag and drop' functionality heavily adapted from
 * www.bekwam.blogspot.co.uk/2016/02/moving-game-piece-on-javafx-checkerboard.html, with permission under the Apache
 * License, Version 2.0: www.apache.org/licenses/LICENSE-2.0
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

    @FXML
    private Circle c1;
    @FXML
    private Circle c2;
    @FXML
    private Circle c3;
    @FXML
    private Circle c4;
    @FXML private Circle c5;
    @FXML private Circle c6;
    @FXML private Circle c7;
    @FXML private Circle c8;
    @FXML private Circle c9;
    @FXML private Circle c10;
    @FXML private Circle c11;
    @FXML private Circle c12;
    @FXML private Circle c13;
    @FXML private Circle c14;
    @FXML private Circle c15;
    @FXML private Circle c16;
    @FXML private Circle c17;
    @FXML private Circle c18;
    @FXML private Circle c19;
    @FXML private Circle c20;
    @FXML private Circle c21;
    @FXML private Circle c22;
    @FXML private Circle c23;
    @FXML private Circle c24;
    private List<Circle> circles = new ArrayList<>();


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
    private Board board = new Board();

    /**
     * Called after window has finished loading.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panes.addAll(Arrays.asList(row_a, row_b, row_c, row_d, row_e, row_f, row_g, row_h));
        circles.addAll(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18,
                c19, c20, c21, c22, c23, c24));
        aiTurn = false;
    }

    /**
     *
     * @param mouseEvent
     */
    @FXML
    public void startMovingPiece(MouseEvent mouseEvent) {
        selectedPiece = (Circle)mouseEvent.getSource(); // get the Circle object being moved
        System.out.println("Initial location of moved piece: " + selectedPiece.getLayoutY() + " - " + selectedPiece.getLayoutX());

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
        Point2D mousePoint_s = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        if( !inBoard(mousePoint_s) || aiTurn) { // check for piece being in board or it not being human's turn
            return;  // don't relocate() b/c will resize Pane
        }

        // Make square green if dropping the piece here would be a valid move
        Point2D mousePt = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePt);
        Rectangle r = pickRectangle(mousePointScene.getX(), mousePointScene.getY());
        if (r == null) {
            System.out.println("Here's a big problem! Rectangle selection was null in movePiece. " +
                    "This may be the cause of all future circle deletion problems, as it seems there are none before this.");
        }
        Point destination = idToPoint(r.getId());
        if (moveGenerator.findValidMoves(board, 'r').contains(new Move(humanMoveOrigin, destination))) {
            highlightSquareGreen(mouseEvent);
        }

        Point2D mousePoint_p = selectedPiece.localToParent(mousePoint);
        selectedPiece.relocate(mousePoint_p.getX()-offset.getX(), mousePoint_p.getY()-offset.getY());
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

        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint); // get point in scene that the cursor is over
        Rectangle r = pickRectangle( mousePointScene.getX(), mousePointScene.getY() ); // get rectangle under cursor

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        if( r != null && !aiTurn) {
            Point2D rectScene = r.localToScene(r.getX(), r.getY());
            Point2D parent = boardPane.sceneToLocal(rectScene.getX(), rectScene.getY());
            Point destination = idToPoint(r.getId());

            // Check move is valid by finding valid moves for red and checking that moves.indexOf(move) returns > -1
            ArrayList<Move> moves = moveGenerator.findValidMoves(board, 'r');
            int moveIndex = moves.indexOf(new Move(humanMoveOrigin, destination));

            ///// HUMAN MOVE: /////
            if (moveIndex >= 0) { // if human move was valid:
                timeline = getValidMoveTimeline(timeline, parent); // create move animation
                Move playerMove = moves.get(moveIndex);
                board.updateLocation(playerMove); // update piece's location in internal board
                if (playerMove.hasPieceToRemove()) {
                    removePiece(selectCircle(playerMove.getPieceToRemove().x, playerMove.getPieceToRemove().y));
                }
                System.out.println("Board after human move:");
                board.printBoard();
                timeline.play(); // play animation
                selectedPiece.setLayoutX(indexToPixel(pixelToIndex(selectedPiece.getLayoutX())));
                selectedPiece.setLayoutY(indexToPixel(pixelToIndex(selectedPiece.getLayoutY())));
                System.out.println(selectedPiece.getLayoutY());
                if (selectedPiece.getLayoutY() == 30.0) {
                    makeKing(selectedPiece);
                }
                System.out.println("Updated location of moved piece: " + selectedPiece.getLayoutY() + " - " + selectedPiece.getLayoutX());

                ///// AI MOVE: /////
                AIMove();

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
    }

    /**
     *
     */
    private void AIMove() {
        aiTurn = true;
        board = ai.play(board);
        System.out.println("\nBoard after AI's move:");
        board.printBoard();
        updatePiece(ai.getLastMove());
        aiTurn = false;
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
                    }
                }
                break;
            }
        }
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
            color.setHeight(60);
            color.setWidth(60);
            color.setPaint(Color.GREEN);
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
        dialogLayout.setHeading(new Text("Help"));
        dialogLayout.setBody(new Text("Some help text, you dummy!"));
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
     *
     * @param row
     * @param col
     * @return
     */
    private Circle selectCircle(int row, int col) {
        for (Circle c : circles) {
            if (c.getLayoutY() == indexToPixel(row+1) && c.getLayoutX() == indexToPixel(col+1)) {
                return c;
            }
        }
        return null;
    }

    /**
     *
     * @param move
     */
    private void updatePiece(Move move) {
        // TODO: selectCircle, then move x and y of circle according to the Move (convert x coord into pixel x by (x*60)+30)
        // TODO: Many issues arising from matrix indexing starting from zero and all that....
        Circle circle = selectCircle(move.getOrigin().x, move.getOrigin().y);
        circle.setLayoutY(indexToPixel(move.getDestination().x+1));
        circle.setLayoutX(indexToPixel(move.getDestination().y+1));
        if (circle.getLayoutY() == 450.0) {
            makeKing(circle);
        }
        if (move.hasPieceToRemove()) {
            removePiece(selectCircle(move.getPieceToRemove().x, move.getPieceToRemove().y));
        }
    }

    /**
     *
     * @param circle
     */
    private void removePiece(Circle circle) {
        try {
            circle.setOpacity(0.0);
            boardPane.getChildren().remove(circle);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException, can't find circle to remove");
        }


    }

    /**
     *
     * @param circle
     */
    private void makeKing(Circle circle) {
        circle.setStyle("-fx-effect: innershadow( one-pass-box , gold , 15 , 0.0 , 2 , 2 )");
    }

    /**
     *
     * @param i
     * @return
     */
    private int indexToPixel(int i) {
        return (i*60)-30;
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
}
