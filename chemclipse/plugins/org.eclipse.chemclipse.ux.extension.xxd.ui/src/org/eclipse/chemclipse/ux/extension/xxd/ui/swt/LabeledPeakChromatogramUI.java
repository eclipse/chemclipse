/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.AbstractViewChromatogramUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;

public class LabeledPeakChromatogramUI extends AbstractViewChromatogramUI {

	private ISeries peakSeries = null;
	private Color foregroundColor;

	public LabeledPeakChromatogramUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesMassScale());
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			/*
			 * Chromatogram
			 */
			ISeries series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
			addSeries(series);
			ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(Colors.BLACK);
		}
	}

	@Override
	protected void initialize() {

		super.initialize();
		foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			public void paintControl(PaintEvent e) {

				if(peakSeries != null) {
					paintPeakNames(peakSeries, false, e);
				}
			}

			public boolean drawBehindSeries() {

				return false;
			}
		});
	}

	protected void paintPeakNames(ISeries peakSeries, boolean mirrored, PaintEvent e) {

		e.gc.setForeground(foregroundColor);
		// TODO draw the name of the best identification
	}
}
