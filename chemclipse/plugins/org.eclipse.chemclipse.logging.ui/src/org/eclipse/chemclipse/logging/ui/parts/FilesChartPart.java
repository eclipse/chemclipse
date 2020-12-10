/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.parts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.ui.support.FilesSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.ICircularSeries;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.ISeriesSet;
import org.eclipse.swtchart.ITitle;

public class FilesChartPart {

	private static final String SERIES_ID = "File Size";
	private Chart pieChart;

	@Inject
	public FilesChartPart(Composite parent) {

		pieChart = new Chart(parent, SWT.NONE);
		IPlotArea plotArea = pieChart.getPlotArea();
		if(plotArea instanceof Composite) {
			Composite composite = (Composite)plotArea;
			composite.addListener(SWT.MouseDoubleClick, new Listener() {

				public void handleEvent(Event event) {

					updateChart();
				}
			});
		}
		//
		updateChart();
	}

	@Focus
	public void setFocus() {

		updateChart();
	}

	private void updateChart() {

		/*
		 * Delete existing series.
		 */
		ISeriesSet seriesSet = pieChart.getSeriesSet();
		if(seriesSet.getSeries(SERIES_ID) != null) {
			pieChart.getSeriesSet().deleteSeries(SERIES_ID);
		}
		/*
		 * Chart
		 */
		ITitle title = pieChart.getTitle();
		title.setText("Log Files (Size)");
		title.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		pieChart.getLegend().setVisible(false);
		ICircularSeries<?> circularSeries = (ICircularSeries<?>)pieChart.getSeriesSet().createSeries(SeriesType.DOUGHNUT, SERIES_ID);
		/*
		 * Series
		 */
		List<File> files = FilesSupport.getSortedLogFiles(FilesSupport.getLogDirectory());
		List<String> labels = new ArrayList<>();
		List<Double> values = new ArrayList<>();
		//
		for(File file : files) {
			labels.add(file.getName());
			double length = file.length();
			values.add(length);
		}
		//
		String[] labelsInput = labels.toArray(new String[labels.size()]);
		double[] valuesInput = values.stream().mapToDouble(d -> d).toArray();
		//
		circularSeries.setSeries(labelsInput, valuesInput);
		pieChart.redraw();
	}
}
