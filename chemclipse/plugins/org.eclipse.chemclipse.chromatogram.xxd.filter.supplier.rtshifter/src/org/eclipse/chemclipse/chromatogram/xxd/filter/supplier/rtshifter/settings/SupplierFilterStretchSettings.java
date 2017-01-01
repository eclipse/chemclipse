/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

public class SupplierFilterStretchSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterStretchSettings {

	private int scanDelay;
	private int chromatogramLength;

	public SupplierFilterStretchSettings(int chromatogramLength) {
		this.chromatogramLength = chromatogramLength;
	}

	@Override
	public int getScanDelay() {

		return scanDelay;
	}

	@Override
	public void setScanDelay(int scanDelay) {

		this.scanDelay = scanDelay;
	}

	@Override
	public int getChromatogramLength() {

		return chromatogramLength;
	}

	@Override
	public void setChromatogramLength(int chromatogramLength) {

		this.chromatogramLength = chromatogramLength;
	}
}
