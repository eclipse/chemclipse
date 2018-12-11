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
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.Range;

public class ChromatogramSelectionUI extends AbstractViewChromatogramUI {

	private static final Logger logger = Logger.getLogger(ChromatogramSelectionUI.class);
	/*
	 * Detection Type
	 */
	private static final String DETECTION_TYPE_SCAN = "DETECTION_TYPE_SCAN";
	//
	public static final String DETECTION_TYPE_BASELINE = "DETECTION_TYPE_BASELINE";
	public static final String DETECTION_TYPE_SCAN_BB = DETECTION_TYPE_SCAN + "_BB";
	public static final String DETECTION_TYPE_SCAN_VV = DETECTION_TYPE_SCAN + "_VV";
	public static final String DETECTION_TYPE_NONE = "";
	//
	public static final char KEY_BASELINE = 'd';
	public static final char KEY_BB = 'b';
	public static final char KEY_VV = 'v';
	/*
	 * Detection Box
	 */
	private static final String DETECTION_BOX_LEFT = "DETECTION_BOX_LEFT";
	private static final String DETECTION_BOX_RIGHT = "DETECTION_BOX_RIGHT";
	private static final String DETECTION_BOX_NONE = "DETECTION_BOX_NONE";
	//
	private static final int BOX_SNAP_MARKER_WINDOW = 4;
	private static final int BOX_MAX_DELTA = 1;
	/*
	 * Baseline Detection Method
	 */
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	//
	private int xMoveStart;
	//
	private String detectionType = DETECTION_TYPE_NONE;
	private String detectionBox = DETECTION_BOX_NONE;
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

