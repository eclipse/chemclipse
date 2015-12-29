/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.numeric.statistics.Calculations;

// TODO JUnit
/**
 * @author eselmeister
 */
public class MultipleSeries implements IMultipleSeries {

	private List<ISeries> multipleSeries;

	// TODO min max halten, und nur beim löschen und hinzufügen aktualisieren
	public MultipleSeries() {
		multipleSeries = new ArrayList<ISeries>();
	}

	@Override
	public void add(ISeries series) {

		multipleSeries.add(series);
	}

	@Override
	public List<ISeries> getMultipleSeries() {

		return multipleSeries;
	}

	@Override
	public void remove(ISeries series) {

		multipleSeries.remove(series);
	}

	@Override
	public void remove(int index) {

		multipleSeries.remove(index);
	}

	@Override
	public void removeAll() {

		multipleSeries.clear();
	}

	@Override
	public double getXMax() {

		if(multipleSeries.size() == 0) {
			return 0.0d;
		} else {
			double[] xMaxValues = new double[multipleSeries.size()];
			int i = 0;
			for(ISeries series : multipleSeries) {
				xMaxValues[i++] = series.getXMax();
			}
			return Calculations.getMax(xMaxValues);
		}
	}

	@Override
	public double getXMin() {

		if(multipleSeries.size() == 0) {
			return 0.0d;
		} else {
			double[] xMinValues = new double[multipleSeries.size()];
			int i = 0;
			for(ISeries series : multipleSeries) {
				xMinValues[i++] = series.getXMin();
			}
			return Calculations.getMin(xMinValues);
		}
	}

	@Override
	public double getYMax() {

		if(multipleSeries.size() == 0) {
			return 0.0d;
		} else {
			double[] yMaxValues = new double[multipleSeries.size()];
			int i = 0;
			for(ISeries series : multipleSeries) {
				yMaxValues[i++] = series.getYMax();
			}
			return Calculations.getMax(yMaxValues);
		}
	}

	@Override
	public double getYMin() {

		if(multipleSeries.size() == 0) {
			return 0.0d;
		} else {
			double[] yMinValues = new double[multipleSeries.size()];
			int i = 0;
			for(ISeries series : multipleSeries) {
				yMinValues[i++] = series.getYMin();
			}
			return Calculations.getMin(yMinValues);
		}
	}
}
