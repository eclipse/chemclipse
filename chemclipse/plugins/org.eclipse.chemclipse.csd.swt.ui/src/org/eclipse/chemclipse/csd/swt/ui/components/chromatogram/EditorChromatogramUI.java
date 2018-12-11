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
package org.eclipse.chemclipse.csd.swt.ui.components.chromatogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.swt.ui.converter.SeriesConverterCSD;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.exceptions.NoIdentifiedScansAvailableException;
import org.eclipse.chemclipse.swt.ui.exceptions.NoPeaksAvailableException;
import org.eclipse.chemclipse.swt.ui.marker.MouseMoveMarker;
import org.eclipse.chemclipse.swt.ui.marker.SelectedPositionMarker;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.LineStyle;

public class EditorChromatogramUI extends AbstractEditorChromatogramUI {

	private MouseMoveMarker mouseMoveMarker;
	private SelectedPositionMarker selectedPositionMarker;
	private PeakRetentionTimeComparator chromatogramPeakComparator;

	public EditorChromatogramUI(Composite parent, int style) {
		super(parent, style);
		boolean yMinimumToZero = PreferenceSupplier.showBackgroundInChromatogramEditor();
		setYMinimumToZero(yMinimumToZero);
		chromatogramPeakComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionCSD) {
			//
			IChromatogramSelectionCSD chromatogramSelection = (IChromatogramSelectionCSD)storedChromatogramSelection;
			/*
			 * Series
			 */
			ISeries series;
			ILineSeries lineSeries;
			ILineSeries selectedScanSeries;
			ILineSeries scatterSeries;
			ILineSeries peakSeries;
			ILineSeries backgroundSeries;
			/*
			 * Convert a selection.
			 */
			series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, false);
			addSeries(series);
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			Color chromatogramColor = PreferenceSupplier.getChromatogramColor();
			if(chromatogramColor == null) {
				chromatogramColor = Colors.RED;
			}
			lineSeries.setLineColor(chromatogramColor);
			/*
			 * Set the selected scan.
			 */
			IScan selectedScan = chromatogramSelection.getSelectedScan();
			if(selectedScan != null) {
				series = getSelectedScanSeries(selectedScan);
				selectedScanSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				selectedScanSeries.setXSeries(series.getXSeries());
				selectedScanSeries.setYSeries(series.getYSeries());
				selectedScanSeries.setLineStyle(LineStyle.NONE);
				selectedScanSeries.setSymbolType(PlotSymbolType.CROSS);
				selectedScanSeries.setSymbolSize(5);
				selectedScanSeries.setSymbolColor(Colors.DARK_RED);
			}
			/*
			 * Set the detected peaks and the selected peak if available.
			 */
			try {
				/*
				 * Active Peaks
				 */
				series = SeriesConverterCSD.convertPeakMaxPositions(chromatogramSelection, new Offset(0, 0), Sign.POSITIVE, true);
				scatterSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				scatterSeries.setXSeries(series.getXSeries());
				scatterSeries.setYSeries(series.getYSeries());
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
				scatterSeries.setSymbolSize(5);
				scatterSeries.setLineColor(Colors.GRAY);
				scatterSeries.setSymbolColor(Colors.DARK_GRAY);
			} catch(NoPeaksAvailableException e) {
				/*
				 * Do nothing.
				 * Just don't add the series.
				 */
			}
			/*
			 * Inactive Peaks
			 */
			try {
				series = SeriesConverterCSD.convertPeakMaxPositions(chromatogramSelection, new Offset(0, 0), Sign.POSITIVE, false);
				scatterSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				scatterSeries.setXSeries(series.getXSeries());
				scatterSeries.setYSeries(series.getYSeries());
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
				scatterSeries.setSymbolSize(5);
				scatterSeries.setLineColor(Colors.GRAY);
				scatterSeries.setSymbolColor(Colors.GRAY);
			} catch(NoPeaksAvailableException e) {
				/*
				 * Do nothing.
				 * Just don't add the series.
				 */
			}
			/*
			 * Show the selected peak if available
			 */
			IPeakCSD peak = chromatogramSelection.getSelectedPeak();
			if(peak != null && PreferenceSupplier.showSelectedPeakInEditor()) {
				/*
				 * Peak
				 */
				series = SeriesConverterCSD.convertSelectedPeak(peak, true, Sign.POSITIVE);
				peakSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				peakSeries.setXSeries(series.getXSeries());
				peakSeries.setYSeries(series.getYSeries());
				peakSeries.enableArea(true);
				if(PreferenceSupplier.showScansOfSelectedPeak()) {
					peakSeries.setSymbolType(PlotSymbolType.CIRCLE);
					peakSeries.setSymbolColor(Colors.DARK_RED);
					int size = PreferenceSupplier.sizeOfPeakScanMarker();
					peakSeries.setSymbolSize(size);
				} else {
					peakSeries.setSymbolType(PlotSymbolType.NONE);
				}
				peakSeries.setLineColor(Colors.DARK_RED);
				/*
				 * Background
				 */
				series = SeriesConverterCSD.convertSelectedPeakBackground(peak, Sign.POSITIVE);
				backgroundSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				backgroundSeries.setXSeries(series.getXSeries());
				backgroundSeries.setYSeries(series.getYSeries());
				backgroundSeries.enableArea(true);
				backgroundSeries.setSymbolType(PlotSymbolType.NONE);
				backgroundSeries.setLineColor(Colors.BLACK);
			}
			/*
			 * Set the identified scans marker if available.
			 */
			try {
				series = SeriesConverterCSD.convertIdentifiedScans(chromatogramSelection, new Offset(0, 0), Sign.POSITIVE);
				scatterSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				scatterSeries.setXSeries(series.getXSeries());
				scatterSeries.setYSeries(series.getYSeries());
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolType(PlotSymbolType.CIRCLE);
				scatterSeries.setSymbolSize(3);
				scatterSeries.setLineColor(Colors.GRAY);
				scatterSeries.setSymbolColor(Colors.DARK_GRAY);
			} catch(NoIdentifiedScansAvailableException e) {
				/*
				 * Do nothing.
				 * Just don't add the series.
				 */
			}
		}
	}

	private ISeries getSelectedScanSeries(IScan selectedScan) {

		double[] xSeries = new double[]{selectedScan.getRetentionTime()};
		int totalSignal = (int)selectedScan.getTotalSignal();
		double[] ySeries = new double[]{totalSignal};
		return new Series(xSeries, ySeries, "Selected Scan");
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		/*
		 * The mouse move marker draws a vertical line in the plot area.
		 */
		mouseMoveMarker = new MouseMoveMarker();
		plotArea.addCustomPaintListener(mouseMoveMarker);
		/*
		 * The selection position marker draws the selected retention time and abundance.
		 */
		if(PreferenceSupplier.showChromatogramPositionMarkerBox()) {
			Color foregroundColor = PreferenceSupplier.getPositionMarkerForegroundColor();
			Color backgroundColor = PreferenceSupplier.getPositionMarkerBackgroundColor();
			selectedPositionMarker = new SelectedPositionMarker(foregroundColor, backgroundColor);
			plotArea.addCustomPaintListener(selectedPositionMarker);
			getPlotArea().addMouseMoveListener(new MouseMoveListener() {

				@Override
				public void mouseMove(MouseEvent e) {

					mouseMoveMarker.setXPosition(e.x);
					double abundance = getSelectedAbundance(e.y);
					int retentionTimeInMilliseconds = getSelectedRetentionTimeAsMilliseconds(e.x);
					selectedPositionMarker.setActualPosition(retentionTimeInMilliseconds, abundance);
					getPlotArea().redraw();
				}
			});
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

		/*
		 * Handles the scan and peak selection.
		 * Left double click.
		 */
		if(e.button == 1) {
			if(e.stateMask == SWT.CTRL) {
				handlePeakSelection(e.x);
			} else {
				handleScanSelection(e.x);
			}
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e) {

		handleMouseWheelSelection(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		int keyCode = e.keyCode;
		int stateMask = e.stateMask;
		/*
		 * Catch events:
		 * STRG + > (keyCode=0x1000004 keyLocation=0x0 stateMask=0x40000)
		 * STRG + < (keyCode=0x1000003 keyLocation=0x0 stateMask=0x40000)
		 */
		if(stateMask == SWT.CTRL) {
			if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT) {
				handleControlScanSelection(keyCode);
			} else {
				super.keyPressed(e);
			}
		} else {
			if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT || keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN) {
				handleArrowMoveWindowSelection(keyCode);
			}
		}
	}

	/**
	 * Get the nearest peak at the given retention time.
	 * 
	 * @param x
	 */
	private void handlePeakSelection(int x) {

		/*
		 * Get the peak
		 */
		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionCSD) {
			IChromatogramSelectionCSD chromatogramSelection = (IChromatogramSelectionCSD)storedChromatogramSelection;
			int retentionTime = getRetentionTime(x);
			IChromatogramPeakCSD selectedPeak = chromatogramSelection.getChromatogramCSD().getPeak(retentionTime);
			if(selectedPeak != null) {
				/*
				 * Fire an update.
				 */
				chromatogramSelection.setSelectedPeak(selectedPeak, true);
				if(PreferenceSupplier.isMoveRetentionTimeOnPeakSelection()) {
					adjustChromatogramSelection(selectedPeak, chromatogramSelection);
				}
				chromatogramSelection.update(true);
			}
		}
	}

	private void adjustChromatogramSelection(IChromatogramPeakCSD selectedPeak, IChromatogramSelectionCSD chromatogramSelection) {

		/*
		 * TODO refactor Editor CSD, MSD, WSD and peak list view!
		 */
		IChromatogramCSD chromatogramCSD = chromatogramSelection.getChromatogramCSD();
		List<IChromatogramPeakCSD> peaks = new ArrayList<>(chromatogramCSD.getPeaks());
		List<IChromatogramPeakCSD> peaksSelection = new ArrayList<>(chromatogramCSD.getPeaks(chromatogramSelection));
		Collections.sort(peaks, chromatogramPeakComparator);
		Collections.sort(peaksSelection, chromatogramPeakComparator);
		//
		if(peaks.get(0).equals(selectedPeak) || peaks.get(peaks.size() - 1).equals(selectedPeak)) {
			/*
			 * Don't move if it is the first or last peak of the chromatogram.
			 */
		} else {
			/*
			 * First peak of the selection: move left
			 * Last peak of the selection: move right
			 */
			if(peaksSelection.get(0).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
			} else if(peaksSelection.get(peaksSelection.size() - 1).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
			}
		}
	}
}
