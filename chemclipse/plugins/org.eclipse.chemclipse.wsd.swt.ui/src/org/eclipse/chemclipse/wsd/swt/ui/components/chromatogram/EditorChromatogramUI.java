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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.marker.MouseMoveMarker;
import org.eclipse.chemclipse.swt.ui.marker.SelectedPositionMarker;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;

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
