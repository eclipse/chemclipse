/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

/**
 * The values of the primary chart axes are returned.
 * X -> BaseChart.ID_PRIMARY_X_AXIS
 * Y -> BaseChart.ID_PRIMARY_Y_AXIS
 */
public class RulerEvent {

	private double startX = 0.0d;
	private double stopX = 0.0d;
	private double startY = 0.0d;
	private double stopY = 0.0d;

	public RulerEvent(double startX, double stopX, double startY, double stopY) {

		this.startX = startX;
		this.stopX = stopX;
		this.startY = startY;
		this.stopY = stopY;
	}

	public double getStartX() {

		return startX;
	}

	public double getStopX() {

		return stopX;
	}

	public double getDeltaX() {

		return stopX - startX;
	}

	public double getStartY() {

		return startY;
	}

	public double getStopY() {

		return stopY;
	}

	public double getDeltaY() {

		return stopY - startY;
	}
}
