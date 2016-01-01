/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

public class AxisTitles implements IAxisTitles {

	private String xAxisTopTitle = ""; // e.g.: milliseconds (ms)
	private String xAxisBottomTitle = ""; // e.g.: minutes (min)
	private String yAxisLeftTitle = ""; // e.g.: abundance
	private String yAxisRightTitle = ""; // e.g.: relative abundance

	public AxisTitles(String xAxisTopTitle, String xAxisBottomTitle, String yAxisLeftTitle, String yAxisRightTitle) {
		this.xAxisTopTitle = xAxisTopTitle;
		this.xAxisBottomTitle = xAxisBottomTitle;
		this.yAxisLeftTitle = yAxisLeftTitle;
		this.yAxisRightTitle = yAxisRightTitle;
	}

	@Override
	public String getXAxisTopTitle() {

		return xAxisTopTitle;
	}

	@Override
	public String getXAxisBottomTitle() {

		return xAxisBottomTitle;
	}

	@Override
	public String getYAxisLeftTitle() {

		return yAxisLeftTitle;
	}

	@Override
	public String getYAxisRightTitle() {

		return yAxisRightTitle;
	}
}
