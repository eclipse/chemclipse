/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
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

public class ScorePlotPage {

	private Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private Map<String, Color> colors;
	//
	private PcaEditor pcaEditor;
	private List<ISample> samples = new ArrayList<>();
	private InteractiveChartExtended scorePlotChart;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;
	//
	private int SYMBOL_SIZE = 8;

	public ScorePlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Score Plot");
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
		spinnerComposite.setLayout(new GridLayout(6, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label;
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText("PC X-Axis: ");
		spinnerPCx = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCx.setMinimum(1);
		spinnerPCx.setMaximum(1);
		spinnerPCx.setIncrement(1);
		spinnerPCx.setLayoutData(gridData);
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText(" PC Y-Axis: ");
		spinnerPCy = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCy.setMinimum(1);
		spinnerPCy.setMaximum(1);
		spinnerPCy.setIncrement(1);
		spinnerPCy.setLayoutData(gridData);
		//
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Score Plot");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reloadScorePlotChart();
			}
		});
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText(" Select samples");
		button.addListener(SWT.Selection, e -> {
			pcaEditor.openSamplesSelectionDialog();
		});
		/*
		 * Plot the PCA chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.BORDER);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		scorePlotChart = new InteractiveChartExtended(chartComposite, SWT.NONE);
		scorePlotChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlotChart.getTitle().setText("PCA Score Plot");
		scorePlotChart.getTitle().setForeground(COLOR_BLACK);
		//
		scorePlotChart.setBackground(COLOR_WHITE);
		scorePlotChart.getLegend().setVisible(false);
		//
		scorePlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + spinnerPCx.getSelection());
		scorePlotChart.getAxisSet().getXAxis(0).getTitle().setForeground(COLOR_BLACK);
		scorePlotChart.getAxisSet().getXAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		scorePlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + spinnerPCy.getSelection());
		scorePlotChart.getAxisSet().getYAxis(0).getTitle().setForeground(COLOR_BLACK);
		scorePlotChart.getAxisSet().getYAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		IPlotArea plotArea = (IPlotArea)scorePlotChart.getPlotArea();
		/*
		 * Plot a marker at zero.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public boolean drawBehindSeries() {

				return false;
			}

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = scorePlotChart.getAxisSet().getXAxes()[0].getRange();
				Range yRange = scorePlotChart.getAxisSet().getYAxes()[0].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = scorePlotChart.getPlotArea().getClientArea();
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
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public boolean drawBehindSeries() {

				return false;
			}

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = scorePlotChart.getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					String label = serie.getId();
					Point point = serie.getPixelCoordinates(0);
					/*
					 * Draw the label
					 */
					Point labelSize = e.gc.textExtent(label);
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawText(label, (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
				}
			}
		});
		//
		tabItem.setControl(composite);
	}

	private void reloadScorePlotChart() {

		if(scorePlotChart != null) {
			/*
			 * Delete all other series.
			 */
			ISeriesSet seriesSet = scorePlotChart.getSeriesSet();
			ISeries[] series = seriesSet.getSeries();
			for(ISeries serie : series) {
				seriesSet.deleteSeries(serie.getId());
			}
			/*
			 * Data
			 */
			for(ISample sample : samples) {
				if(!sample.getPcaResult().isDisplayed()) {
					continue;
				}
				/*
				 * Create the series.
				 */
				String name = sample.getName();
				ILineSeries scatterSeries = (ILineSeries)scorePlotChart.getSeriesSet().createSeries(SeriesType.LINE, name);
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolSize(SYMBOL_SIZE);
				//
				IPcaResult pcaResult = sample.getPcaResult();
				double[] eigenSpace = pcaResult.getEigenSpace();
				/*
				 * Note.
				 * The spinners are 1 based.
				 * The index is zero based.
				 */
				int pcx = spinnerPCx.getSelection();
				int pcy = spinnerPCy.getSelection();
				scorePlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + pcx);
				scorePlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + pcy);
				double x = eigenSpace[pcx - 1]; // e.g. 0 = PC1
				double y = eigenSpace[pcy - 1]; // e.g. 1 = PC2
				scatterSeries.setXSeries(new double[]{x});
				scatterSeries.setYSeries(new double[]{y});
				/*
				 * Set the color.
				 */
				if(x > 0 && y > 0) {
					scatterSeries.setSymbolType(PlotSymbolType.SQUARE);
				} else if(x > 0 && y < 0) {
					scatterSeries.setSymbolType(PlotSymbolType.TRIANGLE);
				} else if(x < 0 && y > 0) {
					scatterSeries.setSymbolType(PlotSymbolType.DIAMOND);
				} else if(x < 0 && y < 0) {
					scatterSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
				} else {
					scatterSeries.setSymbolType(PlotSymbolType.CIRCLE);
				}
				scatterSeries.setSymbolColor(colors.get(sample.getGroupName()));
			}
			scorePlotChart.getAxisSet().adjustRange();
			scorePlotChart.redraw();
			scorePlotChart.update();
		}
	}

	public void update() {

		Optional<IPcaResults> results = pcaEditor.getPcaResults();
		if(results.isPresent()) {
			samples.clear();
			updateSpinnerPCMaxima(results.get().getNumberOfPrincipleComponents());
			results.get().getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toCollection(() -> samples));
			Set<String> groupNames = PcaUtils.getGroupNames(results.get().getSampleList(), false);
			colors = PcaColorGroup.getColorSWT(groupNames);
		}
		reloadScorePlotChart();
	}

	public void updateSelection() {

		reloadScorePlotChart();
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
	}
}
