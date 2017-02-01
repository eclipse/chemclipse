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

public class SeriesData implements ISeriesData {

	private double[] xSeries;
	private double[] ySeries;
	private String id;

	@Override
	public double[] getXSeries() {

		return xSeries;
	}

	@Override
	public void setXSeries(double[] xSeries) {

		this.xSeries = xSeries;
	}

	@Override
	public double[] getYSeries() {

		return ySeries;
	}

	@Override
	public void setYSeries(double[] ySeries) {

		this.ySeries = ySeries;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public void setId(String id) {

		this.id = id;
	}
}
