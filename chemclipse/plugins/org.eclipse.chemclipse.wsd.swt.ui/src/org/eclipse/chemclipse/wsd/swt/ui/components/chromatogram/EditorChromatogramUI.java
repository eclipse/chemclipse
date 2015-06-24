/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.marker.GCPoint;
import org.eclipse.chemclipse.swt.ui.marker.IGCPoint;
import org.eclipse.chemclipse.swt.ui.marker.MouseMoveMarker;
import org.eclipse.chemclipse.swt.ui.marker.SelectedPositionMarker;
import org.eclipse.chemclipse.swt.ui.marker.SelectedScanMarker;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class EditorChromatogramUI extends AbstractEditorChromatogramUI {

	public static final String SELECTED_SCAN_MARKER = "*";
	private SelectedScanMarker selectedScanMarker;
	private MouseMoveMarker mouseMoveMarker;
	private SelectedPositionMarker selectedPositionMarker;
	/*
	 * These will be set if mouse clicks will be performed.
	 */
	private int xStart, yStart;

	public EditorChromatogramUI(Composite parent, int style) {

		super(parent, style);
	}

	@Override
	public void setViewSeries() {

		/*
		 * This chromatogram selection is a instance of IChromatogramSelectionWSD
		 */
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		/*
		 * Series
		 */
		ISeries series;
		ILineSeries lineSeries;
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
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		/*
		 * Add the scan marker.
		 */
		selectedScanMarker = new SelectedScanMarker();
		plotArea.addCustomPaintListener(selectedScanMarker);
		/*
		 * The mouse move marker draws a vertical line in the plot area.
		 */
		mouseMoveMarker = new MouseMoveMarker();
		plotArea.addCustomPaintListener(mouseMoveMarker);
		/*
		 * The selection position marker draws the selected retention time and abundance.
		 */
		selectedPositionMarker = new SelectedPositionMarker(null, null);
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

		super.mouseDoubleClick(e);
		/*
		 * Update the selected scan marker position.
		 */
		IGCPoint point = new GCPoint(e.x, e.y);
		selectedScanMarker.setMarker(SELECTED_SCAN_MARKER);
		selectedScanMarker.setPoint(point);
	}

	@Override
	public void mouseDown(MouseEvent e) {

		super.mouseDown(e);
		xStart = e.x;
		yStart = e.y;
	}

	@Override
	public void mouseUp(MouseEvent e) {

		super.mouseUp(e);
		/*
		 * Remove the selected scan marker.
		 */
		if(isMaster() && e.button == 1) {
			if(xStart != e.x || yStart != e.y) {
				resetSelectedScanMarker();
			}
		}
	}

	@Override
	protected void adjustChromatogramSelection() {

		super.adjustChromatogramSelection();
		resetSelectedScanMarker();
	}

	/**
	 * Resets the selected scan marker.
	 */
	private void resetSelectedScanMarker() {

		selectedScanMarker.setMarker("");
	}
}
