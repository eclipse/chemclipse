/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;

public class ChartSupport {

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public void setAxisSettings(IAxisSettings axisSettings, String positionNode, String pattern, String colorNode, String gridLineStyleNode, String gridColorNode) {

		Position position = Position.valueOf(preferenceStore.getString(positionNode));
		Color color = Colors.getColor(preferenceStore.getString(colorNode));
		LineStyle gridLineStyle = LineStyle.valueOf(preferenceStore.getString(gridLineStyleNode));
		Color gridColor = Colors.getColor(preferenceStore.getString(gridColorNode));
		setAxisSettings(axisSettings, position, pattern, color, gridLineStyle, gridColor);
	}

	public ISecondaryAxisSettings getSecondaryAxisSettingsX(String title, IChartSettings chartSettings) {

		return getSecondaryAxisSettings(chartSettings.getSecondaryAxisSettingsListX(), title);
	}

	public ISecondaryAxisSettings getSecondaryAxisSettingsY(String title, IChartSettings chartSettings) {

		return getSecondaryAxisSettings(chartSettings.getSecondaryAxisSettingsListY(), title);
	}

	public ISecondaryAxisSettings getSecondaryAxisSettings(List<ISecondaryAxisSettings> secondaryAxisSettingsList, String title) {

		ISecondaryAxisSettings secondaryAxisSettings = null;
		//
		for(ISecondaryAxisSettings secondaryAxisSetting : secondaryAxisSettingsList) {
			if(title.equals(secondaryAxisSetting.getTitle())) {
				secondaryAxisSettings = secondaryAxisSetting;
			}
		}
		//
		return secondaryAxisSettings;
	}

	public boolean getBoolean(String preferenceName) {

		return preferenceStore.getBoolean(preferenceName);
	}

	public void setAxisSettings(IAxisSettings axisSettings, Position position, String decimalPattern, Color color, LineStyle gridLineStyle, Color gridColor) {

		if(axisSettings != null) {
			axisSettings.setPosition(position);
			axisSettings.setDecimalFormat(ValueFormat.getDecimalFormatEnglish(decimalPattern));
			axisSettings.setColor(color);
			axisSettings.setGridColor(gridColor);
			axisSettings.setGridLineStyle(gridLineStyle);
		}
	}
}
