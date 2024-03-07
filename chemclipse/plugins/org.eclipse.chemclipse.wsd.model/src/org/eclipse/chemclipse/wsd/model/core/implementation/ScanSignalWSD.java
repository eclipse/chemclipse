/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
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

	private static final long serialVersionUID = 7188703805929591517L;

	public ScanSignalWSD() {

		super();
	}

	public ScanSignalWSD(float wavelength, float abundance) {

		super();
		setWavelength(wavelength);
		setAbsorbance(abundance);
	}
}
