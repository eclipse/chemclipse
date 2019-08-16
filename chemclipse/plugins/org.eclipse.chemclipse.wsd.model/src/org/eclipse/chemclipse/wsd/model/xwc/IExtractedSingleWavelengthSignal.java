/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;

public interface IExtractedSingleWavelengthSignal extends ITotalScanSignal {

	/**
	 * 
	 * @return signal wavelength
	 */
	double getWavelength();

	@Override
	IExtractedSingleWavelengthSignal makeDeepCopy();
}
