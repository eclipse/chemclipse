/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.thirdpartylibraries.swtchart.ext.InteractiveChartExtended;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.LineStyle;
import org.swtchart.Range;

public class ErrorResiduePage {

	private Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private Color COLOR_RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	//
	private int SYMBOL_SIZE = 8;
	//
	private PcaEditor pcaEditor;
	private InteractiveChartExtended errorResidueChart;

	public ErrorResiduePage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	public void update() {

		if(errorResidueChart != null) {
			/*
			 * Delete all other series.
			 */
			IPcaResults pcaResults = pcaEditor.getPcaResults();
			ISeriesSet seriesSet = errorResidueChart.getSeriesSet();
			ISeries[] series = seriesSet.getSeries();
			for(ISeries serie : series) {
				seriesSet.deleteSeries(serie.getId());
			}
			String[] fileNames = new String[pcaResults.getPcaResultMap().entrySet().size()];
			int count = 0;
			/*
			 * Data
			 */
			double[] errorResidue = new double[pcaResults.getPcaResultMap().size()];
			int counter = 0;
			errorResidueChart.getAxisSet().getXAxis(0).getTitle().setText("Sample Names");
			errorResidueChart.getAxisSet().getYAxis(0).getTitle().setText("Error Values(10^-6)");
			for(ISample key : pcaResults.getPcaResultMap().keySet()) {
				IPcaResult temp = pcaResults.getPcaResultMap().get(key);
				// Done to better display error values
				errorResidue[counter] = temp.getErrorMemberShip() * Math.pow(10, 6);
				counter++;
			}
			//
			for(Map.Entry<ISample, IPcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				/*
				 * Create the series.
				 */
				String name = entry.getKey().getName();
				fileNames[count] = name;
				count++;
			}
			ILineSeries scatterSeries = (ILineSeries)errorResidueChart.getSeriesSet().createSeries(SeriesType.LINE, "Samples");
			scatterSeries.setLineStyle(LineStyle.NONE);
			scatterSeries.setSymbolSize(SYMBOL_SIZE);
			double[] xSeries = new double[fileNames.length];
			for(int i = 0; i < fileNames.length; i++) {
				xSeries[i] = i + 1;
			}
			scatterSeries.setYSeries(errorResidue);
			scatterSeries.setXSeries(xSeries);
			/*
			 * Set the color.
			 */
			scatterSeries.setSymbolColor(COLOR_RED);
			scatterSeries.setSymbolType(PlotSymbolType.DIAMOND);
			errorResidueChart.getAxisSet().getXAxis(0).setCategorySeries(fileNames);
			errorResidueChart.getAxisSet().getXAxis(0).enableCategory(true);
			errorResidueChart.getAxisSet().adjustRange();
			errorResidueChart.redraw();
			errorResidueChart.update();
		}
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		//
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Error Residues");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(5, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		/*
		 * Plot the Error residue chart chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.NONE);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Error Chart");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update();
			}
		});
		//
		errorResidueChart = new InteractiveChartExtended(chartComposite, SWT.NONE);
		errorResidueChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		errorResidueChart.getTitle().setText("Error Residue Chart");
		errorResidueChart.getTitle().setForeground(COLOR_BLACK);
		//
		errorResidueChart.setBackground(COLOR_WHITE);
		errorResidueChart.getLegend().setVisible(false);
		//
		errorResidueChart.getAxisSet().getXAxis(0).getTitle().setText("Sample Names");
		errorResidueChart.getAxisSet().getXAxis(0).getTitle().setForeground(COLOR_BLACK);
		errorResidueChart.getAxisSet().getXAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		errorResidueChart.getAxisSet().getYAxis(0).getTitle().setText("Error Values(10^-6)");
		errorResidueChart.getAxisSet().getYAxis(0).getTitle().setForeground(COLOR_BLACK);
		errorResidueChart.getAxisSet().getYAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		String[] tempCategories = {" ", " ", " "};
		errorResidueChart.getAxisSet().getXAxis(0).setCategorySeries(tempCategories);
		errorResidueChart.getAxisSet().getXAxis(0).enableCategory(true);
		IPlotArea plotArea = (IPlotArea)errorResidueChart.getPlotArea();
		/*
		 * Plot a marker at zero.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = errorResidueChart.getAxisSet().getXAxes()[0].getRange();
				Range yRange = errorResidueChart.getAxisSet().getYAxes()[0].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = errorResidueChart.getPlotArea().getClientArea();
					int width = rectangle.width;
					int height = rectangle.height;
					int xWidth;
					int yHeight;
					/*
					 * Dependent where the zero values are.
					 * xDelta and yDelta can't be zero -> protect from division by zero.
					 */
					double xDelta = xRange.upper - xRange.lower;
					double yDelta = yRange.upper - yRange.lower;
					double xDiff = xRange.lower * -1; // lower is negative
					double yDiff = yRange.upper;
					double xPart = ((100 / xDelta) * xDiff) / 100; // percent -> 0.0 - 1.0
					double yPart = ((100 / yDelta) * yDiff) / 100; // percent -> 0.0 - 1.0
					xWidth = (int)(width * xPart);
					yHeight = (int)(height * yPart);
					/*
					 * Draw the line.
					 */
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawLine(xWidth, 0, xWidth, height); // Vertical line through zero
					e.gc.drawLine(0, yHeight, width, yHeight); // Horizontal line through zero
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = errorResidueChart.getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					String label = serie.getId();
					Point point = serie.getPixelCoordinates(0);
					/*
					 * Draw the label
					 */
					Point labelSize = e.gc.textExtent(label);
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawText("", (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		//
		tabItem.setControl(composite);
	}
}
