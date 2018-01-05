/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

	private double x;
	private double y;
	private Point point;

	public BarSeriesValue(double x, double y, Point point) {
		this.x = x;
		this.y = y;
		this.point = point;
	}

	public double getX() {

		return x;
	}

	public double getY() {

		return y;
	}

	public Point getPoint() {

		return point;
	}
}
