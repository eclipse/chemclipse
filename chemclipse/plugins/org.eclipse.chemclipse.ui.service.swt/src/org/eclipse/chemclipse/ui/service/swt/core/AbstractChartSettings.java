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
package org.eclipse.chemclipse.ui.service.swt.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class AbstractChartSettings {

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

	public AbstractChartSettings() {
		verticalSliderVisible = false;
		horizontalSliderVisible = true;
		//
		Display display = Display.getCurrent();
		orientation = SWT.HORIZONTAL;
		legendVisible = false;
		titleVisible = false;
		background = display.getSystemColor(SWT.COLOR_WHITE);
		backgroundInPlotArea = display.getSystemColor(SWT.COLOR_WHITE);
	}

	public boolean isVerticalSliderVisible() {

		return verticalSliderVisible;
	}

	public void setVerticalSliderVisible(boolean verticalSliderVisible) {

		this.verticalSliderVisible = verticalSliderVisible;
	}

	public boolean isHorizontalSliderVisible() {

		return horizontalSliderVisible;
	}

	public void setHorizontalSliderVisible(boolean horizontalSliderVisible) {

		this.horizontalSliderVisible = horizontalSliderVisible;
	}

	public int getOrientation() {

		return orientation;
	}

	/**
	 * SWT.HORIZONTAL or SWT.VERTICAL
	 * See:http://www.swtchart.org/doc/index.html#Chart_Orientation
	 * 
	 * @param orientation
	 */
	public void setOrientation(int orientation) {

		this.orientation = orientation;
	}

	public boolean isLegendVisible() {

		return legendVisible;
	}

	public void setLegendVisible(boolean legendVisible) {

		this.legendVisible = legendVisible;
	}

	public boolean isTitleVisible() {

		return titleVisible;
	}

	public void setTitleVisible(boolean titleVisible) {

		this.titleVisible = titleVisible;
	}

	public Color getBackground() {

		return background;
	}

	public void setBackground(Color background) {

		this.background = background;
	}

	public Color getBackgroundInPlotArea() {

		return backgroundInPlotArea;
	}

	public void setBackgroundInPlotArea(Color backgroundInPlotArea) {

		this.backgroundInPlotArea = backgroundInPlotArea;
	}
}
