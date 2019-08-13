/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add SlopesAbs Function
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.equations;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.exceptions.GaussJordanError;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import org.eclipse.chemclipse.numeric.internal.gaussjordan.GaussJordan;

/**
 * This class serves some static methods to create equations in many ways.<br/>
 * 
 * @author eselmeister
 */
public class Equations {

	public static final Logger logger = Logger.getLogger(Equations.class);

	/**
	 * This method returns a linear equation described by the two points p1 and
	 * p2.<br/>
	 * If p1 or p2 is null, the method will return null.
	 * 
	 * @param p1
	 * @param p2
	 * @return {@link LinearEquation}
	 */
	public static LinearEquation createLinearEquation(IPoint p1, IPoint p2) {

		double a;
		double b;
		a = calculateSlope(p1, p2);
		b = p1.getY() - a * p1.getX();
		return new LinearEquation(a, b);
	}

	/**
	 * Calculates the slope which can be determined by the given two points.<br/>
	 * The points p1 and p2 must not be null.<br/>
	 * If yes, 0 will be returned.
	 * 
	 * @param p1
	 * @param p2
	 * @return double
	 */
	public static double calculateSlope(IPoint p1, IPoint p2) {

		double a;
		/*
		 * p1 and p2 must be an object.
		 */
		if(p1 == null || p2 == null) {
			return 0.0d;
		}
		double divider = p2.getX() - p1.getX();
		if(divider == 0) {
			a = 0;
		} else {
			a = (p2.getY() - p1.getY()) / divider;
		}
		return a;
	}

	/**
	 * Calculates the slope which can be determined by the given two points, but with an absolute X value
	 * 
	 * @param p1
	 * @param p2
	 * @return the slope with absolute x values
	 */
	public static double calculateSlopeAbs(IPoint p1, IPoint p2) {

		double divider = Math.abs(p2.getX() - p1.getX());
		if(divider == 0) {
			return 0;
		} else {
			return (p2.getY() - p1.getY()) / divider;
		}
	}

	/**
	 * In this case a LinearEquation will be created out of a point array.<br/>
	 * The best fitting equation will be calculated, using GaussJordan
	 * transformation.<br/>
	 * If a point value is null, 0 will be used instead. Be aware of. If
	 * something has gone wrong, null will be returned.
	 * 
	 * @param points
	 * @return {@link LinearEquation}
	 */
	public static LinearEquation createLinearEquation(IPoint[] points) {

		/*
		 * f(x) = ax + b f(x) x ------ 4 4 1 7 3 6 5 2 {1,0} = {7,1} In case for
		 * a linear equation use for the array a: double[][] valuesA = {{4,1},
		 * {7,1}, {6,1}, {2,1}}; double[][] valuesB = {{4,0}, {1,0}, {3,0},
		 * {5,0}}; The result will be: f(x) = -0.73x + 6.71
		 */
		// -----------------------------InitializeValues
		double[][] valuesA = new double[points.length][2];
		double[][] valuesB = new double[points.length][2];
		double x = 0.0;
		double y = 0.0;
		IPoint p;
		for(int i = 0; i < points.length; i++) {
			p = points[i];
			/*
			 * Check whether p is null or not and set the x,y values.
			 */
			if(p == null) {
				x = 0.0;
				y = 0.0;
			} else {
				x = p.getX();
				y = p.getY();
			}
			valuesA[i][0] = x;
			valuesA[i][1] = 1.0;
			valuesB[i][0] = y;
		}
		// -----------------------------InitializeValues
		// -----------------------------SolveEquation
		GaussJordan gj = new GaussJordan();
		double[][] a = gj.AtA(valuesA);
		double[][] b = gj.AtB(valuesA, valuesB);
		try {
			double[] result = gj.solve(a, b);
			return new LinearEquation(result[0], result[1]);
		} catch(GaussJordanError e) {
			logger.warn(e);
			return null;
		}
		// -----------------------------SolveEquation
	}

	/**
	 * Calculates the width between the two points.<br/>
	 * 
	 * @param p1
	 * @param p2
	 * @return double
	 */
	public static double calculateWidth(IPoint p1, IPoint p2) {

		/*
		 * Use Pythagoras :-) ...
		 */
		double a = p2.getY() - p1.getY();
		double b = p2.getX() - p1.getX();
		double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		return c;
	}

	/**
	 * Calculates the intersection of both equations.<br/>
	 * If there is no intersection, a SolverException will be thrown.
	 * 
	 * @param eq1
	 * @param eq2
	 * @throws SolverException
	 * @return IPoint
	 */
	public static IPoint calculateIntersection(LinearEquation eq1, LinearEquation eq2) throws SolverException {

		/*
		 * f(x) = ax + b f(x) = cx + d x = (d - b) / (a - c) y = ax + b
		 */
		if(eq1.equals(eq2)) {
			throw new SolverException("There could be no intersection found. The equations are congruent.");
		}
		double denominator = eq1.getA() - eq2.getA();
		double numerator = eq2.getB() - eq1.getB();
		if(denominator == 0.0d) {
			throw new SolverException("There could be no intersection found. The equations are parallel.");
		}
		double x = numerator / denominator;
		double y = eq1.calculateY(x);
		return new Point(x, y);
	}

