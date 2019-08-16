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

import org.eclipse.chemclipse.wsd.model.core.AbstractScanWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class ScanWSD extends AbstractScanWSD implements IScanWSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -4018377154187209353L;

	public ScanWSD() {
		super();
	}

	public ScanWSD(IScanWSD scanWSD, float actualPercentageIntensity) throws IllegalArgumentException {
		super(scanWSD, actualPercentageIntensity);
	}
}
