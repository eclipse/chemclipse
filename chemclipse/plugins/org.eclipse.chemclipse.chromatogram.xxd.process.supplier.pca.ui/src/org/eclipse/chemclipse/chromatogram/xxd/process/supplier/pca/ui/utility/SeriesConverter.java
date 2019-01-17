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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.scattercharts.IScatterSeriesData;
import org.eclipse.swtchart.extensions.scattercharts.IScatterSeriesSettings;
import org.eclipse.swtchart.extensions.scattercharts.ScatterSeriesData;

public class SeriesConverter {

	public static List<IScatterSeriesData> basisVectorsToSeries(IPcaResultsVisualization pcaResults, int pcX, int pcY, Map<String, IVariable> extractedValues) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<>();
		List<IVariableVisualization> variables = pcaResults.getExtractedVariables();
		for(int i = 0; i < variables.size(); i++) {
			String name = variables.get(i).getValue();
			extractedValues.put(name, variables.get(i));
			double x = 0;
			if(pcX != 0) {
				x = pcaResults.getLoadingVectors().get(pcX - 1)[i];
			} else {
				x = i;
			}
			double y = pcaResults.getLoadingVectors().get(pcY - 1)[i];
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			if(variables.get(i).isSelected()) {
				scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			} else {
				scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			}
			scatterSeriesSettings.setSymbolType(PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_LOADING_PLOT_2D_SYMBOL_TYPE)));
			scatterSeriesSettings.setSymbolSize(preferenceStore.getInt(PreferenceSupplier.P_LOADING_PLOT_2D_SYMBOL_SIZE));
			IScatterSeriesSettings scatterSeriesSettingsHighlight = (IScatterSeriesSettings)scatterSeriesSettings.getSeriesSettingsHighlight();
			scatterSeriesSettingsHighlight.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}

	public static List<IScatterSeriesData> basisVectorsToSeriesDescription(IPcaResultsVisualization pcaResults, int pcX, int pcY, Map<String, IVariable> extractedValues) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<>();
		List<IVariableVisualization> variables = pcaResults.getExtractedVariables();
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
				x = pcaResults.getLoadingVectors().get(pcX - 1)[i];
			} else {
				x = i;
			}
			double y = pcaResults.getLoadingVectors().get(pcY - 1)[i];
			ISeriesData seriesData = new SeriesData(new double[]{x}, new double[]{y}, name);
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			scatterSeriesSettings.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			scatterSeriesSettings.setSymbolType(PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_LOADING_PLOT_2D_SYMBOL_TYPE)));
			scatterSeriesSettings.setSymbolSize(preferenceStore.getInt(PreferenceSupplier.P_LOADING_PLOT_2D_SYMBOL_SIZE));
			IScatterSeriesSettings scatterSeriesSettingsHighlight = (IScatterSeriesSettings)scatterSeriesSettings.getSeriesSettingsHighlight();
			scatterSeriesSettingsHighlight.setSymbolColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			scatterSeriesDataList.add(scatterSeriesData);
		}
		return scatterSeriesDataList;
	}

	public static List<IScatterSeriesData> sampleToSeries(IPcaResultsVisualization pcaResults, int pcX, int pcY, Map<String, IPcaResult> extractedPcaResults) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<IScatterSeriesData>();
		PcaUtils.getGroupNames(pcaResults);
		extractedPcaResults.clear();
		for(int i = 0; i < pcaResults.getPcaResultList().size(); i++) {
			IPcaResultVisualization pcaResult = pcaResults.getPcaResultList().get(i);
			/*
			 * Create the series.
			 */
			String name = pcaResult.getName();
			extractedPcaResults.put(name, pcaResult);
			if(!pcaResult.isDisplayed()) {
				continue;
			}
			double[] eigenSpace = pcaResult.getScoreVector();
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
			scatterSeriesSettings.setSymbolType(PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_SCORE_PLOT_2D_SYMBOL_TYPE)));
			scatterSeriesSettings.setSymbolSize(preferenceStore.getInt(PreferenceSupplier.P_SCORE_PLOT_2D_SYMBOL_SIZE));
			Color color = PcaColorGroup.getSampleColorSWT(pcaResult);
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
