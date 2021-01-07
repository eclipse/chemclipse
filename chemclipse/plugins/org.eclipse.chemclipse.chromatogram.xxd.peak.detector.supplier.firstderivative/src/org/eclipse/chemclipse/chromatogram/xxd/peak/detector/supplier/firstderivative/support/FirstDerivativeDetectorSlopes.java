/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add constructor
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlopes;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;

public class FirstDerivativeDetectorSlopes extends DetectorSlopes implements IFirstDerivativeDetectorSlopes {

	public FirstDerivativeDetectorSlopes(ITotalScanSignals signals) {

		super(signals);
	}

	public FirstDerivativeDetectorSlopes(Collection<?> signals) {

		super(0, signals.size() - 1);
	}
}
