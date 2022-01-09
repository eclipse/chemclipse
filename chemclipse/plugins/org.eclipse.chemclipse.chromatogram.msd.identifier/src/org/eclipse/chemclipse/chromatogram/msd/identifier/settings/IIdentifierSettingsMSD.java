/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public interface IIdentifierSettingsMSD extends IIdentifierSettings {

	String DEFAULT_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.cosine";

	/**
	 * Returns the id of the MS comparator that shall be used
	 * for the mass spectrum identification.
	 * 
	 * @return String
	 */
	String getMassSpectrumComparatorId();

	/**
	 * Set the id of the mass spectrum comparator. E.g.:
	 * org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.cosine
	 * 
	 * 
	 * @param massSpectrumComparatorId
	 */
	void setMassSpectrumComparatorId(String massSpectrumComparatorId);

	/**
	 * Returns the excludedIons instance.
	 * 
	 * @return {@link IMarkedIons}
	 */
	IMarkedIons getExcludedIons();
}