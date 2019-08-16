/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.wsd.model.core.AbstractScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

public class ScanSignalWSD extends AbstractScanSignalWSD implements IScanSignalWSD {

	public ScanSignalWSD() {
		super();
	}

	public ScanSignalWSD(double wavelength, float abundance) {
		super();
		setWavelength(wavelength);
		setAbundance(abundance);
	}
}
