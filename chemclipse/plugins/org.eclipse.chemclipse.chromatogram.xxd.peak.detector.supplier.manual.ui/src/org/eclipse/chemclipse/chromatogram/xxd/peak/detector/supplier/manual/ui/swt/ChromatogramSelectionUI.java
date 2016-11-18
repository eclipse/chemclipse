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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.core.ManualPeakDetector;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramAndPeakSelectionUpdateNotifierCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
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

public class ChromatogramSelectionUI extends AbstractViewChromatogramUI {

	public static final String DETECTION_TYPE_BASELINE = "DETECTION_TYPE_BASELINE";
	public static final String DETECTION_TYPE_SCAN_BB = "DETECTION_TYPE_SCAN_BB";
	public static final String DETECTION_TYPE_SCAN_VV = "DETECTION_TYPE_SCAN_VV";
	public static final String DETECTION_TYPE_NONE = "";
	//
	private static final Logger logger = Logger.getLogger(ChromatogramSelectionUI.class);
	/*
	 * Baseline Detection Method
	 */
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	//
	private String detectionType = "";
	private boolean isBaselinePeakSelection = false;
	//
	private BaselineSelectionPaintListener baselineSelectionPaintListener;
	private ScanSelectionPaintListener scanSelectionPaintListener;
	//
	private Cursor defaultCursor;
	private List<IPeakDetectionListener> peakDetectionListeners;

	public ChromatogramSelectionUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
		peakDetectionListeners = new ArrayList<IPeakDetectionListener>();
	}

	public void addPeakDetectionListener(IPeakDetectionListener peakDetectionListener) {

		peakDetectionListeners.add(peakDetectionListener);
	}

	public void removePeakDetectionListener(IPeakDetectionListener peakDetectionListener) {

		peakDetectionListeners.remove(peakDetectionListener);
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		scanSelectionPaintListener = new ScanSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		plotArea.addCustomPaintListener(scanSelectionPaintListener);
	}

	public void startDetectionMode(String detectionType) {

		this.detectionType = detectionType;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			/*
			 * See ASCII code table.
			 * Baseline Modus (d = 100), Scan Modus (s = 115)
			 */
			if(e.keyCode == 100) {
				detectionType = DETECTION_TYPE_BASELINE;
			} else if(e.keyCode == 115) {
				detectionType = DETECTION_TYPE_SCAN_BB;
			} else {
				detectionType = "";
			}
		} else {
			super.keyPressed(e);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE) && e.button == 1) {
			startBaselinePeakSelection(e.x, e.y);
		} else {
			super.mouseDown(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {

		if(isBaselinePeakSelection && e.button == 1) {
			stopBaselinePeakSelection(e.x, e.y);
		} else {
			super.mouseUp(e);
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {

		if(isBaselinePeakSelection) {
			trackBaselinePeakSelection(e.x, e.y);
		} else {
			super.mouseMove(e);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

		if(detectionType.equals(DETECTION_TYPE_SCAN_BB)) {
			if(xStart == 0) {
				int y = getPlotArea().getBounds().height;
				startScanPeakSelection(e.x, y);
			} else if(xStart > 0 && xStop == 0) {
				int y = getPlotArea().getBounds().height;
				stopScanPeakSelection(e.x, y);
			} else {
				resetScanPeakSelection();
			}
		} else {
			super.mouseDoubleClick(e);
		}
	}

	@Override
	public void setViewSeries() {

		/*
		 * Converts the positive chromatogram.
		 */
		ISeries series = SeriesConverter.convertChromatogram(getChromatogramSelection(), Sign.POSITIVE, true);
		addSeries(series);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
	}

	private void startBaselinePeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		isBaselinePeakSelection = true;
		defaultCursor = getPlotArea().getCursor();
		getPlotArea().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		/*
		 * Set the start point.
		 */
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
	}

	private void stopBaselinePeakSelection(int x, int y) {

		xStop = x;
		yStop = y;
		detectionType = DETECTION_TYPE_NONE;
		isBaselinePeakSelection = false;
		baselineSelectionPaintListener.reset();
		getPlotArea().setCursor(defaultCursor);
		/*
		 * Extract the selected peak
		 */
		extractSelectedPeak(xStop, yStop);
		resetSelectedRange();
	}

	private void startScanPeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		defaultCursor = getPlotArea().getCursor();
		getPlotArea().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		/*
		 * Set the start point.
		 */
		scanSelectionPaintListener.setX1(xStart);
		scanSelectionPaintListener.setX2(xStop);
		redraw();
	}

	private void stopScanPeakSelection(int x, int y) {

		xStop = x;
		yStop = y;
		/*
		 * Set the start point.
		 */
		scanSelectionPaintListener.setX1(xStart);
		scanSelectionPaintListener.setX2(xStop);
		redraw();
		/*
		 * Extract the selected peak
		 */
		extractSelectedPeak(xStop, yStop);
	}

	private void resetScanPeakSelection() {

		resetSelectedRange();
		detectionType = DETECTION_TYPE_NONE;
		getPlotArea().setCursor(defaultCursor);
		scanSelectionPaintListener.reset();
		redraw();
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
				firePeakDetected(chromatogramPeak);
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
				firePeakDetected(chromatogramPeak);
				ChromatogramAndPeakSelectionUpdateNotifierCSD.fireUpdateChange(chromatogramSelection, chromatogramPeak, false);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
	}

	private void resetSelectedRange() {

		xStart = 0;
		yStart = 0;
		xStop = 0;
		yStop = 0;
	}

	private void trackBaselinePeakSelection(int x, int y) {

		baselineSelectionPaintListener.setX2(x);
		baselineSelectionPaintListener.setY2(y);
		//
		redraw();
	}

	private void firePeakDetected(IPeak peak) {

		for(IPeakDetectionListener peakDetectionListener : peakDetectionListeners) {
			peakDetectionListener.peakDetected(peak);
		}
	}
}
