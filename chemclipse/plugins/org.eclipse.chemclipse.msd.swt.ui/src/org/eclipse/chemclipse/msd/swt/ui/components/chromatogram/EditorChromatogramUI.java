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
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
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
import org.swtchart.IErrorBar;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

/**
 * Use this class as the basis for presentations of chromatograms that are used
 * in an editor.<br/>
 * Don't forget to set master mode :-).
 */
public class EditorChromatogramUI extends AbstractEditorChromatogramUI {

	private MouseMoveMarker mouseMoveMarker;
	private SelectedPositionMarker selectedPositionMarker;

	public EditorChromatogramUI(Composite parent, int style) {
		super(parent, style);
		boolean yMinimumToZero = PreferenceSupplier.showBackgroundInChromatogramEditor();
		setYMinimumToZero(yMinimumToZero);
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			//
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
			/*
			 * Series
			 */
			ISeries series;
			ILineSeries lineSeries;
			ILineSeries selectedScanSeries;
			ILineSeries selectedIdentifiedScanSeries;
			ILineSeries scatterSeries;
			ILineSeries peakSeries;
			ILineSeries backgroundSeries;
			/*
			 * Convert a selection.
			 */
			boolean showChromatogramArea = PreferenceSupplier.showChromatogramArea();
			series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
			addSeries(series);
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(showChromatogramArea);
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
				series = getScanSeries(selectedScan, "Selected Scan");
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
				series = SeriesConverterMSD.convertPeaks(chromatogramSelection, new Offset(0, 0), Sign.POSITIVE);
				scatterSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				scatterSeries.setXSeries(series.getXSeries());
				scatterSeries.setYSeries(series.getYSeries());
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
				scatterSeries.setSymbolSize(5);
				scatterSeries.setLineColor(Colors.GRAY);
				scatterSeries.setSymbolColor(Colors.DARK_GRAY);
				/*
				 * Setting up a dummy error bar
				 */
				IErrorBar errorBar = scatterSeries.getYErrorBar();
				errorBar.setColor(Colors.DARK_CYAN);
				errorBar.setType(org.swtchart.IErrorBar.ErrorBarType.BOTH);
				errorBar.setLineWidth(2);
				errorBar.setError(10000.0d);
				errorBar.setVisible(false);
				/*
				 * Show the selected peak if available
				 */
				IPeakMSD peak = chromatogramSelection.getSelectedPeak();
				if(peak != null && PreferenceSupplier.showSelectedPeakInEditor()) {
					/*
					 * Peak
					 */
					series = SeriesConverterMSD.convertSelectedPeak(peak, true, Sign.POSITIVE);
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
					series = SeriesConverterMSD.convertSelectedPeakBackground(peak, Sign.POSITIVE);
					backgroundSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
					backgroundSeries.setXSeries(series.getXSeries());
					backgroundSeries.setYSeries(series.getYSeries());
					backgroundSeries.enableArea(true);
					backgroundSeries.setSymbolType(PlotSymbolType.NONE);
					backgroundSeries.setLineColor(Colors.BLACK);
				}
			} catch(NoPeaksAvailableException e) {
				/*
				 * Do nothing.
				 * Just don't add the series.
				 */
			}
			/*
			 * Set the identified scans marker if available.
			 */
			try {
				series = SeriesConverterMSD.convertIdentifiedScans(chromatogramSelection, new Offset(0, 0), Sign.POSITIVE);
				scatterSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				scatterSeries.setXSeries(series.getXSeries());
				scatterSeries.setYSeries(series.getYSeries());
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolType(PlotSymbolType.CIRCLE);
				scatterSeries.setSymbolSize(3);
				scatterSeries.setLineColor(Colors.GRAY);
				scatterSeries.setSymbolColor(Colors.DARK_GRAY);
				/*
				 * Set the selected identified scan.
				 */
				IScan selectedIdentifiedScan = chromatogramSelection.getSelectedIdentifiedScan();
				if(selectedIdentifiedScan != null) {
					series = getScanSeries(selectedIdentifiedScan, "Selected Identified Scan");
					selectedIdentifiedScanSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
					selectedIdentifiedScanSeries.setXSeries(series.getXSeries());
					selectedIdentifiedScanSeries.setYSeries(series.getYSeries());
					selectedIdentifiedScanSeries.setLineStyle(LineStyle.NONE);
					selectedIdentifiedScanSeries.setSymbolType(PlotSymbolType.CIRCLE);
					selectedIdentifiedScanSeries.setSymbolSize(5);
					selectedIdentifiedScanSeries.setSymbolColor(Colors.DARK_RED);
				}
			} catch(NoIdentifiedScansAvailableException e) {
				/*
				 * Do nothing.
				 * Just don't add the series.
				 */
			}
		}
	}

	private ISeries getScanSeries(IScan selectedScan, String id) {

		double[] xSeries = new double[]{selectedScan.getRetentionTime()};
		int totalSignal = (int)selectedScan.getTotalSignal();
		double[] ySeries = new double[]{totalSignal};
		return new Series(xSeries, ySeries, id);
	}

	// ---------------------------------------------------------------ISeriesSetter
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
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
			int retentionTime = getRetentionTime(x);
			IChromatogramPeakMSD selectedPeak = chromatogramSelection.getChromatogramMSD().getPeak(retentionTime);
			if(selectedPeak != null) {
				/*
				 * Fire an update.
				 */
				chromatogramSelection.setSelectedPeak(selectedPeak, true);
				chromatogramSelection.update(true);
			}
		}
	}
}
