/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.equations.QuadraticEquation;

public class ResponseSignals extends ArrayList<IResponseSignal> implements IResponseSignals {

	private static final long serialVersionUID = 7626025302880914344L;

	@Override
	public LinearEquation getLinearEquation(double signal, boolean isCrossZero) {

		/*
		 * Create the equation.
		 */
		List<IPoint> points = getPoints(signal, isCrossZero);
		IPoint[] pointArray = points.toArray(new IPoint[points.size()]);
		LinearEquation linearEquation = Equations.createLinearEquation(pointArray);
		return linearEquation;
	}

	@Override
	public QuadraticEquation getQuadraticEquation(double signal, boolean isCrossZero) {

		/*
		 * Create the equation.
		 */
		List<IPoint> points = getPoints(signal, isCrossZero);
		IPoint[] pointArray = points.toArray(new IPoint[points.size()]);
		QuadraticEquation quadraticEquation = Equations.createQuadraticEquation(pointArray);
		return quadraticEquation;
	}

	@Override
	public double getAverageFactor(double signal, boolean isCrossZero) {

		double x = 0.0d;
		double y = 0.0d;
		List<IPoint> points = getPoints(signal, isCrossZero);
		int size = points.size();
		if(size == 0) {
			return 0;
		}
		/*
		 * Calculate the center.
		 */
		for(IPoint point : points) {
			x += point.getX(); // Concentration
			y += point.getY(); // Intensity
		}
		//
		x /= size;
		y /= size;
		if(y == 0) {
			return 0;
		}
		//
		return x / y;
	}

	public double getMinResponseValue(double signal) {

		double minResponse = Double.MAX_VALUE;
		for(IResponseSignal entry : this) {
			if(signal == entry.getSignal()) {
				double response = entry.getResponse();
				if(response < minResponse) {
					minResponse = response;
				}
			}
		}
		//
		return (minResponse == Double.MAX_VALUE) ? 0.0d : minResponse;
	}

	public double getMaxResponseValue(double signal) {

		double maxResponse = 0;
		for(IResponseSignal entry : this) {
			if(signal == entry.getSignal()) {
				double response = entry.getResponse();
				if(response > maxResponse) {
					maxResponse = response;
				}
			}
		}
		return maxResponse;
	}

	@Override
	public double getMinResponseValue() {

		double minResponse = Double.MAX_VALUE;
		for(IResponseSignal entry : this) {
			double response = entry.getResponse();
			if(response < minResponse) {
				minResponse = response;
			}
		}
		//
		return (minResponse == Double.MAX_VALUE) ? 0.0d : minResponse;
	}

	@Override
	public double getMaxResponseValue() {

		double maxResponse = 0;
		for(IResponseSignal entry : this) {
			double response = entry.getResponse();
			if(response > maxResponse) {
				maxResponse = response;
			}
		}
		return maxResponse;
	}

	@Override
	public Set<Double> getSignalSet() {

		Set<Double> signalSet = new HashSet<Double>();
		for(IResponseSignal entry : this) {
			signalSet.add(entry.getSignal());
		}
		return signalSet;
	}

	@Override
	public List<IResponseSignal> getList(double signal) {

		List<IResponseSignal> entries = new ArrayList<IResponseSignal>();
		for(IResponseSignal entry : this) {
			if(entry.getSignal() == signal) {
				entries.add(entry);
			}
		}
		return entries;
	}

	private List<IPoint> getPoints(double signal, boolean isCrossZero) {

		/*
		 * Points are:
		 * x -> Concentration
		 * y -> Response
		 */
		List<IPoint> points = new ArrayList<IPoint>();
		if(isCrossZero) {
			points.add(new Point(0.0d, 0.0d));
		}
		/*
		 * Parse all entries and extract those who are needed.
		 */
		for(IResponseSignal entry : this) {
			/*
			 * Check the signal.
			 */
			if(signal == entry.getSignal()) {
				points.add(new Point(entry.getConcentration(), entry.getResponse()));
			}
		}
		return points;
	}
}
