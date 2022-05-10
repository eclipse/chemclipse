/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - comment
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

/**
 * The marked signal is a generic construct to
 * match MSD (m/z) and WSD (wavelength) traces.
 */
public interface IMarkedTrace {

	double TOTAL_SIGNAL = ISignal.TOTAL_INTENSITY;
	int TOTAL_SIGNAL_AS_INT = (int)Math.round(TOTAL_SIGNAL);

	int castTrace();

	double getTrace();

	void setTrace(double trace);

	int getMagnification();

	void setMagnification(int magnification);
}