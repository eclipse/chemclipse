/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.LoadingPlotPage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.SeriesConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.swtchart.ISeries;

public class LoadingPlot extends PCA2DPlot {

	final public static int LABELS_DESCRIPTION = 2;
	final public static int LABELS_RETENTION_TIME_MINUTES = 1;
	final private Set<String> actualSelection = new HashSet<>();
	final private Map<String, IRetentionTime> extractedValues = new HashMap<>();
	private int labelsType = LABELS_RETENTION_TIME_MINUTES;
	private LoadingPlotPage loadingPlotPage;
	private IPcaResults pcaResults;
	private int pcX;
	private int pcY;

	public LoadingPlot(Composite parent, LoadingPlotPage loadingPlotPage) {
		super(parent, "Loading Plot");
		this.loadingPlotPage = loadingPlotPage;
	}

	public Set<String> getActualSelection() {

		return actualSelection;
	}

	public Map<String, IRetentionTime> getExtractedValues() {

		return extractedValues;
	}

	public int getLabelsType() {

		return labelsType;
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		super.handleMouseUpEvent(event);
		//
		updateSelection();
	}

	private boolean isPointVisible(Point point, Rectangle plotAreaBounds) {

		if(point.x >= 0 && point.x <= plotAreaBounds.width && point.y >= 0 && point.y <= plotAreaBounds.height) {
			return true;
		} else {
			return false;
		}
	}

	public void setLabelsType(int labelsType) {

		if(labelsType == LABELS_DESCRIPTION || labelsType == LABELS_RETENTION_TIME_MINUTES) {
			this.labelsType = labelsType;
		}
	}

	@Override
	public void update() {

		super.update();
		deleteSeries();
		extractedValues.clear();
		actualSelection.clear();
		if(pcaResults != null) {
			List<IScatterSeriesData> series;
			if(labelsType == LABELS_RETENTION_TIME_MINUTES) {
				series = SeriesConverter.basisVectorsToSeries(pcaResults, pcX, pcY, extractedValues);
			} else {
				series = SeriesConverter.basisVectorsToSeriesDescription(pcaResults, pcX, pcY, extractedValues);
			}
			for(IScatterSeriesData seriesData : series) {
				String id = seriesData.getSeriesData().getId();
				if(loadingPlotPage.getSelectedData().contains(id)) {
					seriesData.getScatterSeriesSettings().setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
			}
			addSeriesData(series);
		}
	}

	@Override
	public void update(int pcX, int pcY) {

		if(pcaResults != null) {
			this.pcX = pcX;
			this.pcY = pcY;
			super.update(pcX, pcY);
		}
	}

	public void update(IPcaResults pcaResults, int pcX, int pcY) {

		if(pcaResults != null) {
			this.pcaResults = pcaResults;
			update(pcX, pcY);
		}
	}

	private void updateSelection() {

		BaseChart baseChart = getBaseChart();
		Rectangle plotAreaBounds = baseChart.getPlotArea().getBounds();
		ISeries[] series = baseChart.getSeriesSet().getSeries();
		//
		actualSelection.clear();
		for(ISeries scatterSeries : series) {
			if(scatterSeries != null) {
				int size = scatterSeries.getXSeries().length;
				String id = scatterSeries.getId();
				for(int i = 0; i < size; i++) {
					Point point = scatterSeries.getPixelCoordinates(i);
					if(isPointVisible(point, plotAreaBounds)) {
						baseChart.selectSeries(id);
						actualSelection.add(id);
					}
				}
			}
		}
		loadingPlotPage.updateSelection();
	}
}
