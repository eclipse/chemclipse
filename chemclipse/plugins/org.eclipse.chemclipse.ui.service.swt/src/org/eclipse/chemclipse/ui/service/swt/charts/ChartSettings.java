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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ChartSettings implements IChartSettings {

	private boolean verticalSliderVisible;
	private boolean horizontalSliderVisible;
	/*
	 * Chart
	 */
	private int orientation;
	private boolean legendVisible;
	private boolean titleVisible;
	private Color background;
	private Color backgroundInPlotArea;
	private boolean enableCompress;
	private boolean useZeroY;
	private boolean useZeroX;

	public ChartSettings() {
		//
		verticalSliderVisible = false;
		horizontalSliderVisible = true;
		//
		Display display = Display.getCurrent();
		orientation = SWT.HORIZONTAL;
		legendVisible = false;
		titleVisible = false;
		background = display.getSystemColor(SWT.COLOR_WHITE);
		backgroundInPlotArea = display.getSystemColor(SWT.COLOR_WHITE);
		enableCompress = true;
		useZeroY = true;
		useZeroX = true;
	}

	@Override
	public boolean isVerticalSliderVisible() {

		return verticalSliderVisible;
	}

	@Override
	public IChartSettings setVerticalSliderVisible(boolean verticalSliderVisible) {

		this.verticalSliderVisible = verticalSliderVisible;
		return this;
	}

	@Override
	public boolean isHorizontalSliderVisible() {

		return horizontalSliderVisible;
	}

	@Override
	public IChartSettings setHorizontalSliderVisible(boolean horizontalSliderVisible) {

		this.horizontalSliderVisible = horizontalSliderVisible;
		return this;
	}

	@Override
	public int getOrientation() {

		return orientation;
	}

	/**
	 * SWT.HORIZONTAL or SWT.VERTICAL
	 * See:http://www.swtchart.org/doc/index.html#Chart_Orientation
	 * 
	 * @param orientation
	 */
	@Override
	public IChartSettings setOrientation(int orientation) {

		this.orientation = orientation;
		return this;
	}

	@Override
	public boolean isLegendVisible() {

		return legendVisible;
	}

	@Override
	public IChartSettings setLegendVisible(boolean legendVisible) {

		this.legendVisible = legendVisible;
		return this;
	}

	@Override
	public boolean isTitleVisible() {

		return titleVisible;
	}

	@Override
	public IChartSettings setTitleVisible(boolean titleVisible) {

		this.titleVisible = titleVisible;
		return this;
	}

	@Override
	public Color getBackground() {

		return background;
	}

	@Override
	public IChartSettings setBackground(Color background) {

		this.background = background;
		return this;
	}

	@Override
	public Color getBackgroundInPlotArea() {

		return backgroundInPlotArea;
	}

	@Override
	public IChartSettings setBackgroundInPlotArea(Color backgroundInPlotArea) {

		this.backgroundInPlotArea = backgroundInPlotArea;
		return this;
	}

	@Override
	public boolean isEnableCompress() {

		return enableCompress;
	}

	@Override
	public IChartSettings setEnableCompress(boolean enableCompress) {

		this.enableCompress = enableCompress;
		return this;
	}

	@Override
	public boolean isUseZeroY() {

		return useZeroY;
	}

	@Override
	public IChartSettings setUseZeroY(boolean useZeroY) {

		this.useZeroY = useZeroY;
		return this;
	}

	@Override
	public boolean isUseZeroX() {

		return useZeroX;
	}

	@Override
	public IChartSettings setUseZeroX(boolean useZeroX) {

		this.useZeroX = useZeroX;
		return this;
	}
}
