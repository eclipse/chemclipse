/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;

public class ChartGridSupport {

	private Map<IAxisSettings, LineStyle> axisSettingsInitial = new HashMap<>();

	public boolean isGridDisplayed(IChartSettings chartSettings) {

		List<IAxisSettings> axisSettingsList = getAxisSettings(chartSettings);
		for(IAxisSettings axisSettings : axisSettingsList) {
			if(isGridDisplayed(axisSettings)) {
				return true;
			}
		}
		//
		return false;
	}

	public void showGrid(IChartSettings chartSettings, boolean show) {

		List<IAxisSettings> axisSettingsList = getAxisSettings(chartSettings);
		for(IAxisSettings axisSettings : axisSettingsList) {
			showGrid(axisSettings, show);
		}
	}

	private void showGrid(IAxisSettings axisSettings, boolean show) {

		if(axisSettings.isVisible()) {
			if(show) {
				LineStyle lineStyle = axisSettingsInitial.getOrDefault(axisSettings, LineStyle.DOT);
				axisSettings.setGridLineStyle(lineStyle);
			} else {
				persistInitialState(axisSettings);
				axisSettings.setGridLineStyle(LineStyle.NONE);
			}
		}
	}

	private void persistInitialState(IAxisSettings axisSettings) {

		if(!axisSettingsInitial.containsKey(axisSettings)) {
			axisSettingsInitial.put(axisSettings, axisSettings.getGridLineStyle());
		}
	}

	private boolean isGridDisplayed(IAxisSettings axisSettings) {

		return axisSettings.isVisible() && !LineStyle.NONE.equals(axisSettings.getGridLineStyle());
	}

	private List<IAxisSettings> getAxisSettings(IChartSettings chartSettings) {

		List<IAxisSettings> axisSettingsList = new ArrayList<>();
		/*
		 * Primary Axis X/Y
		 */
		axisSettingsList.add(chartSettings.getPrimaryAxisSettingsX());
		axisSettingsList.add(chartSettings.getPrimaryAxisSettingsY());
		/*
		 * Secondary Axes X/Y
		 */
		for(IAxisSettings axisSettings : chartSettings.getSecondaryAxisSettingsListX()) {
			axisSettingsList.add(axisSettings);
		}
		//
		for(IAxisSettings axisSettings : chartSettings.getSecondaryAxisSettingsListY()) {
			axisSettingsList.add(axisSettings);
		}
		//
		return axisSettingsList;
	}
}