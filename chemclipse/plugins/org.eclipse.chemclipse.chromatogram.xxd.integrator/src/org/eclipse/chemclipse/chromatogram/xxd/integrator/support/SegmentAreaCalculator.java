/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.support;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;

/**
 * Calculates the area of a given integration segment.<br/>
 * A segment can be described as a trapezium.<br/>
 * The area of a segment could be also negative. In general, 4 points are needed
 * to determine the area.<br/>
 * The start and end point of the peak baseline and the start and end point of
 * the background baseline.<br/>
 * There is also a special case, where the peak baseline crosses the background
 * baseline. In this case the areas of two triangles with different area signs
 * will be integrated.
 * 
 * @author eselmeister
 */
public class SegmentAreaCalculator {

	/**
	 * This class should have only static methods.
	 */
	private SegmentAreaCalculator() {
	}

	public static double calculateSegmentArea(ISegment segment) {

		double area = 0.0d;
		double a;
		double c;
		double h;
		IPoint pbp1 = segment.getPeakBaselinePoint1();
		IPoint pbp2 = segment.getPeakBaselinePoint2();
		IPoint cbp1 = segment.getChromatogramBaselinePoint1();
		IPoint cbp2 = segment.getChromatogramBaselinePoint2();
		/*
		 * Return if a point is null.
		 */
		if(cbp1 == null || cbp2 == null || pbp1 == null || pbp2 == null) {
			return area;
		}
		/*
		 * Check whether the baselines are crossing or not.<br/> If they are
		 * crossing, handle them separately.
		 */
		if(crossingBaselinesInGivenRange(segment)) {
			/*
			 * The area consists of two triangles.
			 */
			try {
				IPoint intersection = calculateBaselineIntersection(segment);
				/*
				 * The length of c is 0, as it they are two triangles, a
				 * trapezium special case.
				 */
				c = 0.0f;
				// Left triangle.
				h = intersection.getX() - pbp1.getX();
				a = pbp1.getY() - cbp1.getY();
				area += calculateTrapeziumArea(a, c, h);
				// Right triangle.
				h = pbp2.getX() - intersection.getX();
				a = pbp2.getY() - cbp2.getY();
				area += calculateTrapeziumArea(a, c, h);
			} catch(SolverException e) {
				area = 0.0f;
			}
		} else {
			/*
			 * Calculate the normal trapezium area.
			 */
			a = pbp1.getY() - cbp1.getY();
			c = pbp2.getY() - cbp2.getY();
			h = pbp2.getX() - pbp1.getX();
			area = calculateTrapeziumArea(a, c, h);
		}
		return area;
	}

	// ------------------------------------------private methods
	/*
	 * Calculates the area of a trapezium.<br/> <br/> A = ((a + c) / 2) * h<br/>
	 * <br/> h = x2 - x1<br/> a = pb1 - cb1<br/> c = pb2 - cb2<br/>
	 */
	private static double calculateTrapeziumArea(double a, double c, double h) {

		return ((a + c) / 2) * h;
	}

	/**
	 * If the baselines are crossing (in the given x window range Point.x to
	 * Point2.x ...), the method will return true. In this case the segment
	 * consists of two triangulars. If false, it is a trapezium.
	 * 
	 * @param segment
	 * @return boolean
	 */
	private static boolean crossingBaselinesInGivenRange(ISegment segment) {

		if(crossingBaselinesPositive(segment) || crossingBaselinesNegative(segment)) {
			return true;
		}
		return false;
	}

	/**
	 * If this value is true, the first triangle is negative and the second
	 * triangle is positive.
	 * 
	 * @param segment
	 * @return boolean
	 */
	private static boolean crossingBaselinesPositive(ISegment segment) {

		IPoint cbp1 = segment.getChromatogramBaselinePoint1();
		IPoint cbp2 = segment.getChromatogramBaselinePoint2();
		IPoint pbp1 = segment.getPeakBaselinePoint1();
		IPoint pbp2 = segment.getPeakBaselinePoint2();
		/*
		 * Return if a point is null.
		 */
		if(cbp1 == null || cbp2 == null || pbp1 == null || pbp2 == null) {
			return false;
		}
		if(cbp1.getY() > pbp1.getY() && cbp2.getY() < pbp2.getY()) {
			return true;
		}
		return false;
	}

	/**
	 * If this value is true, the first triangle is positive and the second
	 * triangle is negative.
	 * 
	 * @param segment
	 * @return boolean
	 */
	private static boolean crossingBaselinesNegative(ISegment segment) {

		IPoint cbp1 = segment.getChromatogramBaselinePoint1();
		IPoint cbp2 = segment.getChromatogramBaselinePoint2();
		IPoint pbp1 = segment.getPeakBaselinePoint1();
		IPoint pbp2 = segment.getPeakBaselinePoint2();
		/*
		 * Return if a point is null.
		 */
		if(cbp1 == null || cbp2 == null || pbp1 == null || pbp2 == null) {
			return false;
		}
		if(cbp1.getY() < pbp1.getY() && cbp2.getY() > pbp2.getY()) {
			return true;
		}
		return false;
	}

	/**
	 * Calculates the intersection of both baselines.<br/>
	 * If they are not intersecting (means parallel) null will be returned.<br/>
	 * Check first if the baselines are cross each other in the given x window
	 * range.
	 * 
	 * @throws SolverException
	 * @param segment
	 * @return IPoint
	 */
	private static IPoint calculateBaselineIntersection(ISegment segment) throws SolverException {

		LinearEquation peakBaseline = Equations.createLinearEquation(segment.getPeakBaselinePoint1(), segment.getPeakBaselinePoint2());
		LinearEquation chromatogramBaseline = Equations.createLinearEquation(segment.getChromatogramBaselinePoint1(), segment.getChromatogramBaselinePoint2());
		return Equations.calculateIntersection(peakBaseline, chromatogramBaseline);
	}
	// ------------------------------------------private methods
}
