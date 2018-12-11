/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.swt.ui.base.InteractiveChartExtended;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.Range;

public abstract class AbstractBarSeriesUI extends InteractiveChartExtended implements ISeriesSetter, KeyListener, MouseListener {

	protected boolean master = false;
	/*
	 * Subclasses may override setViewSeries().
	 */
	protected IChromatogramSelectionWSD chromatogramSelection;
	protected IScanWSD supplierScan;
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
	private IAxis wavelength;
	private IAxis nm;
	private IAxis abundance;
	private IAxis relativeAbundance;
	/*
	 * These will be set if mouse clicks will be performed.
	 */
	private int xStart, yStart;
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

	/**
	 * Clears the current bar series.
	 */
	public void clear() {

		deleteAllCurrentSeries();
		maxSignal = 1.0d;
		supplierScan = null;
		List<IAxis> axisList = new ArrayList<IAxis>();
		axisList.add(wavelength);
		axisList.add(nm);
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
	 * update method from {@link IChromatogramSelectionWSDUpdateNotifier} calls forceReload ==
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

	// ------------------------------------------------------Update Scan
	public void update(IScanWSD supplierScan, boolean forceReload) {

		this.supplierScan = supplierScan;
		/*
		 * If the current view is not a master, reload the data on each
		 * update.<br/> If the ui is in master mode and force reload is set,
		 * load the series.
		 */
		if(!master || (master && forceReload)) {
			setSeries(forceReload);
		}
	}

	// ------------------------------------------------------Update Scan
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
			ChartUtil.checkAndSetRange(wavelength, xMin, xMax);
			double yMin = multipleLineSeries.getYMin();
			double yMax = multipleLineSeries.getYMax();
			/*
			 * Set extra space at top of the bar series, e.g. to show
			 * the highest ion value.
			 */
			yMax += yMax * SPACE_FACTOR;
			ChartUtil.checkAndSetRange(abundance, yMin, yMax);
			addSpaceToTopOfAbundance(yMax);
			redrawIonScale();
			redrawRelativeAbundanceScale();
		}
	}

	private void addSpaceToTopOfAbundance(double yMax) {

		Range range = abundance.getRange();
		/*
		 * Don't set the range if the upperLimit is below yMax
		 * as the interactive chart would be redrawn each time the
		 * user clicks on the chart and the bars would be lowered.
		 * That's not our intention.
		 */
		double upperLimit = range.upper + range.upper * SPACE_FACTOR;
		/*
		 * Cut the upper range to yMax, only if it is higher than yMax.
		 * How could this happen? This is an interactive chart, so the
		 * upper range could be higher than yMax. If it is higher,
		 * than the method would run into an infinite redraw() loop as
		 * the ChartUtil.checkAndSetRange(abundance, yMin, yMax) method
		 * would reset the range.upper value ... and so on.
		 */
		if(upperLimit >= yMax) {
			range.upper = yMax;
			abundance.setRange(range);
		}
	}

	// ------------------------------------------KeyListener
	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	// ------------------------------------------KeyListener
	// ------------------------------------------MouseListener
	@Override
	public void mouseDoubleClick(MouseEvent e) {

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

		xStart = e.x;
		yStart = e.y;
	}

	@Override
	public void mouseUp(MouseEvent e) {

		/*
		 * Perform an update only if the current composite is in master
		 * mode.<br/> Do the update only if the left mouse button (e.button ==
		 * 1) was pressed.<br/> <br/> If the implementing class is a peak, and
		 * not a chromatgram, the corresponding chromatogram scan will
		 * be shown.<br/> Otherwise, the peak could override this method.
		 */
		if(master && e.button == 1) {
			/*
			 * The start and stop coordinates will be different, if the user
			 * selects an area.<br/> In such a case, update the chromatogram
			 * selection.<br/> If the user performs a single or double click,
			 * the start and stop position will be the same.<br/> Don't waste
			 * processor time and throw an update in such a case.
			 */
			if(xStart != e.x || yStart != e.y) {
				System.out.println("wavelength selection");
			}
		}
	}

	// ------------------------------------------MouseListener
	/**
	 * Initializes the chart with axes, series, colors.
	 */
	protected void initialize() {

		/*
		 * Add key and mouse listeners.
		 */
		getPlotArea().addKeyListener(this);
		getPlotArea().addMouseListener(this);
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

		assert (wavelength != null) : "The ions instance must be not null.";
		assert (nm != null) : "The dalton instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set minutes scale.
		 */
		range = wavelength.getRange();
		min = range.lower;
		max = range.upper;
		ChartUtil.setRange(nm, min, max);
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
		wavelength = axisSet.getXAxis(0);
		abundance = axisSet.getYAxis(0);
		abundance.getTitle().setText("abundance");
		wavelength.getTitle().setText("wavelength");
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
		nm = axisSet.getXAxis(axisIdMinutes);
		nm.getTitle().setText("nm");
		nm.setPosition(Position.Secondary);
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
		assert (nm != null) : "The nm instance must be not null.";
		assert (relativeAbundance != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		Range range;
		/*
		 * dalton
		 */
		min = multipleLineSeries.getXMin();
		max = multipleLineSeries.getXMax();
		range = new Range(min, max);
		nm.setRange(range);
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
		assert (wavelength != null) : "The wavelength instance must be not null.";
		assert (nm != null) : "The dalton instance must be not null.";
		assert (relativeAbundance != null) : "The relativeAbundance instance must be not null.";
		/*
		 * Color background, axes and line series
		 */
		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		ChartUtil.setAxisColor(abundance, Colors.BLACK);
		ChartUtil.setAxisColor(wavelength, Colors.BLACK);
		ChartUtil.setAxisColor(nm, Colors.BLACK);
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
}
