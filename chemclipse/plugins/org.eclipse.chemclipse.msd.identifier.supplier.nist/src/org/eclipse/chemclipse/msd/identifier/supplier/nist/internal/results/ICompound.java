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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

import java.util.List;

public interface ICompound {

	/**
	 * Returns the identifier
	 * 
	 * @return
	 */
	String getIdentfier();

	/**
	 * Sets the identifier
	 * 
	 * @param identifier
	 */
	void setIdentifier(String identifier);

	/**
	 * Returns the factor.
	 * 
	 * @return String
	 */
	String getCompoundInLibraryFactor();

	/**
	 * Sets the factor.
	 * 
	 * @param compoundInLibraryFactor
	 */
	void setCompoundInLibraryFactor(String compoundInLibraryFactor);

	/**
	 * Adds a hit.
	 * 
	 * @param hit
	 */
	void add(IHit hit);

	/**
	 * Removes a hit.
	 * 
	 * @param hit
	 */
	void remove(IHit hit);

	/**
	 * Returns the hits of the compound.
	 * 
	 * @return
	 */
	List<IHit> getHits();

	/**
	 * Returns the hit at the given index. The index starts at 1. If no hit is
	 * available, null will be returned.
	 * 
	 * @param index
	 * @return IHit
	 */
	IHit getHit(int index);

	/**
	 * Returns the size.
	 * 
	 * @return int
	 */
	int size();
}
