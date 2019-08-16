/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.EnumSet;

import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.AxisConfig;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class ChartConfigSupport implements AxisConfig {

	private ScrollableChart chart;
	private EnumSet<ChartAxis> available;

	public ChartConfigSupport(ScrollableChart chart, EnumSet<ChartAxis> available) {
		this.chart = chart;
		this.available = available;
	}

	@Override
	public void setAxisLabelVisible(ChartAxis axis, boolean visible) {

		if(available.contains(axis)) {
			switch(axis) {
				case PRIMARY_X:
					chart.getChartSettings().getPrimaryAxisSettingsX().setTitleVisible(visible);
					break;
				case PRIMARY_Y:
					chart.getChartSettings().getPrimaryAxisSettingsY().setTitleVisible(visible);
					break;
				case SECONDARY_Y:
					for(ISecondaryAxisSettings chartAxis : chart.getChartSettings().getSecondaryAxisSettingsListY()) {
						chartAxis.setTitleVisible(visible);
					}
					break;
				case SECONDARY_X:
					for(ISecondaryAxisSettings chartAxis : chart.getChartSettings().getSecondaryAxisSettingsListX()) {
						chartAxis.setTitleVisible(visible);
					}
				default:
					break;
			}
			chart.applySettings(chart.getChartSettings());
		}
	}

	@Override
	public void setAxisVisible(ChartAxis axis, boolean visible) {

		if(available.contains(axis)) {
			switch(axis) {
				case PRIMARY_X:
					chart.getChartSettings().getPrimaryAxisSettingsX().setVisible(visible);
					break;
				case PRIMARY_Y:
					chart.getChartSettings().getPrimaryAxisSettingsY().setVisible(visible);
					break;
				case SECONDARY_Y:
					for(ISecondaryAxisSettings chartAxis : chart.getChartSettings().getSecondaryAxisSettingsListY()) {
						chartAxis.setVisible(visible);
					}
					break;
				case SECONDARY_X:
					for(ISecondaryAxisSettings chartAxis : chart.getChartSettings().getSecondaryAxisSettingsListX()) {
						chartAxis.setVisible(visible);
					}
					break;
				default:
					break;
			}
			chart.applySettings(chart.getChartSettings());
		}
	}

	@Override
	public boolean hasAxis(ChartAxis axis) {

		return available.contains(axis);
	}
}
