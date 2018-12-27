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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.IEquation;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.swt.ui.base.InteractiveChartExtended;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.Range;

public class ConcentrationResponseEntriesLineSeriesUI extends InteractiveChartExtended implements ISeriesSetter, IQuantitationCompoundUpdater, KeyListener, MouseListener {

	private static final Logger logger = Logger.getLogger(ConcentrationResponseEntriesLineSeriesUI.class);
	//
	private IMultipleSeries multipleLineSeries;
	private IAxis yAxisLeft; // e.g.: abundance
	private IAxis xAxisBottom; // e.g.: concentration
	private IAxis yAxisRight; // e.g.: relative abundance
	private IAxis xAxisTop;
	//
	private double maxSignal = 0.0d;
	//
	private IQuantitationCompoundMSD quantitationCompoundMSD;

	public ConcentrationResponseEntriesLineSeriesUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@Override
	public void update(IQuantitationCompoundMSD quantitationCompoundMSD, IQuantDatabase database) {

		if(quantitationCompoundMSD == null || database == null) {
			setConcentrationLabel("");
			deleteAllCurrentSeries();
		} else {
			this.quantitationCompoundMSD = quantitationCompoundMSD;
			setViewSeries();
		}
	}

	@Override
	public void setViewSeries() {

		if(quantitationCompoundMSD != null) {
			/*
			 * Delete the current and set the new series.
			 */
			deleteAllCurrentSeries();
			setConcentrationLabel(quantitationCompoundMSD.getConcentrationUnit());
			//
			IConcentrationResponseEntries concentrationResponseEntriesMSD = quantitationCompoundMSD.getConcentrationResponseEntriesMSD();
			maxSignal = concentrationResponseEntriesMSD.getMaxResponseValue();
			CalibrationMethod calibrationMethod = quantitationCompoundMSD.getCalibrationMethod();
			boolean useCrossZero = quantitationCompoundMSD.isCrossZero();
			//
			IMultipleSeries multipleSeries;
			ILineSeries lineSeries;
			String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
			IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
			/*
			 * Points of concentration and response entries. -------------------------------------------------------------
			 */
			multipleSeries = getSeries(concentrationResponseEntriesMSD);
			for(ISeries series : multipleSeries.getMultipleSeries()) {
				/*
				 * Series for each ion.
				 */
				addSeries(series);
				lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				lineSeries.setXSeries(series.getXSeries());
				lineSeries.setYSeries(series.getYSeries());
				lineSeries.enableArea(false);
				lineSeries.setSymbolType(PlotSymbolType.CIRCLE);
				lineSeries.setSymbolColor(colorScheme.getColor());
				lineSeries.setLineStyle(LineStyle.NONE);
				colorScheme.incrementColor();
			}
			/*
			 * Equation(s) ----------------------------------------------------------------------------------------------
			 */
			colorScheme.reset();
			/*
			 * Get an 20% offset to display the last concentration
			 * response entry not in the edge.
			 */
			double xExtra = multipleSeries.getXMax() / 20.0d;
			double yExtra = multipleSeries.getYMax() / 20.0d;
			double xMin = 0;
			double yMin = 0;
			double xMax = multipleSeries.getXMax() + xExtra;
			double yMax = multipleSeries.getYMax() + yExtra;
			//
			Point pointMin = new Point(xMin, yMin);
			Point pointMax = new Point(xMax, yMax);
			//
			multipleSeries = getSeriesByEquation(concentrationResponseEntriesMSD, calibrationMethod, useCrossZero, pointMin, pointMax);
			for(ISeries series : multipleSeries.getMultipleSeries()) {
				/*
				 * Equation series for each ion.
				 */
				addSeries(series);
				lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				lineSeries.setXSeries(series.getXSeries());
				lineSeries.setYSeries(series.getYSeries());
				lineSeries.enableArea(false);
				if(calibrationMethod.equals(CalibrationMethod.AVERAGE)) {
					/*
					 * AVERAGE
					 */
					lineSeries.setSymbolType(PlotSymbolType.CROSS);
					lineSeries.setSymbolColor(colorScheme.getColor());
					lineSeries.setLineStyle(LineStyle.NONE);
				} else if(calibrationMethod.equals(CalibrationMethod.QUADRATIC)) {
					/*
					 * QUADRATIC
					 */
					lineSeries.setSymbolType(PlotSymbolType.NONE);
					lineSeries.setLineStyle(LineStyle.SOLID);
					lineSeries.setLineColor(colorScheme.getColor());
				} else {
					/*
					 * LINEAR
					 */
					lineSeries.setSymbolType(PlotSymbolType.NONE);
					lineSeries.setLineStyle(LineStyle.SOLID);
					lineSeries.setLineColor(colorScheme.getColor());
				}
				//
				colorScheme.incrementColor();
			}
			//
			getAxisSet().adjustRange();
			redraw();
		}
	}

