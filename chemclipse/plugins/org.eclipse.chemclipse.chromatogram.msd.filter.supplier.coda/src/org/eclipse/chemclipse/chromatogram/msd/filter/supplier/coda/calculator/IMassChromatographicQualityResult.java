/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public interface IMassChromatographicQualityResult {

	/**
	 * Returns an {@link IMarkedIons} object.<br/>
	 * The object stores, which ion values should not be used.
	 * 
	 * @return {@link ExcludedIons}
	 */
	IMarkedIons getExcludedIons();

	/**
	 * Returns the data reduction value.
	 * 
	 * @return float
	 */
	float getDataReductionValue();
}
