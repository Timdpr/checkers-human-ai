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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import main.java.model.Board;
import main.java.model.Move;
import main.java.model.MoveGenerator;
import main.java.model.MoveValidator;

/**
 * Methods related to 'drag and drop' functionality adapted from
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

    private Point2D offset;

    private Circle selectedPiece;
    private Rectangle selectedRectangle;
    private Rectangle originRectangle;
    
    private boolean movingPiece;
    private double startLayoutX;
    private double startLayoutY;

    private boolean aiTurn;

    private Point humanMoveOrigin;

    private MoveValidator validator = new MoveValidator();
    private MoveGenerator moveGenerator = new MoveGenerator();
    private Board board = new Board();

    /**
     * Called after window has finished loading.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panes.add(row_a);
        panes.add(row_b);
        panes.add(row_c);
        panes.add(row_d);
        panes.add(row_e);
        panes.add(row_f);
        panes.add(row_g);
        panes.add(row_h);

        aiTurn = false;

        board.printBoard();

        MoveGenerator moves = new MoveGenerator();
        for (Move m : moves.findValidMoves(board, 'r')) {
            System.out.println("Red: " + m.getOrigin() + " - " + m.getDestination());
            if (m.hasPieceToRemove()) {
                System.out.println("Piece to remove: " + m.getPieceToRemove());
            }
        }

        for (Move m : moves.findValidMoves(board, 'w')) {
            System.out.println("White: " + m.getOrigin() + " - " + m.getDestination());
            if (m.hasPieceToRemove()) {
                System.out.println("Piece to remove: " + m.getPieceToRemove());
            }
        }
    }

    @FXML
    public void startMovingPiece(MouseEvent mouseEvent) {
        selectedPiece = (Circle)mouseEvent.getSource(); // get the Circle object being moved

        // set humanMoveOrigin to the location of the piece (can't use helper method here, must stay in local method)
        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint);
        originRectangle = pickRectangle(mousePointScene.getX(), mousePointScene.getY());
        String id = originRectangle.getId();
        id = id.replace("s", "");
        String[] xy = id.split("_");
        humanMoveOrigin =  new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
        System.out.println(humanMoveOrigin);

        startLayoutX = selectedPiece.getLayoutX();
        startLayoutY = selectedPiece.getLayoutY();
        selectedPiece.setOpacity(0.4d);
        offset = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        movingPiece = true;
    }

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
        Rectangle r = pickRectangle( mousePointScene.getX(), mousePointScene.getY() );
        Point2D rectScene = r.localToScene(r.getX(), r.getY());
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

    public void finishMovingPiece(MouseEvent mouseEvent) {
        offset = new Point2D(0.0d, 0.0d);

        Point2D mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint); // get point in scene that the cursor is over
        Rectangle r = pickRectangle( mousePointScene.getX(), mousePointScene.getY() ); // get rectangle under cursor

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        if( r != null && !aiTurn) {
            Point2D rectScene = r.localToScene(r.getX(), r.getY());
            Point2D parent = boardPane.sceneToLocal(rectScene.getX(), rectScene.getY());
            Point destination = idToPoint(r.getId());

            // Check move is valid by finding valid moves for red and checking whether the list contains that move
            if (moveGenerator.findValidMoves(board, 'r').contains(new Move(humanMoveOrigin, destination))) {
                // If move was valid, place it in the middle of the parent rectangle and update the internal board

                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(selectedPiece.layoutXProperty(), parent.getX() + 30),
                                new KeyValue(selectedPiece.layoutYProperty(), parent.getY() + 30),
                                new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                        )
                );
                board.updateLocation(humanMoveOrigin, destination); // update piece's location in internal board
                board.printBoard();
            } else { // if move is not valid, move the circle back to its original position
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(selectedPiece.layoutXProperty(), selectedPiece.getLayoutX() - (selectedPiece.getLayoutX() - originRectangle.getLayoutX()-30)),
                                new KeyValue(selectedPiece.layoutYProperty(), selectedPiece.getLayoutY() - (selectedPiece.getLayoutY() - idToPoint(originRectangle.getId()).x*60-30)),
                                new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                        )
                );

                board.printBoard();
            }

        // return circle's opacity to 0 if move unsuccessful because it was the ai's turn
        } else {
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                    )
            );
        }


        timeline.play();
        movingPiece = false;
    }

    private Rectangle pickRectangle(MouseEvent evt) {
        return pickRectangle(evt.getSceneX(), evt.getSceneY());
    }

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

    public void checkReleaseOutOfBoard(MouseEvent evt) {
        Point2D mousePoint_s = new Point2D(evt.getSceneX(), evt.getSceneY());
        if( !inBoard(mousePoint_s) ) {
            leaveBoard(evt);
            evt.consume();
        }
    }

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
            selectedRectangle = r;
            if( selectedRectangle != null ) {  // new selection
                selectedRectangle.setEffect(new InnerShadow());
            }
        }
    }

    public void highlightSquareGreen(MouseEvent evt) {
        Rectangle r = pickRectangle(evt);
        if( r != selectedRectangle ) {
            if( selectedRectangle != null ) {
                // deselect previous
                selectedRectangle.setEffect( null );
            }
            selectedRectangle = r;
            if( selectedRectangle != null ) {  // new selection
                ColorInput color = new ColorInput();
                color.setHeight(60);
                color.setWidth(60);
                color.setPaint(Color.GREEN);
                selectedRectangle.setEffect(color);
            }
        }
    }

    public void leaveBoard(MouseEvent evt) {
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

    private Point idToPoint(String id) {
        String idCopy = id;
        idCopy = idCopy.replace("s", "");
        String[] xy = idCopy.split("_");
        return new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }
}
