/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.core;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.IScan;

public interface IScanISD extends IScan {

	SignalType getSignalType();

	TreeSet<ISignalVS> getProcessedSignals();

	double getRotationAngle();

	void setRotationAngle(double rotationAngle);

	double[] getRawSignals();

	void setRawSignals(double[] rawSignals);

	double[] getBackgroundSignals();

	void setBackgroundSignals(double[] backgroundSignals);

	void removeWavenumbers(Set<Integer> wavenumbers);

	void keepWavenumbers(Set<Integer> wavenumbers);
}
