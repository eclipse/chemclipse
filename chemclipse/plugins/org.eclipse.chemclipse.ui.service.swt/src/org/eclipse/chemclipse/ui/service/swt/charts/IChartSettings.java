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

import java.util.List;

import org.eclipse.swt.graphics.Color;

public interface IChartSettings {

	boolean isVerticalSliderVisible();

	void setVerticalSliderVisible(boolean verticalSliderVisible);

	boolean isHorizontalSliderVisible();

	void setHorizontalSliderVisible(boolean horizontalSliderVisible);

	String getTitle();

	void setTitle(String title);

	boolean isTitleVisible();

	void setTitleVisible(boolean titleVisible);

	Color getTitleColor();

	void setTitleColor(Color titleColor);

	IPrimaryAxisSettings getPrimaryAxisSettingsX();

	IPrimaryAxisSettings getPrimaryAxisSettingsY();

	List<ISecondaryAxisSettings> getSecondaryAxisSettingsListX();

	List<ISecondaryAxisSettings> getSecondaryAxisSettingsListY();

	int getOrientation();

	/**
	 * SWT.HORIZONTAL or SWT.VERTICAL
	 * See:http://www.swtchart.org/doc/index.html#Chart_Orientation
	 * 
	 * @param orientation
	 */
	void setOrientation(int orientation);

	boolean isLegendVisible();

	void setLegendVisible(boolean legendVisible);

	Color getBackground();

	void setBackground(Color background);

	Color getBackgroundInPlotArea();

	void setBackgroundInPlotArea(Color backgroundInPlotArea);

	boolean isEnableCompress();

	void setEnableCompress(boolean enableCompress);

	boolean isUseZeroY();

	/**
	 * 0 is the lowest y value.
	 * Otherwise, the lowest y values of the series is used.
	 * 
	 * @param useZeroY
	 */
	void setUseZeroY(boolean useZeroY);

	boolean isUseZeroX();

	/**
	 * 0 is the lowest x value.
	 * Otherwise, the lowest x values of the series is used.
	 * 
	 * @param useZeroX
	 */
	void setUseZeroX(boolean useZeroX);
}