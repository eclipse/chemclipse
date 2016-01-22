/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.thirdpartylibraries.swtchart.ext.InteractiveChartExtended;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.IAxisSet;
import org.swtchart.Range;

public abstract class AbstractBarSeriesUI extends InteractiveChartExtended implements ISeriesSetter, KeyListener, MouseListener {

	protected boolean master = false;
	/*
	 * Subclasses may override setViewSeries().
	 */
	protected IChromatogramSelectionMSD chromatogramSelection;
	protected IScanMSD massSpectrum;
	protected IMultipleSeries multipleLineSeries;
	/*
	 * To draw the relative abundance correctly, the maximum total signal of the
	 * chromatogram is needed. It's not sufficient to get only the max signal of
	 * the current series, cause the currents series could be only a small
	 * selection of a chromatogram. If an update is requested,
	 * chromatogramSelection.getChromatogram().getMaxSignal() will be called to
	 * get the value.
	 */
	private double maxSignal = 0.0d;
	/*
	 * The Axes
	 */
	private IAxis ions;
	private IAxis dalton;
	private IAxis abundance;
	private IAxis relativeAbundance;
	/*
	 * Space on top of abundance.
	 */
	private static final double SPACE_FACTOR = 0.15d;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param style
	 */
	public AbstractBarSeriesUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void setLayoutData(GridData gridData) {

		super.setLayoutData(gridData);
	}

	/**
	 * Clears the current bar series.
	 */
	public void clear() {

		deleteAllCurrentSeries();
		maxSignal = 1.0d;
		massSpectrum = null;
		List<IAxis> axisList = new ArrayList<IAxis>();
		axisList.add(ions);
		axisList.add(dalton);
		axisList.add(abundance);
		axisList.add(relativeAbundance);
		for(IAxis axis : axisList) {
			Range range = axis.getRange();
			range.lower = 0;
			range.upper = 1;
			axis.setRange(range);
		}
		getAxisSet().adjustRange();
		super.redraw();
	}

	/**
	 * Returns whether this ui is a master ui or not.<br/>
	 * See setMaster(boolean master)
	 * 
	 * @return boolean
	 */
	public boolean isMaster() {

		return master;
	}

	/**
	 * If true, than this ui will be set to master state.<br/>
	 * In the master state, the whole series will be kept loaded until the
	 * update method from {@link IChromatogramSelectionMSDUpdateNotifier} calls forceReload ==
	 * true.<br/>
	 * When should the master method be used? In example if the chromatogram
	 * should be presented in an editor.<br/>
	 * All changes from the editor will be propagated to the views, which are
	 * not in master mode. So, the views change their chromatographic data,
	 * depending on the selection they get.<br/>
	 * The master still keeps the selection.
	 * 
	 * @param master
	 */
	public void setMaster(boolean master) {

		this.master = master;
	}

