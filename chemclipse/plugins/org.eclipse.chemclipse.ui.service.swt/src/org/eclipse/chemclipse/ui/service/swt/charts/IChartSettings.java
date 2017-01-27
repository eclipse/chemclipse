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
package org.eclipse.chemclipse.ui.service.swt.charts;

import org.eclipse.swt.graphics.Color;

public interface IChartSettings {

	boolean isVerticalSliderVisible();

	IChartSettings setVerticalSliderVisible(boolean verticalSliderVisible);

	boolean isHorizontalSliderVisible();

	IChartSettings setHorizontalSliderVisible(boolean horizontalSliderVisible);

	int getOrientation();

	/**
	 * SWT.HORIZONTAL or SWT.VERTICAL
	 * See:http://www.swtchart.org/doc/index.html#Chart_Orientation
	 * 
	 * @param orientation
	 */
	IChartSettings setOrientation(int orientation);

	boolean isLegendVisible();

	IChartSettings setLegendVisible(boolean legendVisible);

	boolean isTitleVisible();

	IChartSettings setTitleVisible(boolean titleVisible);

	Color getBackground();

	IChartSettings setBackground(Color background);

	Color getBackgroundInPlotArea();

	IChartSettings setBackgroundInPlotArea(Color backgroundInPlotArea);

	boolean isEnableCompress();

	IChartSettings setEnableCompress(boolean enableCompress);

	boolean isUseZeroY();

	/**
	 * 0 is the lowest y value.
	 * Otherwise, the lowest y values of the series is used.
	 * 
	 * @param useZeroY
	 */
	IChartSettings setUseZeroY(boolean useZeroY);

	boolean isUseZeroX();

	/**
	 * 0 is the lowest x value.
	 * Otherwise, the lowest x values of the series is used.
	 * 
	 * @param useZeroX
	 */
	IChartSettings setUseZeroX(boolean useZeroX);
}