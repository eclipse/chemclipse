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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesData;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesSettings;
import org.eclipse.eavp.service.swtchart.scattercharts.ScatterSeriesData;
import org.eclipse.swt.graphics.Color;
import org.swtchart.ILineSeries.PlotSymbolType;

public class SeriesConverter {

	private static int SYMBOL_SIZE = 8;

	public static List<IScatterSeriesData> sampleToSeries(IPcaResults pcaResults, int pcX, int pcY) {

		return sampleToSeries(pcaResults.getSampleList(), pcX, pcY);
	}

	public static List<IScatterSeriesData> sampleToSeries(List<ISample> samples, int pcX, int pcY) {

		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<IScatterSeriesData>();
		Set<String> groupNames = PcaUtils.getGroupNames(samples, false);
		Map<String, Color> colors = PcaColorGroup.getColorSWT(groupNames);
		for(ISample sample : samples) {
			if(!sample.isSelected() || !sample.getPcaResult().isDisplayed()) {
				continue;
			}
			/*
			 * Create the series.
			 */
			String name = sample.getName();
			IPcaResult pcaResult = sample.getPcaResult();
			double[] eigenSpace = pcaResult.getEigenSpace();
			double x = eigenSpace[pcX - 1]; // e.g. 0 = PC1
			double y = eigenSpace[pcY - 1]; // e.g. 1 = PC2
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			/*
			 * Set the color.
			 */
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			if(x > 0 && y > 0) {
				scatterSeriesSettings.setSymbolType(PlotSymbolType.SQUARE);
			} else if(x > 0 && y < 0) {
				scatterSeriesSettings.setSymbolType(PlotSymbolType.TRIANGLE);
			} else if(x < 0 && y > 0) {
				scatterSeriesSettings.setSymbolType(PlotSymbolType.DIAMOND);
			} else if(x < 0 && y < 0) {
				scatterSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
			} else {
				scatterSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			}
			scatterSeriesSettings.setSymbolSize(SYMBOL_SIZE);
			scatterSeriesSettings.setSymbolColor(colors.get(sample.getGroupName()));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}
}
