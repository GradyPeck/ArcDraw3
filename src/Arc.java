import java.awt.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;

public class Arc {
	Point[] points = {null, null};
	double inputAngle;
	boolean flop;

	public Arc(Point p1, Point p2, double angleA,  GraphicsContext gc) {
		points[0] = p1;
		points[1] = p2;
		inputAngle = angleA;
//		if(inputAngle < 0) {
//			inputAngle = inputAngle * -1.0;
//			flup = !flup;
//		}
		DrawMe(inputAngle, gc);
	}
	
	public void DrawMe(double angleA, GraphicsContext gc) {
		
		double Jx = (double)points[0].x;
		double Jy = (double)points[0].y;
		double Kx = (double)points[1].x;
		double Ky = (double)points[1].y;
		boolean flip = false;
		
		//save the original transform
		Affine tx = gc.getTransform();
		
		if(angleA > 180.0) {
			angleA = -360.0 + angleA;
			inputAngle = angleA;
		}
		else if(angleA < -180.0) {
			angleA = 360.0 + angleA;
			inputAngle = angleA;
		}
		
		double angleC = 180 - (2.0 * angleA);
		double littleC = Math.sqrt(
				Math.pow(Math.abs(Jx - Kx), 2.0) +
				Math.pow(Math.abs(Jy - Ky), 2.0));
		
		if(Math.abs(angleA) > 90.0) {
			flip = true;
			if(angleA > 0) angleA = 180.0 - angleA;
			else angleA = -180.0 - angleA;
		}
		
		if(angleA < 0) {
			angleA = Math.abs(angleA);
			flop = !flop;
		}
		
		if(angleA == 90.0) {
			//alternate mode for circles (special case)
			//move the origin point to point J
			gc.translate(Jx, Jy);
			
			//rotate into alignment with anchor line
			double roto = Math.toDegrees(Math.atan((Jy - Ky)/(Jx - Kx)));
//			if(Jx > Kx) roto = roto + 180.0;
			gc.rotate(roto);
			
			//draw the circular arc
			gc.setStroke(Color.RED);
			gc.strokeArc(0.0, littleC/-2.0, littleC, littleC, 0.0, 180.0, ArcType.OPEN);
			
			//reset the transform
			gc.setTransform(tx);
			return;
		}
		
		double sideA = Math.abs(2.0 * Math.sin(Math.toRadians(angleA))*(littleC / Math.sin(Math.toRadians(angleC))));
		double sideC = 2.0 * littleC;
		double Haxis = 0;
		double Vaxis = 0;
		double Sa = Math.max(Math.sqrt(3.0)/6.0*sideC, (1.0/6.0)*Math.sqrt(4.0*Math.pow(sideA, 2.0) - Math.pow(sideC, 2.0)));
		double Sb = Math.min(Math.sqrt(3.0)/6.0*sideC, (1.0/6.0)*Math.sqrt(4.0*Math.pow(sideA, 2.0) - Math.pow(sideC, 2.0)));
		
		if(angleA > 60) {
			Vaxis = Sa;
			Haxis = Sb;
		}
		else {
			Vaxis = Sb;
			Haxis = Sa;
		}
		
		//relative coordinates from this point onwards - J is the origin, the anchor line is the X axis
		
		//move the origin point to point J
		gc.translate(Jx, Jy);
		
		//rotate into alignment with anchor line
		double roto = Math.toDegrees(Math.atan((Jy - Ky)/(Jx - Kx)));
		if(Jx > Kx) roto = roto + 180.0;
		gc.rotate(roto);
		

		//redefine point K relative to point J
		Kx = littleC;
		Ky = 0;
		
		//TEST: draw the anchor line
		gc.strokeLine(0, 0, Kx, Ky);
		
		//Find points L and O relative to point J (Lx == Ox)
		double Ox = (littleC / 2.0);
		double Ly = Math.sin(Math.toRadians(angleA))*(sideA / 2.0);
		double Oy = Ly - Vaxis;
		
		//x = a cos(theta)    y = b sin(theta)
		double preSweep = Math.toDegrees(Math.acos(Ox/Haxis));
		double sweep = 180 - Math.abs(2.0 * preSweep);
		
		if(flip) {
			gc.translate(littleC/2.0, 0);
			gc.rotate(-180.0);
			gc.translate(littleC/-2.0, 0);
//			Oy = Oy * -1.0;
			preSweep = preSweep + sweep;
			sweep = 360.0 - sweep;
		}
		
		if(flop) {
			gc.translate(littleC/2.0, 0);
			gc.rotate(-180.0);
			gc.translate(littleC/-2.0, 0);
//			System.out.println("flopped");
		}
		
		//TEST: draw point O
//		gc.setFill(Color.GREEN);
//		gc.fillOval(Ox - 1, Oy - 1, 2, 2);
//		gc.setFill(Color.BLACK);
		
		//TEST: print out values from this run
//		System.out.println(preSweep);
//		System.out.println(sweep);
		
		//TEST: draw the full ellipse
//		gc.setStroke(Color.BLACK);
//		gc.strokeOval(Ox - Haxis, Oy - Vaxis, Haxis*2.0, Vaxis*2.0);
		
		//draw the arc
		gc.setStroke(Color.RED);
		gc.strokeArc(Ox - Haxis, Oy - Vaxis, Haxis*2.0, Vaxis*2.0, preSweep, sweep, ArcType.OPEN);
		gc.setStroke(Color.BLACK);
		
		gc.setTransform(tx);
	}
}
