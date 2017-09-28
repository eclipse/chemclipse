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
import org.eclipse.eavp.service.swtchart.core.ICustomSelectionHandler;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.ISeries;

public class LoadingPlot extends PCA2DPlot {

	final public static int LABELS_DESCRIPTION = 2;
	final public static int LABELS_RETENTION_TIME_MINUTES = 1;
	final private Set<String> actualSelection = new HashSet<>();
	final private Map<String, IRetentionTime> extractedValues = new HashMap<>();
	private int labelsType = LABELS_RETENTION_TIME_MINUTES;

	public LoadingPlot(Composite parent, LoadingPlotPage loadingPlotPage) {
		super(parent, "Loading Plot");
		getBaseChart().addCustomRangeSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				updateSelection();
			}
		});
		getBaseChart().addCustomPointSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				BaseChart baseChart = getBaseChart();
				ISeries[] series = baseChart.getSeriesSet().getSeries();
				// loadingPlot
				for(ISeries scatterSeries : series) {
					if(scatterSeries != null) {
						int size = scatterSeries.getXSeries().length;
						String id = scatterSeries.getId();
						for(int i = 0; i < size; i++) {
							Point point = scatterSeries.getPixelCoordinates(i);
							if((point.x - event.x) * (point.x - event.x) + (point.y - event.y) * (point.y - event.y) < 16) {
								if(baseChart.getSelectedSeriesIds().contains(id)) {
									deselect(id);
								} else {
									baseChart.selectSeries(id);
								}
							}
						}
					}
				}
				baseChart.redraw();
			}
		});
	}

	public void deselect(Set<String> set) {

		Set<String> selection = new HashSet<>(getBaseChart().getSelectedSeriesIds());
		for(String id : set) {
			selection.remove(id);
		}
		getBaseChart().resetSeriesSettings();
		for(String id : selection) {
			getBaseChart().selectSeries(id);
		}
		getBaseChart().redraw();
	}

	public void deselect(String... set) {

		Set<String> selection = new HashSet<>(getBaseChart().getSelectedSeriesIds());
		for(String id : set) {
			selection.remove(id);
		}
		getBaseChart().resetSeriesSettings();
		for(String id : selection) {
			getBaseChart().selectSeries(id);
		}
		getBaseChart().redraw();
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

	public void update(IPcaResults pcaResults, int pcX, int pcY) {

		List<IScatterSeriesData> series;
		if(labelsType == LABELS_RETENTION_TIME_MINUTES) {
			series = SeriesConverter.basisVectorsToSeries(pcaResults, pcX, pcY, extractedValues);
		} else {
			series = SeriesConverter.basisVectorsToSeriesDescription(pcaResults, pcX, pcY, extractedValues);
		}
		deleteSeries();
		addSeriesData(series);
		update(pcX, pcY);
		updateSelection();
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
						actualSelection.add(id);
					}
				}
			}
		}
	}
}
