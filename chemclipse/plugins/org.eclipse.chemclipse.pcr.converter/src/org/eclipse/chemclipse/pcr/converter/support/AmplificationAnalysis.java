/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Apex;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.IQuadraticEquation;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;

public class AmplificationAnalysis {

	public static void calculateCrossingPoints(IPlate plate) {

		for(IWell well : plate.getWells()) {
			for(IChannel channel : well.getChannels().values()) {
				/*
				 * Calculate the crossing point.
				 */
				List<IPoint> rawData = getPoints(channel.getFluorescence());
				List<IPoint> firstDerivative = calculateDerivative(rawData);
				List<IPoint> firstDerivativeSmoothed = calculateMovingAverage(firstDerivative);
				List<IPoint> secondDerivative = calculateDerivative(firstDerivativeSmoothed);
				/*
				 * Peak Start
				 */
				IPoint peakStart = getPeakStart(firstDerivativeSmoothed);
				if(peakStart != null) {
					IPoint inflectionPoint = getInflectionPoint(peakStart, secondDerivative);
					if(inflectionPoint != null) {
						double crossingPointX = getCrossingPointX(inflectionPoint, secondDerivative);
						channel.setCrossingPoint(crossingPointX);
					}
				}
			}
		}
	}

	private static double getCrossingPointX(IPoint inflectionPoint, List<IPoint> points) {

		double crossingPointX = 0.0d;
		for(int i = 1; i < points.size() - 1; i++) {
			IPoint px = points.get(i);
			if(px.getX() == inflectionPoint.getX()) {
				IPoint[] pts = new Point[3];
				pts[0] = points.get(i - 1);
				pts[1] = px;
				pts[2] = points.get(i + 1);
				IQuadraticEquation quadraticEquation = Equations.createQuadraticEquation(pts);
				crossingPointX = quadraticEquation.getApexValueForX(Apex.POSITIVE);
			}
		}
		return crossingPointX;
	}

	private static IPoint getInflectionPoint(IPoint peakStart, List<IPoint> points) {

		IPoint p0 = null;
		for(int i = 0; i < points.size(); i++) {
			IPoint px = points.get(i);
			if(p0 == null) {
				if(px.getX() == peakStart.getX()) {
					p0 = px;
				}
			} else {
				if(px.getY() > p0.getY()) {
					p0 = px;
				}
			}
		}
		//
		return p0;
	}

	private static IPoint getPeakStart(List<IPoint> points) {

		double threshold = getMedian(points) * 5;
		//
		IPoint peakStart = null;
		IPoint p0 = null;
		int counter = 0;
		exitloop:
		for(int i = 0; i < points.size(); i++) {
			IPoint px = points.get(i);
			if(p0 != null) {
				if(px.getY() <= p0.getY()) {
					counter = 0;
				} else {
					if(px.getY() > threshold) {
						counter++;
					}
				}
				//
				if(counter >= 3) {
					peakStart = points.get(i - 2);
					break exitloop;
				}
			}
			p0 = px;
		}
		//
		return peakStart;
	}

	private static double getMedian(List<IPoint> points) {

		double[] values = new double[points.size()];
		for(int i = 0; i < points.size(); i++) {
			values[i] = points.get(i).getY();
		}
		return Calculations.getMedian(values);
	}

	private static List<IPoint> getPoints(List<Double> signals) {

		List<IPoint> points = new ArrayList<>();
		for(int i = 0; i < signals.size(); i++) {
			points.add(new Point(i, signals.get(i)));
		}
		return points;
	}

	private static List<IPoint> calculateDerivative(List<IPoint> points) {

		List<IPoint> values = new ArrayList<>();
		for(int i = 0; i < (points.size() - 1); i++) {
			IPoint p1 = points.get(i);
			IPoint p2 = points.get(i + 1);
			double y = p2.getY() - p1.getY();
			y = (y < 0) ? 0 : y;
			values.add(new Point(p1.getX(), y));
		}
		return values;
	}

	private static List<IPoint> calculateMovingAverage(List<IPoint> points) {

		List<IPoint> values = new ArrayList<>();
		for(int i = 1; i < (points.size() - 1); i++) {
			IPoint p0 = points.get(i - 1);
			IPoint px = points.get(i);
			IPoint p1 = points.get(i + 1);
			double y = (p0.getY() + px.getY() + p1.getY()) / 3;
			values.add(new Point(px.getX(), y));
		}
		return values;
	}
}