	/**
	 * Get the series for the concentration response entry points.
	 * 
	 * @param concentrationResponseEntriesMSD
	 * @return IMultipleSeries
	 */
	private IMultipleSeries getSeries(IConcentrationResponseEntries concentrationResponseEntriesMSD) {

		IMultipleSeries concentrationResponseSeries = new MultipleSeries();
		Set<Double> ions = concentrationResponseEntriesMSD.getSignalSet();
		/*
		 * Parse all ions.
		 */
		for(double ion : ions) {
			/*
			 * Get the corresponding response entries.
			 */
			List<IConcentrationResponseEntry> list = concentrationResponseEntriesMSD.getList(ion);
			double[] xSeries = new double[list.size()];
			double[] ySeries = new double[list.size()];
			int x = 0;
			int y = 0;
			//
			for(IConcentrationResponseEntry concentrationResponseEntry : list) {
				/*
				 * Retrieve the x and y signals.
				 */
				xSeries[x++] = concentrationResponseEntry.getConcentration();
				ySeries[y++] = concentrationResponseEntry.getResponse();
			}
			//
			String label = (ion == AbstractIon.TIC_ION) ? "TIC" : "m/z " + Double.valueOf(ion).toString();
			concentrationResponseSeries.add(new Series(xSeries, ySeries, label));
		}
		//
		return concentrationResponseSeries;
	}

	/**
	 * Creates the equation series for all ions of the concentration response entries.
	 * 
	 * @param concentrationResponseEntriesMSD
	 * @param calibrationMethod
	 * @param useCrossZero
	 * @param xMax
	 * @param yMax
	 * @return IMultipleSeries
	 */
	private IMultipleSeries getSeriesByEquation(IConcentrationResponseEntries concentrationResponseEntriesMSD, CalibrationMethod calibrationMethod, boolean useCrossZero, Point pointMin, Point pointMax) {

		IMultipleSeries equationSeries = new MultipleSeries();
		Set<Double> ions = concentrationResponseEntriesMSD.getSignalSet();
		/*
		 * Parse all ions.
		 */
		for(double ion : ions) {
			/*
			 * Get the label and the equation.
			 */
			String label = (ion == AbstractIon.TIC_ION) ? "TIC" : "m/z " + Double.valueOf(ion).toString();
			Series series = null;
			IEquation equation;
			//
			switch(calibrationMethod) {
				case LINEAR:
					//
					label += " eq";
					try {
						equation = concentrationResponseEntriesMSD.getLinearEquation(ion, useCrossZero);
						/*
						 * Equations may not have a negative slope.
						 * In this case, use Cross Zero = true.
						 */
						if(((LinearEquation)equation).getA() < 0) {
							throw new Exception();
						}
						series = calculateLinearEquationSeries(equation, useCrossZero, pointMin, pointMax, label);
					} catch(Exception e) {
						series = getErrorEquationSeries(label);
					}
					break;
				case QUADRATIC:
					//
					label += " eq^2";
					try {
						equation = concentrationResponseEntriesMSD.getQuadraticEquation(ion, useCrossZero);
						series = calculateQuadraticEquationSeries(equation, useCrossZero, pointMin, pointMax, label);
					} catch(Exception e) {
						series = getErrorEquationSeries(label);
					}
					break;
				case AVERAGE:
					//
					label += " avg";
					try {
						List<IConcentrationResponseEntry> entries = concentrationResponseEntriesMSD.getList(ion);
						series = calculateAverageSeries(entries, label);
					} catch(Exception e) {
						series = getErrorEquationSeries(label);
					}
					break;
				case ISTD:
					logger.warn("ISTD shouldn't be used here.");
					break;
			}
			//
			if(series != null) {
				equationSeries.add(series);
			}
		}
		//
		return equationSeries;
	}

