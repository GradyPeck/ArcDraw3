import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

public class Main extends Application {
	GraphicsContext gc;
	static double anglechoice = 0;
	Arc arcanum;
	ArrayList<Arc> archon = new ArrayList<Arc>();

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene s = new Scene(root, 500, 500, Color.WHITE);
		Canvas canvas = new Canvas(500, 500);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().addAll(canvas);
		stage.setScene(s);
		stage.show();
		Point[] points = {null, null};
		
		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if(points[0] == null) {
					points[0] = new Point((int) e.getX(), (int) e.getY());
					//draw the first anchor point
					gc.fillOval(points[0].x - 2, points[0].y - 2, 4, 4);
				}
				else if(points[1] == null) {
					points[1] = new Point((int) e.getX(), (int) e.getY());
					
					//draw the second anchor point
//					gc.fillOval(points[1].x - 2, points[1].y - 2, 4, 4);

					gc.setStroke(Color.BLACK);
//					gc.strokeLine(points[0].x, points[0].y, points[1].x, points[1].y);
					archon.add(new Arc(points[0], points[1], 0, gc));
				}
				else {
					//find the slope of the previous anchor line
					double oldslope = ((double)points[0].y - (double)points[1].y)/((double)points[0].x - (double)points[1].x);
					//shuffle the points forward (and draw the new point and line)
					Point pointy = points[0];
					points[0] = points[1];
					points[1] = new Point((int) e.getX(), (int) e.getY());
					
					
//					gc.fillOval(points[1].x - 2, points[1].y - 2, 4, 4);
//					gc.setStroke(Color.BLACK);
//					gc.strokeLine(points[0].x, points[0].y, points[1].x, points[1].y);
					
					
					//find the slope of the new anchor line
					double newslope = ((double)points[0].y - (double)points[1].y)/((double)points[0].x - (double)points[1].x);
//					System.out.println(oldslope + " / " + newslope);
					//find the acute angle between them
					double betwangle = Math.abs((oldslope - newslope) / (1.0 + (oldslope * newslope)));
					betwangle = Math.toDegrees(Math.atan(betwangle));
					
					//check if the angle should be obtuse (if the opplength is longer than it would be if it was a hypotenuse)
					double opplength = findDist(pointy, points[1]);
					if(opplength > Math.sqrt(Math.pow(findDist(pointy, points[0]), 2.0) + Math.pow(findDist(points[0], points[1]), 2.0))) {
						betwangle = 180.0 - betwangle;//flip it to the obtuse counterpart
					}
					betwangle = 180.0 - betwangle;//flip the angle to where the new arc opens (the BASE ANGLE, opposite the LINE ANGLE)

					Arc prevArc = archon.get(archon.size() - 1);
					
					//check if the arc needs to be "flopped" vertically (same angle but upside down)
					boolean flop = false;
					double b = pointy.y - (oldslope * pointy.x);
					double yshould = (oldslope * points[1].x) + b;
					if (yshould > points[1].y) {
						//new point is above previous line which is UPRIGHT, open RIGHT (negative)
						betwangle = -1.0 * betwangle;//right-opening angles are negative
					}
//					}
					else if(yshould < points[1].y) {
						//new point is below previous line, which is UPRIGHT, open LEFT (positive)
					}
					//skipped: above previous which is INVERTED, open LEFT (positive)
					else {
						//colinear - ???
					}
					//if the previous line segment is right to left, reverse your "above" and "below" assessments
					if(pointy.x > points[0].x) {
						betwangle = -1.0 * betwangle;
					}
					
//					System.out.println("Betwangle: " + betwangle);
					
					double arcadjust = prevArc.inputAngle;//get the RAW INPUT ANGLE of the previous arc
					betwangle = betwangle - arcadjust;//subtract the previous arc's angle from the base angle
					
//					System.out.println("Previous Arc: " + arcadjust);
//					System.out.println("Adjusted Betwangle: " + betwangle);
					
					archon.add(new Arc(points[0], points[1], betwangle, gc));
				}
			}
		});
	}
	
	double findDist(Point p1, Point p2) {
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		double dist = Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
		return dist;
	}
	
	
}
