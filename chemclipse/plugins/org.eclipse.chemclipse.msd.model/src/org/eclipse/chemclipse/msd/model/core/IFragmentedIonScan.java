/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 * Christoph Läubrich - add precursor type
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * 
 * A {@link IScanMSD scan} representing a fragmented ion spectrum.
 * 
 * @author eselmeister
 * @author <a href="mailto:alexander.kerner@openchrom.net">Alexander
 *         Kerner</a>
 */
public interface IFragmentedIonScan extends IScanMSD {

	/**
	 * Returns the precursor ion.
	 * If none has been selected, 0 will be returned.
	 * 
	 * @return double
	 */
	double getPrecursorIon();

	/**
	 * Sets the precursor ion.
	 * 
	 * @param precursorIon
	 * @return {@code this}
	 */
	IFragmentedIonScan setPrecursorIon(double precursorIon);

	default String getPrecursorType() {

		return null;
	}
}
