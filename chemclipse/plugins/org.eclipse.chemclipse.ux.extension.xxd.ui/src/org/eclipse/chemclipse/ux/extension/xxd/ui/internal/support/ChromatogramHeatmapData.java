/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.nebula.visualization.widgets.datadefinition.IPrimaryArrayWrapper;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

public class ChromatogramHeatmapData {

	private Range axisRangeWidth;
	private Range axisRangeHeight;
	private IPrimaryArrayWrapper arrayWrapper;
	private double minimum;
	private double maximum;
	private int dataWidth;
	private int dataHeight;

	public ChromatogramHeatmapData(IPrimaryArrayWrapper arrayWrapper, Range axisRangeWidth, Range axisRangeHeight, double minimum, double maximum, int dataWidth, int dataHeight) {

		super();
		//
		this.axisRangeWidth = axisRangeWidth;
		this.axisRangeHeight = axisRangeHeight;
		this.arrayWrapper = arrayWrapper;
		this.minimum = minimum;
		this.maximum = maximum;
		this.dataHeight = dataHeight;
		this.dataWidth = dataWidth;
	}

	public Range getAxisRangeWidth() {

		return axisRangeWidth;
	}

	public void setAxisRangeWidth(Range axisRangeWidth) {

		this.axisRangeWidth = axisRangeWidth;
	}

	public Range getAxisRangeHeight() {

		return axisRangeHeight;
	}

	public void setAxisRangeHight(Range axisRangeHeight) {

		this.axisRangeHeight = axisRangeHeight;
	}

	public IPrimaryArrayWrapper getArrayWrapper() {

		return arrayWrapper;
	}

	public void setArrayWrapper(IPrimaryArrayWrapper arrayWrapper) {

		this.arrayWrapper = arrayWrapper;
	}

	public double getMinimum() {

		return minimum;
	}

	public void setMinimum(double minimum) {

		this.minimum = minimum;
	}

	public double getMaximum() {

		return maximum;
	}

	public void setMaximum(double maximum) {

		this.maximum = maximum;
	}

	public void setDataHeight(int dataHeight) {

		this.dataHeight = dataHeight;
	}

	public void setDataWidth(int dataWidth) {

		this.dataWidth = dataWidth;
	}

	public int getDataHeight() {

		return dataHeight;
	}

	public int getDataWidth() {

		return dataWidth;
	}
}
