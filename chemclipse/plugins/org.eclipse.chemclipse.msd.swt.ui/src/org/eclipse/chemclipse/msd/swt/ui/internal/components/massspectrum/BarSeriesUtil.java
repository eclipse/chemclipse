/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum;

import org.eclipse.chemclipse.msd.swt.ui.exceptions.NoIonAvailableException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtchart.IBarSeries;

public class BarSeriesUtil {

	/**
	 * Use only static methods.
	 */
	private BarSeriesUtil() {
	}

	public static IBarSeriesIons getBarSeriesIonList(IBarSeries barSeries, int widthPlotArea) {

		IBarSeriesIons barSeriesIons = null;
		if(barSeries != null) {
			//
			IBarSeriesIon barSeriesIon;
			barSeriesIons = new BarSeriesIons();
			//
			double[] xSeries = barSeries.getXSeries();
			double[] ySeries = barSeries.getYSeries();
			int size = barSeries.getBounds().length;
			//
			for(int i = 0; i < size; i++) {
				Point point = barSeries.getPixelCoordinates(i);
				if(point.x >= 0 && point.x <= widthPlotArea) {
					barSeriesIon = new BarSeriesIon(xSeries[i], ySeries[i], i);
					barSeriesIons.add(barSeriesIon);
				}
			}
		}
		return barSeriesIons;
	}

	public static double getSelectedIon(int x, IBarSeries barSeries, int widthPlotArea) throws NoIonAvailableException {

		IBarSeriesIon barSeriesIon;
		if(barSeries == null) {
			throw new NoIonAvailableException("BarSeries instance is null.");
		} else {
			/*
			 * Get the allowed deviation.
			 */
			IBarSeriesIons barSeriesIons = getBarSeriesIonList(barSeries, widthPlotArea);
			double deviation = getCalculatedDeviation(barSeries, barSeriesIons);
			/*
			 * Get the selected ion or throw an exception.
			 */
			double xMin = x - deviation;
			double xMax = x + deviation;
			/*
			 * Return a suitable ion.
			 */
			for(int index = 0; index < barSeriesIons.size(); index++) {
				barSeriesIon = barSeriesIons.getBarSeriesIon(index);
				Point point = barSeries.getPixelCoordinates(barSeriesIon.getIndex());
				/*
				 * Check if the actual rectangle is in between the minX and maxX bounds.
				 */
				if(point.x >= xMin && point.x <= xMax) {
					/*
					 * Yes, there is a ion.
					 * But is the abundance higher than 0?
					 */
					if(barSeriesIon.getAbundance() > 0.0d) {
						return barSeriesIon.getIon();
					}
				}
			}
			throw new NoIonAvailableException("There is no suitable ion.");
		}
	}

	private static double getCalculatedDeviation(IBarSeries barSeries, IBarSeriesIons barSeriesIons) {

		double deviation = Double.MAX_VALUE;
		double delta;
		Point pointActual;
		Point pointNext;
		/*
		 * Get the lowest deviation
		 */
		for(int index = 0; index < barSeriesIons.size() - 1; index++) {
			pointActual = getPoint(barSeries, barSeriesIons, index);
			/*
			 * Check if the rectangle has a negative x value. If yes, continue.
			 */
			if(pointActual.x < 0) {
				continue;
			}
			/*
			 * Calculate the delta.
			 */
			pointNext = getPoint(barSeries, barSeriesIons, index + 1);
			delta = (double)pointNext.x - pointActual.x;
			if(delta > 0 && delta < deviation) {
				deviation = delta;
			}
		}
		return deviation / 2;
	}

	private static Point getPoint(IBarSeries barSeries, IBarSeriesIons barSeriesIons, int index) {

		IBarSeriesIon barSeriesIon;
		barSeriesIon = barSeriesIons.getBarSeriesIon(index);
		Point point = barSeries.getPixelCoordinates(barSeriesIon.getIndex());
		return point;
	}
}