	public void setDetectionType(String detectionType) {

		setDefaultCursor();
		resetBaselinePeakSelection();
		resetScanPeakSelection();
		//
		this.detectionType = detectionType;
		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			this.detectionBox = DETECTION_BOX_NONE;
		}
		//
		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			setDefaultCursor();
		} else {
			setCursor(SWT.CURSOR_CROSS);
		}
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		defaultCursor = getPlotArea().getCursor();
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		scanSelectionPaintListener = new ScanSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		plotArea.addCustomPaintListener(scanSelectionPaintListener);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			if(e.keyCode == KEY_BASELINE) {
				detectionType = DETECTION_TYPE_BASELINE;
			} else if(e.keyCode == KEY_BB) {
				detectionType = DETECTION_TYPE_SCAN_BB;
			} else if(e.keyCode == KEY_VV) {
				detectionType = DETECTION_TYPE_SCAN_VV;
			} else {
				detectionType = DETECTION_TYPE_NONE;
			}
		} else if(detectionType.startsWith(DETECTION_TYPE_SCAN)) {
			/*
			 * Detection Type Scan
			 */
			if(e.keyCode == 16777219) {
				/*
				 * Arrow left
				 */
				if(detectionBox.equals(DETECTION_BOX_LEFT)) {
					xStart -= 1;
					redrawScanPeakSelection(true);
				} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
					xStop -= 1;
					redrawScanPeakSelection(true);
				}
			} else if(e.keyCode == 16777220) {
				/*
				 * Arrow right
				 */
				if(detectionBox.equals(DETECTION_BOX_LEFT)) {
					xStart += 1;
					redrawScanPeakSelection(true);
				} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
					xStop += 1;
					redrawScanPeakSelection(true);
				}
			} else {
				super.keyPressed(e);
			}
		} else {
			super.keyPressed(e);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE) && e.button == 1) {
			startBaselinePeakSelection(e.x, e.y);
			setCursor(SWT.CURSOR_CROSS);
		} else if(detectionType.startsWith(DETECTION_TYPE_SCAN) && e.button == 1) {
			/*
			 * Set the move start coordinate.
			 */
			if(isLeftMoveSnapMarker(e.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
				xMoveStart = e.x;
				detectionBox = DETECTION_BOX_LEFT;
			} else if(isRightMoveSnapMarker(e.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
				xMoveStart = e.x;
				detectionBox = DETECTION_BOX_RIGHT;
			} else {
				setCursor(SWT.CURSOR_CROSS);
				detectionBox = DETECTION_BOX_NONE;
			}
		} else {
			super.mouseDown(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			stopBaselinePeakSelection(e.x, e.y);
			setDefaultCursor();
		} else if(detectionType.startsWith(DETECTION_TYPE_SCAN)) {
			/*
			 * Move.
			 */
			if(e.button == 1) {
				/*
				 * Set the peak.
				 */
				setCursor(SWT.CURSOR_CROSS);
				if(!detectionBox.equals(DETECTION_BOX_NONE)) {
					/*
					 * Validate that the snap marker is matched.
					 */
					int delta = getDeltaMove(e.x);
					if(detectionBox.equals(DETECTION_BOX_LEFT)) {
						xStart += delta;
						redrawScanPeakSelection(true);
					} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
						xStop += delta;
						redrawScanPeakSelection(true);
					}
				}
				/*
				 * Mark the selected box.
				 */
				if(e.count == 1) {
					detectionBox = getDetectionBox(e.x);
					redrawScanPeakSelection(false);
				}
			}
		} else {
			super.mouseUp(e);
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE) && e.stateMask == 524288) {
			trackBaselinePeakSelection(e.x, e.y);
		} else if(detectionType.startsWith(DETECTION_TYPE_SCAN)) {
			/*
			 * Mark the mouse
			 */
			if(isLeftMoveSnapMarker(e.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
			} else if(isRightMoveSnapMarker(e.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
			} else {
				setCursor(SWT.CURSOR_CROSS);
			}
			//
			if(e.stateMask == 524288) {
				if(!detectionBox.equals(DETECTION_BOX_NONE)) {
					/*
					 * Get the move delta.
					 */
					int delta = getDeltaMove(e.x);
					if(detectionBox.equals(DETECTION_BOX_LEFT)) {
						xStart += delta;
						redrawScanPeakSelection(false);
					} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
						xStop += delta;
						redrawScanPeakSelection(false);
					}
				}
			}
		} else {
			super.mouseMove(e);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

		if(detectionType.startsWith(DETECTION_TYPE_SCAN)) {
			//
			setCursor(SWT.CURSOR_CROSS);
			if(xStart == 0) {
				/*
				 * Get the y coordinate
				 */
				int y;
				//
				switch(detectionType) {
					case DETECTION_TYPE_SCAN_BB:
						y = getPlotArea().getBounds().height;
						break;
					case DETECTION_TYPE_SCAN_VV:
						y = e.y;
						break;
					default:
						y = getPlotArea().getBounds().height;
				}
				startScanPeakSelection(e.x, y);
			} else if(xStart > 0 && xStop == 0) {
				/*
				 * Get the y coordinate
				 */
				int y;
				//
				switch(detectionType) {
					case DETECTION_TYPE_SCAN_BB:
						y = getPlotArea().getBounds().height;
						break;
					case DETECTION_TYPE_SCAN_VV:
						y = e.y;
						break;
					default:
						y = getPlotArea().getBounds().height;
				}
				stopScanPeakSelection(e.x, y);
			} else {
				setDefaultCursor();
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

	private String getDetectionBox(int x) {

		if(xStart > 0) {
			if(x <= xStart) {
				return DETECTION_BOX_LEFT;
			} else {
				if(xStop > 0) {
					if(x >= xStop) {
						return DETECTION_BOX_RIGHT;
					}
				}
			}
		}
		return DETECTION_BOX_NONE;
	}

	private boolean isLeftMoveSnapMarker(int x) {

		if(x > xStart - BOX_SNAP_MARKER_WINDOW && x < xStart + BOX_SNAP_MARKER_WINDOW) {
			return true;
		}
		return false;
	}

	private boolean isRightMoveSnapMarker(int x) {

		if(x > xStop - BOX_SNAP_MARKER_WINDOW && x < xStop + BOX_SNAP_MARKER_WINDOW) {
			return true;
		}
		return false;
	}

	private int getDeltaMove(int x) {

		int delta = x - xMoveStart;
		if(Math.abs(delta) > BOX_MAX_DELTA) {
			if(delta < 0) {
				delta = -BOX_MAX_DELTA;
			} else {
				delta = BOX_MAX_DELTA;
			}
		}
		return delta;
	}

	private void startBaselinePeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		/*
		 * Set the start point.
		 */
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
	}

	private void stopBaselinePeakSelection(int x, int y) {

		xStop = x;
		yStop = y;
		/*
		 * Extract the selected peak
		 */
		extractSelectedPeak();
		resetBaselinePeakSelection();
	}

	private void trackBaselinePeakSelection(int x, int y) {

		xStop = x;
		yStop = y;
		//
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
		baselineSelectionPaintListener.setX2(xStop);
		baselineSelectionPaintListener.setY2(yStop);
		redraw();
	}

	private void resetBaselinePeakSelection() {

		detectionType = DETECTION_TYPE_NONE;
		baselineSelectionPaintListener.reset();
		resetSelectedRange();
		redraw();
	}

	private void startScanPeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		//
		setCursor(SWT.CURSOR_CROSS);
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
		extractSelectedPeak();
	}

	private void redrawScanPeakSelection(boolean extractPeak) {

		/*
		 * Set the start point.
		 */
		scanSelectionPaintListener.setX1(xStart);
		scanSelectionPaintListener.setX2(xStop);
		if(detectionBox.equals(DETECTION_BOX_LEFT)) {
			scanSelectionPaintListener.setHighlightBox(ScanSelectionPaintListener.HIGHLIGHT_BOX_LEFT);
		} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
			scanSelectionPaintListener.setHighlightBox(ScanSelectionPaintListener.HIGHLIGHT_BOX_RIGHT);
		} else {
			scanSelectionPaintListener.setHighlightBox(ScanSelectionPaintListener.HIGHLIGHT_BOX_NONE);
		}
		redraw();
		/*
		 * Extract the selected peak
		 */
		if(extractPeak) {
			extractSelectedPeak();
		}
	}

	private void resetScanPeakSelection() {

		resetSelectedRange();
		detectionType = DETECTION_TYPE_NONE;
		setDefaultCursor();
		scanSelectionPaintListener.reset();
		redraw();
	}

	/**
	 * Extracts the selected peak.
	 * 
	 * @param xStop
	 * @param yStop
	 */
	private void extractSelectedPeak() {

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

	private void setCursor(int cursorId) {

		getPlotArea().setCursor(Display.getCurrent().getSystemCursor(cursorId));
	}

	private void setDefaultCursor() {

		getPlotArea().setCursor(defaultCursor);
	}

	private void firePeakDetected(IPeak peak) {

		for(IPeakDetectionListener peakDetectionListener : peakDetectionListeners) {
			peakDetectionListener.peakDetected(peak);
		}
	}
}