	// ------------------------------------------------------Update MassSpectrum
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		this.massSpectrum = massSpectrum;
		/*
		 * If the current view is not a master, reload the data on each
		 * update.<br/> If the ui is in master mode and force reload is set,
		 * load the series.
		 */
		if(!master || (master && forceReload)) {
			setSeries(forceReload);
		}
	}

	// ------------------------------------------------------Update MassSpectrum
	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public void redraw() {

		super.redraw();
		double xMin, xMax;
		xMin = multipleLineSeries.getXMin();
		xMax = multipleLineSeries.getXMax();
		/*
		 * Check and corrects the ranges and assures that xMin and xMax are not
		 * both 0.
		 */
		if(xMin < xMax) {
			ChartUtil.checkAndSetRange(ions, xMin, xMax);
			double yMin = multipleLineSeries.getYMin();
			double yMax = multipleLineSeries.getYMax();
			/*
			 * Set extra space at top of the bar series, e.g. to show
			 * the highest ion value.
			 * If yMin is 0, than it will remain be 0.
			 */
			yMin += yMin * SPACE_FACTOR;
			yMax += yMax * SPACE_FACTOR;
			ChartUtil.checkAndSetRange(abundance, yMin, yMax);
			addSpaceToTopOfAbundance(yMin, yMax);
			redrawIonScale();
			redrawRelativeAbundanceScale();
		}
	}

	private void addSpaceToTopOfAbundance(double yMin, double yMax) {

		Range range = abundance.getRange();
		/*
		 * Don't set the range if the upperLimit is below yMax
		 * as the interactive chart would be redrawn each time the
		 * user clicks on the chart and the bars would be lowered.
		 * That's not our intention.
		 */
		double lowerLimit = range.lower + range.lower * SPACE_FACTOR;
		double upperLimit = range.upper + range.upper * SPACE_FACTOR;
		/*
		 * Cut the upper range to yMax, only if it is higher than yMax.
		 * How could this happen? This is an interactive chart, so the
		 * upper range could be higher than yMax. If it is higher,
		 * than the method would run into an infinite redraw() loop as
		 * the ChartUtil.checkAndSetRange(abundance, yMin, yMax) method
		 * would reset the range.upper value ... and so on.
		 */
		if(lowerLimit <= yMin || upperLimit >= yMax) {
			range.lower = yMin;
			range.upper = yMax;
			abundance.setRange(range);
		}
	}

	// ------------------------------------------MouseListener
	@Override
	public void mouseDoubleClick(MouseEvent e) {

		super.mouseDoubleClick(e);
		/*
		 * Perform an update only if the current composite is in master mode.
		 */
		if(master) {
			/*
			 * Implement the master behavior.
			 */
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {

		super.mouseDown(e);
	}

	// ------------------------------------------MouseListener
	/**
	 * Initializes the chart with axes, series, colors.
	 */
	protected void initialize() {

		/*
		 * Add key and mouse listeners.
		 */
		multipleLineSeries = new MultipleSeries();
		// No title, no legend.
		getLegend().setVisible(false);
		getTitle().setVisible(false);
		createPrimaryAxes();
		createSecondaryAxes();
		// red, white and black
		setAxesAndBackgroundColors();
		getAxisSet().adjustRange();
	}

	private void redrawIonScale() {

		assert (ions != null) : "The ions instance must be not null.";
		assert (dalton != null) : "The dalton instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set minutes scale.
		 */
		range = ions.getRange();
		min = range.lower;
		max = range.upper;
		ChartUtil.setRange(dalton, min, max);
	}

	/**
	 * Redraws the relative abundance scale.
	 */
	private void redrawRelativeAbundanceScale() {

		assert (abundance != null) : "The abundance instance must be not null.";
		assert (relativeAbundance != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set the relative abundance scale.
		 */
		range = abundance.getRange();
		min = ChartUtil.getRelativeAbundance(maxSignal, range.lower);
		max = ChartUtil.getRelativeAbundance(maxSignal, range.upper);
		ChartUtil.setRange(relativeAbundance, min, max);
	}

	/**
	 * Creates the primary axes abundance and milliseconds.
	 */
	private void createPrimaryAxes() {

		/*
		 * Main Axes.
		 */
		IAxisSet axisSet = getAxisSet();
		ions = axisSet.getXAxis(0);
		abundance = axisSet.getYAxis(0);
		abundance.getTitle().setText("abundance");
		ions.getTitle().setText("m/z");
	}

	/**
	 * Creates the secondary axes minutes and relative abundance.<br/>
	 * The primary axis must be created first and setSeries has to be called at
	 * least once.
	 */
	private void createSecondaryAxes() {

		IAxisSet axisSet = getAxisSet();
		/*
		 * Minute axis
		 */
		int axisIdMinutes = axisSet.createXAxis();
		dalton = axisSet.getXAxis(axisIdMinutes);
		dalton.getTitle().setText("dalton");
		dalton.setPosition(Position.Secondary);
		/*
		 * Relative abundance axis
		 */
		int axisIdAbundanceRelative = axisSet.createYAxis();
		relativeAbundance = axisSet.getYAxis(axisIdAbundanceRelative);
		relativeAbundance.getTitle().setText("relative abundance");
		relativeAbundance.setPosition(Position.Secondary);
	}

	/**
	 * Sets the secondary ranges.
	 */
	private void setSecondaryRanges() {

		assert (chromatogramSelection != null) : "The chromatogramSelection instance must be not null.";
		assert (dalton != null) : "The dalton instance must be not null.";
		assert (relativeAbundance != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		Range range;
		/*
		 * dalton
		 */
		min = multipleLineSeries.getXMin();
		max = multipleLineSeries.getXMax();
		range = new Range(min, max);
		dalton.setRange(range);
		/*
		 * Relative Abundance Range
		 */
		min = ChartUtil.getRelativeAbundance(maxSignal, multipleLineSeries.getYMin());
		max = ChartUtil.getRelativeAbundance(maxSignal, multipleLineSeries.getYMax());
		range = new Range(min, max);
		relativeAbundance.setRange(range);
	}

	/**
	 * Sets the axes and background colors.
	 */
	private void setAxesAndBackgroundColors() {

		assert (abundance != null) : "The abundance instance must be not null.";
		assert (ions != null) : "The ions instance must be not null.";
		assert (dalton != null) : "The dalton instance must be not null.";
		assert (relativeAbundance != null) : "The relativeAbundance instance must be not null.";
		/*
		 * Color background, axes and line series
		 */
		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		ChartUtil.setAxisColor(abundance, Colors.BLACK);
		ChartUtil.setAxisColor(ions, Colors.BLACK);
		ChartUtil.setAxisColor(dalton, Colors.BLACK);
		ChartUtil.setAxisColor(relativeAbundance, Colors.BLACK);
	}

	/**
	 * Sets the chromatogram series.<br/>
	 * Subclasses may override this method to draw specific chromatographic
	 * values.
	 */
	protected void setSeries(boolean forceReload) {

		assert (chromatogramSelection != null) : "The chromatogramSelection instance must be not null.";
		assert (multipleLineSeries != null) : "The multipleLineSeries instance must be not null.";
		/*
		 * Delete the current and set the new series.
		 */
		deleteAllCurrentSeries();
		setViewSeries();
		/*
		 * Set the max abundance signal.
		 */
		double xMin = multipleLineSeries.getXMin();
		double xMax = multipleLineSeries.getXMax();
		/*
		 * Adjust only if a signal is available.
		 */
		if(xMin < xMax) {
			maxSignal = multipleLineSeries.getYMax();
			getAxisSet().adjustRange();
			setSecondaryRanges();
		}
		redraw();
	}

	/**
	 * Deletes all series.
	 */
	private void deleteAllCurrentSeries() {

		multipleLineSeries.removeAll();
		org.swtchart.ISeries[] series = getSeriesSet().getSeries();
		List<String> ids = new ArrayList<String>();
		/*
		 * Get the ids.
		 */
		for(org.swtchart.ISeries serie : series) {
			ids.add(serie.getId());
		}
		/*
		 * Remove all ids.
		 */
		for(String id : ids) {
			getSeriesSet().deleteSeries(id);
		}
	}
}
