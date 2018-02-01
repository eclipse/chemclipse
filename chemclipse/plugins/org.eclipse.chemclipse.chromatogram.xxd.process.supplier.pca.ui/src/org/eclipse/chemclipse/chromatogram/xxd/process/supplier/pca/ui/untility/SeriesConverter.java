/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesData;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesSettings;
import org.eclipse.eavp.service.swtchart.scattercharts.ScatterSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ILineSeries.PlotSymbolType;

public class SeriesConverter {

	private static int SYMBOL_SIZE_LOADING_PLOT = 4;
	private static int SYMBOL_SIZE_SCORE_PLOT = 8;

	public static List<IScatterSeriesData> basisVectorsToSeries(IPcaResults pcaResults, int pcX, int pcY, Map<String, IVaribleExtracted> extractedValues) {

		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<>();
		List<IVaribleExtracted> variables = pcaResults.getExtractedVariables();
		for(int i = 0; i < variables.size(); i++) {
			String name = variables.get(i).getValue();
			extractedValues.put(name, variables.get(i));
			double x = 0;
			if(pcX != 0) {
				x = pcaResults.getBasisVectors().get(pcX - 1)[i];
			} else {
				x = i;
			}
			double y = pcaResults.getBasisVectors().get(pcY - 1)[i];
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			if(variables.get(i).getVariableOrigin().isSelected()) {
				scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			} else {
				scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			}
			scatterSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			scatterSeriesSettings.setSymbolSize(SYMBOL_SIZE_LOADING_PLOT);
			IScatterSeriesSettings scatterSeriesSettingsHighlight = (IScatterSeriesSettings)scatterSeriesSettings.getSeriesSettingsHighlight();
			scatterSeriesSettingsHighlight.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}

	public static List<IScatterSeriesData> basisVectorsToSeriesDescription(IPcaResults pcaResults, int pcX, int pcY, Map<String, IVaribleExtracted> extractedValues) {

		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<>();
		List<IVaribleExtracted> variables = pcaResults.getExtractedVariables();
		for(int i = 0; i < variables.size(); i++) {
			IVariable retentionTime = variables.get(i);
			String description = retentionTime.getDescription();
			String name = null;
			if(description == null || description.isEmpty()) {
				name = variables.get(i).getValue();
			} else {
				name = description;
			}
			extractedValues.put(name, variables.get(i));
			double x = 0;
			if(pcX != 0) {
				x = pcaResults.getBasisVectors().get(pcX - 1)[i];
			} else {
				x = i;
			}
			double y = pcaResults.getBasisVectors().get(pcY - 1)[i];
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			scatterSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			scatterSeriesSettings.setSymbolSize(SYMBOL_SIZE_LOADING_PLOT);
			IScatterSeriesSettings scatterSeriesSettingsHighlight = (IScatterSeriesSettings)scatterSeriesSettings.getSeriesSettingsHighlight();
			scatterSeriesSettingsHighlight.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}

	public static List<IScatterSeriesData> sampleToSeries(IPcaResults pcaResults, int pcX, int pcY, Map<String, IPcaResult> extractedPcaResults) {

		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<IScatterSeriesData>();
		Set<String> groupNames = PcaUtils.getGroupNames(pcaResults);
		Map<String, Color> colors = PcaColorGroup.getColorSWT(groupNames);
		extractedPcaResults.clear();
		for(int i = 0; i < pcaResults.getPcaResultList().size(); i++) {
			IPcaResult pcaResult = pcaResults.getPcaResultList().get(i);
			/*
			 * Create the series.
			 */
			String name = pcaResult.getName();
			extractedPcaResults.put(name, pcaResult);
			if(!pcaResult.isDisplayed()) {
				continue;
			}
			double[] eigenSpace = pcaResult.getEigenSpace();
			double x = 0;
			if(pcX != 0) {
				x = eigenSpace[pcX - 1]; // e.g. 0 = PC1
			} else {
				x = i;
			}
			double y = eigenSpace[pcY - 1]; // e.g. 1 = PC2
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			/*
			 * Set the color.
			 */
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			scatterSeriesSettings.setSymbolType(PlotSymbolType.SQUARE);
			scatterSeriesSettings.setSymbolSize(SYMBOL_SIZE_SCORE_PLOT);
			Color color = colors.get(pcaResult.getGroupName());
			if(pcaResult.getSample().isSelected()) {
				scatterSeriesSettings.setSymbolColor(color);
			} else {
				scatterSeriesSettings.setSymbolColor(PcaColorGroup.getUnselectedColor(color));
			}
			IScatterSeriesSettings scatterSeriesSettingsHighlight = (IScatterSeriesSettings)scatterSeriesSettings.getSeriesSettingsHighlight();
			scatterSeriesSettingsHighlight.setSymbolColor(PcaColorGroup.getActualSelectedColor(color));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}
}
