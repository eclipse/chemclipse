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
package org.eclipse.chemclipse.msd.model.core;

public interface IScanIon extends IIon {

	/**
	 * Returns the minimal possible abundance value.<br/>
	 * It could be that the range of the abundance range differs from
	 * manufacturer to manufacturer.<br/>
	 * Be aware of that!
	 * 
	 * @return float
	 */
	float getMinPossibleAbundanceValue();

	/**
	 * Returns the maximal possible abundance value.<br/>
	 * It could be that the range of the abundance range differs from
	 * manufacturer to manufacturer.<br/>
	 * One manufacturer saves the values in a 2 byte field another in a 4 byte
	 * field with another scope.<br/>
	 * Be aware of that!
	 * 
	 * @return float
	 */
	float getMaxPossibleAbundanceValue();

	/**
	 * Returns the minimal possible ion value.<br/>
	 * It could be that the range of the ion value range differs from
	 * manufacturer to manufacturer.<br/>
	 * Be aware of that!
	 * 
	 * @return float
	 */
	double getMinPossibleIonValue();

	/**
	 * Returns the maximal possible ion value.<br/>
	 * It could be that the range of the ion value range differs from
	 * manufacturer to manufacturer.<br/>
	 * One manufacturer saves the values in a 2 byte field another in a 4 byte
	 * field with another scope.<br/>
	 * Be aware of that!
	 * 
	 * @return float
	 */
	double getMaxPossibleIonValue();

	/**
	 * Returns whether the abundance limit of the supplier will be considered or
	 * not.
	 * 
	 * @return boolean
	 */
	boolean isIgnoreAbundanceLimit();

	/**
	 * Sets if the abundance limit of the supplier should be ignored.<br/>
	 * If ignoreAbundanceLimit is true, it will throw no
	 * AbundanceLimitExceededException even if it is higher than defined by
	 * getMaxPossibleAbundanceValue() in ISupplierIon.<br/>
	 * Be careful with this option, because to high abundance values will be
	 * sized down while writing a binary supplier chromatogram.
	 * 
	 * @param ignoreAbundanceLimit
	 */
	void setIgnoreAbundanceLimit(boolean ignoreAbundanceLimit);
}
