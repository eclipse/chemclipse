/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import org.eclipse.swt.graphics.Point;

public class BarSeriesValue {

	private double value;
	private double intensity;
	private Point point;

	public BarSeriesValue(double value, double intensity, Point point) {
		this.value = value;
		this.intensity = intensity;
		this.point = point;
	}

	public double getValue() {

		return value;
	}

	public double getIntensity() {

		return intensity;
	}

	public Point getPoint() {

		return point;
	}
}
