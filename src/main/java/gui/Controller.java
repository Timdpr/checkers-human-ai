package main.java.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private StackPane rootStackPane;

    @FXML private JFXButton buttonHelp;

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
    
    private boolean movingPiece;
    private double startLayoutX;
    private double startLayoutY;

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
    }

    @FXML
    public void startMovingPiece(MouseEvent mouseEvent) {
        selectedPiece = (Circle)mouseEvent.getSource();
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

        if( !inBoard(mousePoint_s) ) {
            return;  // don't relocate() b/c will resize Pane
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
        Point2D mousePointScene = selectedPiece.localToScene(mousePoint);

        Rectangle r = pickRectangle( mousePointScene.getX(), mousePointScene.getY() );

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        if( r != null ) {

            Point2D rectScene = r.localToScene(r.getX(), r.getY());
            Point2D parent = boardPane.sceneToLocal(rectScene.getX(), rectScene.getY());

            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(selectedPiece.layoutXProperty(), parent.getX()+30),
                            new KeyValue(selectedPiece.layoutYProperty(), parent.getY()+30),
                            new KeyValue(selectedPiece.opacityProperty(), 1.0d)
                    )
            );
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

    @FXML
    public void displayHelp(ActionEvent actionEvent) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Help"));
        dialogLayout.setBody(new Text("Some help text, you dummy!"));

        JFXDialog dialog = new JFXDialog(rootStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);

        JFXButton buttonExit = new JFXButton("Okay");
        buttonExit.setButtonType(JFXButton.ButtonType.RAISED);
        buttonExit.setOnAction(event -> dialog.close());

        dialogLayout.setActions(buttonExit);
        dialog.show();
    }
}
