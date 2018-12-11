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
package org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.marker.MouseMoveMarker;
import org.eclipse.chemclipse.swt.ui.marker.SelectedPositionMarker;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
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

	public EditorChromatogramUI(Composite parent, int style) {
		super(parent, style);
		boolean yMinimumToZero = PreferenceSupplier.showBackgroundInChromatogramEditor();
		setYMinimumToZero(yMinimumToZero);
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionWSD) {
			/*
			 * Series
			 */
			IChromatogramSelectionWSD chromatogramSelection = (IChromatogramSelectionWSD)storedChromatogramSelection;
			ISeries series;
			ILineSeries lineSeries;
			ILineSeries selectedScanSeries;
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
			lineSeries.setLineColor(Colors.RED);
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
		 * Handles the scan selection.
		 * Left double click.
		 */
		if(e.button == 1) {
			handleScanSelection(e.x);
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
}