	// TODO implementieren
	/*
	 * public static QuadraticEquation createQuadraticEquation(IPoint p1, IPoint
	 * p2, IPoint p3) { double a = 0, b = 0, c = 0;
	 * System.out.println("Quadratic implementieren"); return new
	 * QuadraticEquation(a, b, c); }
	 */
	/**
	 * Returns a quadratic equations which fits best to the given point array.<br/>
	 * The system will be solved using the gauss jordan algorithm.
	 * 
	 * @param points
	 * @return {@link QuadraticEquation}
	 */
	public static QuadraticEquation createQuadraticEquation(IPoint[] points) {

		/*
		 * f(x) = ax^2 + bx + c f(x) x ------ 4 4 1 7 3 6 5 2 {1,0,0} = {49,7,1}
		 * In case for a linear equation use for the array a: double[][] valuesA
		 * = {{16,4,1}, {49,7,1}, {36,6,1}, {4,2,1}}; double[][] valuesB =
		 * {{4,0,0}, {1,0,0}, {3,0,0}, {5,0,0}}; The result will be: f(x) =
		 * -0.73x + 6.71
		 */
		// -----------------------------InitializeValues
		double[][] valuesA = new double[points.length][3];
		double[][] valuesB = new double[points.length][3];
		double x = 0.0;
		double y = 0.0;
		IPoint p;
		for(int i = 0; i < points.length; i++) {
			p = points[i];
			/*
			 * Check whether p is null or not and set the x,y values.
			 */
			if(p == null) {
				x = 0.0;
				y = 0.0;
			} else {
				x = p.getX();
				y = p.getY();
			}
			/*
			 * f(x) = ax^2 + bx + c
			 */
			valuesA[i][0] = Math.pow(x, 2);
			valuesA[i][1] = x;
			valuesA[i][2] = 1.0;
			valuesB[i][0] = y;
		}
		// -----------------------------InitializeValues
		// -----------------------------SolveEquation
		GaussJordan gj = new GaussJordan();
		double[][] a = gj.AtA(valuesA);
		double[][] b = gj.AtB(valuesA, valuesB);
		// TODO siehe Equations_4_Test (wie kann ich erreichen, dass immer die
		// Regressiongerade positiv gewölbt ist?
		try {
			double[] result = gj.solve(a, b);
			return new QuadraticEquation(result[0], result[1], result[2]);
		} catch(GaussJordanError e) {
			logger.warn(e);
			return null;
		}
		// -----------------------------SolveEquation
	}
	// TODO test
	/*
	 * public static PolynomicalEquation9thDegree
	 * createPolynomicalEquation9thDegree(IPoint[] points) { double[][] valuesA
	 * = new double[points.length][10]; double[][] valuesB = new
	 * double[points.length][10]; double x = 0.0; double y = 0.0; IPoint p;
	 * for(int i = 0; i < points.length; i++) { p = points[i]; Check whether p
	 * is null or not and set the x,y values. if(p == null) { x = 0.0; y = 0.0;
	 * } else { x = p.getX(); y = p.getY(); } f(x) = ax^9 + bx8 + ...
	 * valuesA[i][0] = Math.pow(x, 9); valuesA[i][1] = Math.pow(x, 8);
	 * valuesA[i][2] = Math.pow(x, 7); valuesA[i][3] = Math.pow(x, 6);
	 * valuesA[i][4] = Math.pow(x, 5); valuesA[i][5] = Math.pow(x, 4);
	 * valuesA[i][6] = Math.pow(x, 3); valuesA[i][7] = Math.pow(x, 2);
	 * valuesA[i][8] = x; valuesA[i][9] = 1.0; valuesB[i][0] = y; }
	 * //-----------------------------InitializeValues
	 * //-----------------------------SolveEquation GaussJordan gj = new
	 * GaussJordan(); double[][] a = gj.AtA(valuesA); double[][] b =
	 * gj.AtB(valuesA, valuesB); // TODO siehe Equations_4_Test (wie kann ich
	 * erreichen, dass immer die Regressiongerade positiv gewölbt ist? try {
	 * double[] result = gj.solve(a, b); return new
	 * PolynomicalEquation9thDegree(result[0], result[1], result[2], result[3],
	 * result[4], result[5], result[6], result[7], result[8]); } catch
	 * (GaussJordanError e) { logger.warn(e); return null; }
	 * //-----------------------------SolveEquation }
	 */
	/*
	 * public static PolynomicalEquation4thDegree
	 * createPolynomicalEquation4thDegree(IPoint[] points) { double[][] valuesA
	 * = new double[points.length][5]; double[][] valuesB = new
	 * double[points.length][5]; double x = 0.0; double y = 0.0; IPoint p;
	 * for(int i = 0; i < points.length; i++) { p = points[i]; Check whether p
	 * is null or not and set the x,y values. if(p == null) { x = 0.0; y = 0.0;
	 * } else { x = p.getX(); y = p.getY(); } f(x) = ax^9 + bx8 + ...
	 * valuesA[i][0] = Math.pow(x, 4); valuesA[i][1] = Math.pow(x, 3);
	 * valuesA[i][2] = Math.pow(x, 2); valuesA[i][3] = x; valuesA[i][4] = 1.0;
	 * valuesB[i][0] = y; } //-----------------------------InitializeValues
	 * //-----------------------------SolveEquation GaussJordan gj = new
	 * GaussJordan(); double[][] a = gj.AtA(valuesA); double[][] b =
	 * gj.AtB(valuesA, valuesB); // TODO siehe Equations_4_Test (wie kann ich
	 * erreichen, dass immer die Regressiongerade positiv gewölbt ist? try {
	 * double[] result = gj.solve(a, b); return new
	 * PolynomicalEquation4thDegree(result[0], result[1], result[2], result[3]);
	 * } catch (GaussJordanError e) { logger.warn(e); return null; }
	 * //-----------------------------SolveEquation }
	 */
}
