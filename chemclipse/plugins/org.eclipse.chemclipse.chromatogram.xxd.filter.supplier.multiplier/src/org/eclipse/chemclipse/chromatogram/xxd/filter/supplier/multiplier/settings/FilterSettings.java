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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

public class FilterSettings extends AbstractChromatogramFilterSettings implements IFilterSettings {

	private float multiplier;

	@Override
	public float getMultiplier() {

		return multiplier;
	}

	@Override
	public void setMultiplier(float multiplier) {

		this.multiplier = multiplier;
	}
}
