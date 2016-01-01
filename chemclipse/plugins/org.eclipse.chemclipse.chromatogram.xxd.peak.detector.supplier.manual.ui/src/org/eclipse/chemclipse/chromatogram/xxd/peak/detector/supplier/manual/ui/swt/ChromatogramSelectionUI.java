/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;

import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.core.ManualPeakDetector;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramAndPeakSelectionUpdateNotifierCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramAndPeakSelectionUpdateNotifierMSD;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.AbstractViewChromatogramUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class ChromatogramSelectionUI extends AbstractViewChromatogramUI {

	private static final Logger logger = Logger.getLogger(ChromatogramSelectionUI.class);
	/*
	 * These will be set if mouse clicks will be performed.
	 */
	private int xStart;
	private int yStart;
	private boolean isKeyPressed = false;
	private boolean isManualPeakSelection = false;
	private Cursor defaultCursor;
	private PeakSelectionPaintListener peakSelectionPaintListener;

	/**
	 * @param parent
	 * @param style
	 */
	public ChromatogramSelectionUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		/*
		 * Add the peak selection listener.
		 */
		peakSelectionPaintListener = new PeakSelectionPaintListener();
		plotArea.addCustomPaintListener(peakSelectionPaintListener);
	}

	public void startDetectionMode() {

		isKeyPressed = true;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		/*
		 * Check if the "d" key is pressed and activate the manual peak detection.
		 * d = 100
		 * d = Manual Peak Detection
		 */
		if(!isKeyPressed && e.keyCode == 100) {
			isKeyPressed = true;
		} else {
			super.keyPressed(e);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {

		if(isKeyPressed && e.button == 1) {
			startManualPeakSelection(e.x, e.y);
		} else {
			super.mouseDown(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {

		if(isManualPeakSelection && e.button == 1) {
			stopManualPeakSelection(e.x, e.y);
		} else {
			super.mouseUp(e);
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {

		if(isManualPeakSelection) {
			trackManualPeakSelection(e.x, e.y);
		} else {
			super.mouseMove(e);
		}
	}

	@Override
	public void setViewSeries() {

		ISeries series;
		ILineSeries lineSeries;
		/*
		 * Converts the positive chromatogram.
		 */
		series = SeriesConverter.convertChromatogram(getChromatogramSelection(), Sign.POSITIVE, true);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
	}

	// -------------------------------------------private methods
	/**
	 * Starts the manual peak selection.
	 * 
	 * @param event
	 */
	private void startManualPeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		/*
		 * Preserve the old and set the new cursor.
		 */
		defaultCursor = getPlotArea().getCursor();
		getPlotArea().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		/*
		 * Start the manual peak selection.
		 */
		isManualPeakSelection = true;
		/*
		 * Set the start point.
		 */
		peakSelectionPaintListener.setX1(xStart);
		peakSelectionPaintListener.setY1(yStart);
	}

	/**
	 * Stops the manual peak selection.
	 * 
	 * @param event
	 */
	private void stopManualPeakSelection(int x, int y) {

		int xStop = x;
		int yStop = y;
		/*
		 * Reset the paint listener and cursor.
		 */
		isKeyPressed = false;
		isManualPeakSelection = false;
		peakSelectionPaintListener.reset();
		getPlotArea().setCursor(defaultCursor);
		/*
		 * Extract the selected peak
		 */
		extractSelectedPeak(xStop, yStop);
	}

	/**
	 * Extracts the selected peak.
	 * 
	 * @param xStop
	 * @param yStop
	 */
	private void extractSelectedPeak(int xStop, int yStop) {

		/*
		 * Calculate the rectangle factors.
		 */
		Rectangle rectangle = getPlotArea().getBounds();
		int height = rectangle.height;
		double factorHeight = 100.0d / height;
		int width = rectangle.width;
		double factorWidth = 100.0d / width;
		/*
		 * Calculate the percentage heights and widths.
		 */
		double percentageStartHeight = (100.0d - (factorHeight * yStart)) / 100.0d;
		double percentageStopHeight = (100.0d - (factorHeight * yStop)) / 100.0d;
		double percentageStartWidth = (factorWidth * xStart) / 100.0d;
		double percentageStopWidth = (factorWidth * xStop) / 100.0d;
		/*
		 * Calculate the start and end points.
		 */
		Range abundanceRange = getYAxisLeft().getRange();
		Range millisecondsRange = getXAxisTop().getRange();
		/*
		 * With abundance and retention time.
		 */
		double abundanceHeight = abundanceRange.upper - abundanceRange.lower;
		double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
		/*
		 * Peak start and stop abundances and retention times.
		 */
		int startRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
		int stopRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
		float startAbundance = (float)(abundanceRange.lower + abundanceHeight * percentageStartHeight);
		float stopAbundance = (float)(abundanceRange.lower + abundanceHeight * percentageStopHeight);
		/*
		 * Try to detect the peak.
		 */
		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * Peak Detection MSD
			 */
			try {
				IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
				ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
				IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
				IChromatogramPeakMSD chromatogramPeak = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				/*
				 * Fire an update.
				 * Show the peak in the ManualDetectedPeakView.
				 */
				ChromatogramAndPeakSelectionUpdateNotifierMSD.fireUpdateChange(chromatogramSelection, chromatogramPeak, false);
			} catch(PeakException e) {
				logger.warn(e);
			}
		} else if(storedChromatogramSelection instanceof IChromatogramSelectionCSD) {
			/*
			 * Peak Detection FID
			 */
			try {
				IChromatogramSelectionCSD chromatogramSelection = (IChromatogramSelectionCSD)storedChromatogramSelection;
				ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
				IChromatogramCSD chromatogram = chromatogramSelection.getChromatogramCSD();
				IChromatogramPeakCSD chromatogramPeak = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				/*
				 * Fire an update.
				 * Show the peak in the ManualDetectedPeakView.
				 */
				ChromatogramAndPeakSelectionUpdateNotifierCSD.fireUpdateChange(chromatogramSelection, chromatogramPeak, false);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Tracks the manual peak selection.
	 * 
	 * @param event
	 */
	private void trackManualPeakSelection(int x, int y) {

		peakSelectionPaintListener.setX2(x);
		peakSelectionPaintListener.setY2(y);
		redraw();
	}
	// -------------------------------------------private methods
}
