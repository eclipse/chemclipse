/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.charts.bar;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ui.service.swt.charts.AbstractSeriesSettings;
import org.eclipse.swt.graphics.Color;
import org.swtchart.IBarSeries.BarWidthStyle;

public class BarSeriesSettings extends AbstractSeriesSettings implements IBarSeriesSettings {

	private Color barColor;
	private int barPadding;
	private int barWidth;
	private BarWidthStyle barWidthStyle;

	public BarSeriesSettings() {
		barColor = Colors.RED;
		barPadding = 20;
		barWidth = 1;
		barWidthStyle = BarWidthStyle.FIXED;
	}

	@Override
	public Color getBarColor() {

		return barColor;
	}

	@Override
	public void setBarColor(Color barColor) {

		this.barColor = barColor;
	}

	@Override
	public int getBarPadding() {

		return barPadding;
	}

	@Override
	public void setBarPadding(int barPadding) {

		this.barPadding = barPadding;
	}

	@Override
	public int getBarWidth() {

		return barWidth;
	}

	@Override
	public void setBarWidth(int barWidth) {

		this.barWidth = barWidth;
	}

	@Override
	public BarWidthStyle getBarWidthStyle() {

		return barWidthStyle;
	}

	@Override
	public void setBarWidthStyle(BarWidthStyle barWidthStyle) {

		this.barWidthStyle = barWidthStyle;
	}
}