/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	/**
	 * Returns the normalization base.
	 * 
	 * @return float
	 */
	float getNormalizationBase();

	/**
	 * Sets the normalization base.<br/>
	 * The value must be higher than MIN_NORMALIZATION_BASE.
	 * 
	 * @param normalizationBase
	 */
	void setNormalizationBase(float normalizationBase);
}
