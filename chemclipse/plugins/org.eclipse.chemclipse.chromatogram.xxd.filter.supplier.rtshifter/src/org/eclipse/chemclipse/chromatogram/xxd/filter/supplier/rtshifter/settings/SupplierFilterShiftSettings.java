/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
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

public class SupplierFilterShiftSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterShiftSettings {

	private int millisecondsToShift;
	private boolean isShiftAllScans;

	public SupplierFilterShiftSettings(int millisecondsToShift, boolean isShiftAllScans) {
		this.millisecondsToShift = millisecondsToShift;
		this.isShiftAllScans = isShiftAllScans;
	}

	@Override
	public int getMillisecondsToShift() {

		return millisecondsToShift;
	}

	@Override
	public boolean isShiftAllScans() {

		return isShiftAllScans;
	}
}
