/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.IEquation;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class CalibrationChartSupport {

	private static final Logger logger = Logger.getLogger(CalibrationChartSupport.class);

	public ILineSeriesData getLineSeriesData(List<IResponseSignal> signalEntries, String label, Color color) {

		int size = signalEntries.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IResponseSignal concentrationResponseEntry = signalEntries.get(i);
			xSeries[i] = concentrationResponseEntry.getConcentration();
			ySeries[i] = concentrationResponseEntry.getResponse();
		}
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
		lineSeriesSettings.setSymbolSize(5);
		//
		return lineSeriesData;
	}

	public ILineSeriesData getLineSeriesData(List<IResponseSignal> signalEntries, IEquation equation, CalibrationMethod calibrationMethod, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

		ILineSeriesData lineSeriesData = null;
		switch(calibrationMethod) {
			case LINEAR:
				/*
				 * Equations may not have a negative slope.
				 * In this case, use Cross Zero = true.
				 */
				label += " eq";
				if(((LinearEquation)equation).getA() < 0) {
					lineSeriesData = getErrorEquationSeries(label, color);
				} else {
					lineSeriesData = calculateLinearEquationSeries(equation, useCrossZero, pointMin, pointMax, label, color);
				}
				break;
			case QUADRATIC:
				/*
				 * Quadratic
				 */
				label += " eq^2";
				try {
					lineSeriesData = calculateQuadraticEquationSeries(equation, useCrossZero, pointMin, pointMax, label, color);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label, color);
				}
				break;
			case AVERAGE:
				/*
				 * Average
				 */
				label += " avg";
				try {
					lineSeriesData = calculateAverageSeries(signalEntries, label, color);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label, color);
				}
				break;
			case ISTD:
				/*
				 * Not used here.
				 */
				logger.warn("ISTD shouldn't be used here.");
				break;
		}
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateLinearEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

		/*
		 * Take care the intersections of the equations with
		 * the border are inside the plotting area.
		 */
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		/*
		 * MIN
		 */
		if(useCrossZero) {
			xSeries[0] = 0;
			ySeries[0] = 0;
		} else {
			xSeries[0] = pointMin.getX();
			ySeries[0] = pointMin.getY();
		}
		/*
		 * MAX
		 */
		xSeries[1] = pointMax.getX();
		ySeries[1] = equation.calculateY(pointMax.getX());
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateQuadraticEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

		int steps = 50; // Calculate the y values for 50 steps
		double xStart = (useCrossZero) ? 0 : pointMin.getX();
		double xStep = (pointMax.getX() - xStart) / steps;
		/*
		 * Why do we use a List here?
		 * The calculation of the quadratic line has several constraints.
		 * Values lower than 0 shall not be used. Furthermore, the
		 * array index must be not exceeded, but we don't know how many
		 * values are valid previously. Hence, it's a bit more resource
		 * consuming to use a List, but it's more safe.
		 */
		List<IPoint> points = new ArrayList<IPoint>();
		for(double x = xStart; x < pointMax.getX(); x += xStep) {
			/*
			 * Calculate the quadratic line series.
			 */
			double y = equation.calculateY(x);
			if(y > 0) {
				points.add(new Point(x, y));
			}
		}
		//
		double[] xSeries = new double[points.size()];
		double[] ySeries = new double[points.size()];
		for(int index = 0; index < points.size(); index++) {
			IPoint point = points.get(index);
			xSeries[index] = point.getX();
			ySeries[index] = point.getY();
		}
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateAverageSeries(List<IResponseSignal> entries, String label, Color color) {

		double x = 0.0d;
		double y = 0.0d;
		int size = entries.size();
		if(size == 0) {
			return null;
		}
		/*
		 * Calculate the center.
		 */
		for(IResponseSignal entry : entries) {
			x += entry.getConcentration();
			y += entry.getResponse();
		}
		//
		x /= size;
		y /= size;
		//
		double[] xSeries = new double[]{x};
		double[] ySeries = new double[]{y};
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setSymbolSize(6);
		lineSeriesSettings.setSymbolType(PlotSymbolType.CROSS);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getErrorEquationSeries(String label, Color color) {

		double[] xSeries = {0};
		double[] ySeries = {0};
		//
		return getLineSeriesData(xSeries, ySeries, label, color);
	}

	private ILineSeriesData getLineSeriesData(double[] xSeries, double[] ySeries, String label, Color color) {

		ISeriesData seriesData = new SeriesData(xSeries, ySeries, label);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setLineWidth(1);
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setSymbolType(PlotSymbolType.NONE);
		lineSeriesSettings.setSymbolColor(color);
		lineSeriesSettings.setSymbolSize(1);
		//
		return lineSeriesData;
	}
}