	private Series getErrorEquationSeries(String label) {

		double[] xSeries = {0};
		double[] ySeries = {0};
		return new Series(xSeries, ySeries, label + " (Error)");
	}

	/**
	 * Calculates the values for a linear equation.
	 * 
	 * @param equation
	 * @param xMax
	 * @param yMax
	 * @param label
	 * @return Series
	 */
	private Series calculateLinearEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label) {

		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		double x;
		double y;
		double xMax = pointMax.getX();
		double yMax = pointMax.getY();
		/*
		 * Take care the intersections of the equations with
		 * the border are inside the plotting area.
		 */
		x = equation.calculateX(yMax);
		if(x > xMax) {
			y = equation.calculateY(xMax);
			x = xMax;
		} else {
			y = equation.calculateY(x);
		}
		/*
		 * Set the x and y signals.
		 */
		if(useCrossZero) {
			xSeries[0] = 0;
			ySeries[0] = 0;
		} else {
			xSeries[0] = pointMin.getX();
			ySeries[0] = pointMin.getY();
		}
		xSeries[1] = x;
		ySeries[1] = y;
		//
		return new Series(xSeries, ySeries, label);
	}

	/*
	 * Returns the quadratic equation series.
	 */
	private Series calculateQuadraticEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label) {

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
		return new Series(xSeries, ySeries, label);
	}

	/*
	 * Returns the center position.
	 */
	private Series calculateAverageSeries(List<IConcentrationResponseEntry> entries, String label) {

		double x = 0.0d;
		double y = 0.0d;
		int size = entries.size();
		if(size == 0) {
			return null;
		}
		/*
		 * Calculate the center.
		 */
		for(IConcentrationResponseEntry entry : entries) {
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
		return new Series(xSeries, ySeries, label);
	}

	/**
	 * Initializes the chart with axes, series, colors.
	 */
	private void initialize() {

		/*
		 * Add key and mouse listeners.
		 */
		getPlotArea().addKeyListener(this);
		getPlotArea().addMouseListener(this);
		multipleLineSeries = new MultipleSeries();
		// No title, no legend.
		getLegend().setVisible(false);
		getTitle().setVisible(false);
		// e.g.: concentration, abundance
		createPrimaryAxes();
		createSecondaryAxes();
		// red, white and black
		setAxesAndBackgroundColors();
		getAxisSet().adjustRange();
	}

	/**
	 * Creates the primary axes.
	 */
	private void createPrimaryAxes() {

		/*
		 * Main Axes.
		 */
		IAxisSet axisSet = getAxisSet();
		yAxisLeft = axisSet.getYAxis(0);
		yAxisLeft.getTitle().setText("Response");
		yAxisLeft.setPosition(Position.Primary);
		//
		xAxisBottom = axisSet.getXAxis(0);
		xAxisBottom.getTitle().setText("Concentration");
		xAxisBottom.setPosition(Position.Primary);
	}

	/**
	 * Creates the secondary axes.
	 */
	private void createSecondaryAxes() {

		IAxisSet axisSet = getAxisSet();
		/*
		 * e.g. top axis
		 */
		int axisIdTop = axisSet.createXAxis();
		xAxisTop = axisSet.getXAxis(axisIdTop);
		xAxisTop.getTitle().setText("Concentration");
		xAxisTop.setPosition(Position.Secondary);
		/*
		 * e.g. Relative abundance axis
		 */
		int axisIdAbundanceRelative = axisSet.createYAxis();
		yAxisRight = axisSet.getYAxis(axisIdAbundanceRelative);
		yAxisRight.getTitle().setText("Relative Response");
		yAxisRight.setPosition(Position.Secondary);
	}

	private void setConcentrationLabel(String concentrationUnit) {

		xAxisTop.getTitle().setText("Concentration (" + concentrationUnit + ")");
		xAxisBottom.getTitle().setText("Concentration (" + concentrationUnit + ")");
	}

	private void setAxesAndBackgroundColors() {

		assert (yAxisLeft != null) : "The abundance instance must be not null.";
		assert (xAxisBottom != null) : "The concentration instance must be not null.";
		assert (yAxisRight != null) : "The relativeAbundance instance must be not null.";
		assert (xAxisTop != null) : "The top instance must be not null.";
		/*
		 * Color background, axes and line series
		 */
		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		ChartUtil.setAxisColor(yAxisLeft, Colors.BLACK);
		ChartUtil.setAxisColor(xAxisBottom, Colors.BLACK);
		ChartUtil.setAxisColor(yAxisRight, Colors.BLACK);
		ChartUtil.setAxisColor(xAxisTop, Colors.BLACK);
	}

	/**
	 * Deletes all series.
	 */
	protected void deleteAllCurrentSeries() {

		multipleLineSeries.removeAll();
		org.eclipse.swtchart.ISeries[] series = getSeriesSet().getSeries();
		List<String> ids = new ArrayList<String>();
		/*
		 * Get the ids.
		 */
		for(org.eclipse.swtchart.ISeries serie : series) {
			ids.add(serie.getId());
		}
		/*
		 * Remove all ids.
		 */
		for(String id : ids) {
			getSeriesSet().deleteSeries(id);
		}
	}

	public void addSeries(ISeries series) {

		if(multipleLineSeries != null) {
			multipleLineSeries.add(series);
		}
	}

	@Override
	public synchronized void redraw() {

		super.redraw();
		double xMin, xMax, yMin, yMax;
		xMin = multipleLineSeries.getXMin();
		xMax = multipleLineSeries.getXMax();
		yMin = multipleLineSeries.getYMin();
		yMax = multipleLineSeries.getYMax();
		/*
		 * Check and corrects the ranges and assures that xMin and xMax are not
		 * both 0.
		 */
		if(xMin < xMax && yMin < yMax) {
			ChartUtil.checkAndSetRange(xAxisBottom, xMin, xMax);
			ChartUtil.checkAndSetRange(yAxisLeft, yMin, yMax);
			redrawXAxisBottomScale();
			redrawYAxisRightScale();
		}
	}

	/**
	 * Redraws the x axis bottom scale.
	 */
	public void redrawXAxisBottomScale() {

		assert (xAxisTop != null) : "The xAxisTop instance must be not null.";
		assert (xAxisBottom != null) : "The xAxisBottom instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set xAxisBottom scale.
		 */
		range = xAxisBottom.getRange();
		min = range.lower;
		max = range.upper;
		ChartUtil.setRange(xAxisTop, min, max);
	}

	/**
	 * Redraws the y axis right scale.
	 */
	public void redrawYAxisRightScale() {

		assert (yAxisLeft != null) : "The yAxisLeft instance must be not null.";
		assert (yAxisRight != null) : "The yAxisRight instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set the yAxisRight scale.
		 */
		range = yAxisLeft.getRange();
		min = ChartUtil.getRelativeAbundance(maxSignal, range.lower);
		max = ChartUtil.getRelativeAbundance(maxSignal, range.upper);
		ChartUtil.setRange(yAxisRight, min, max);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
