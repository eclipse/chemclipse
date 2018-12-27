/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.equations.QuadraticEquation;

public abstract class AbstractConcentrationResponseEntriesMSD implements IConcentrationResponseEntriesMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 163932807502826445L;
	//
	private List<IConcentrationResponseEntry> concentrationResponseEntries;

	public AbstractConcentrationResponseEntriesMSD() {
		concentrationResponseEntries = new ArrayList<IConcentrationResponseEntry>();
	}

	@Override
	public void add(IConcentrationResponseEntry concentrationResponseEntry) {

		concentrationResponseEntries.add(concentrationResponseEntry);
	}

	@Override
	public void addAll(List<IConcentrationResponseEntry> concentrationResponseEntries) {

		this.concentrationResponseEntries.addAll(concentrationResponseEntries);
	}

	@Override
	public void remove(IConcentrationResponseEntry concentrationResponseEntry) {

		concentrationResponseEntries.remove(concentrationResponseEntry);
	}

	@Override
	public void removeAll(List<IConcentrationResponseEntry> concentrationResponseEntriesMSD) {

		concentrationResponseEntries.removeAll(concentrationResponseEntriesMSD);
	}

	@Override
	public void clear() {

		concentrationResponseEntries.clear();
	}

	@Override
	public int size() {

		return concentrationResponseEntries.size();
	}

	@Override
	public IConcentrationResponseEntry get(int index) {

		return concentrationResponseEntries.get(index);
	}

	@Override
	public List<IConcentrationResponseEntry> getList() {

		return concentrationResponseEntries;
	}

	@Override
	public LinearEquation getLinearEquation(double ion, boolean isCrossZero) {

		/*
		 * Create the equation.
		 */
		List<IPoint> points = getPoints(ion, isCrossZero);
		IPoint[] pointArray = points.toArray(new IPoint[points.size()]);
		LinearEquation linearEquation = Equations.createLinearEquation(pointArray);
		return linearEquation;
	}

	@Override
	public QuadraticEquation getQuadraticEquation(double ion, boolean isCrossZero) {

		/*
		 * Create the equation.
		 */
		List<IPoint> points = getPoints(ion, isCrossZero);
		IPoint[] pointArray = points.toArray(new IPoint[points.size()]);
		QuadraticEquation quadraticEquation = Equations.createQuadraticEquation(pointArray);
		return quadraticEquation;
	}

	@Override
	public double getAverageFactor(double ion, boolean isCrossZero) {

		double x = 0.0d;
		double y = 0.0d;
		List<IPoint> points = getPoints(ion, isCrossZero);
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

	private List<IPoint> getPoints(double ion, boolean isCrossZero) {

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
		for(IConcentrationResponseEntry entry : concentrationResponseEntries) {
			/*
			 * Check the ion.
			 */
			if(ion == entry.getSignal()) {
				points.add(new Point(entry.getConcentration(), entry.getResponse()));
			}
		}
		return points;
	}

	@Override
	public double getMaxResponseValue() {

		double maxResponse = 0;
		for(IConcentrationResponseEntry entry : concentrationResponseEntries) {
			double response = entry.getResponse();
			if(response > maxResponse) {
				maxResponse = response;
			}
		}
		return maxResponse;
	}

	@Override
	public Set<Double> getIonSet() {

		Set<Double> ionSet = new HashSet<Double>();
		for(IConcentrationResponseEntry entry : concentrationResponseEntries) {
			ionSet.add(entry.getSignal());
		}
		return ionSet;
	}

	@Override
	public List<IConcentrationResponseEntry> getList(double ion) {

		List<IConcentrationResponseEntry> entries = new ArrayList<IConcentrationResponseEntry>();
		for(IConcentrationResponseEntry entry : concentrationResponseEntries) {
			if(entry.getSignal() == ion) {
				entries.add(entry);
			}
		}
		return entries;
	}
}
