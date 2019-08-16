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

public interface ICompounds {

	/**
	 * Adds a compound.
	 * 
	 * @param compound
	 */
	void add(ICompound compound);

	/**
	 * Removes the compound.
	 * 
	 * @param compound
	 */
	void remove(ICompound compound);

	/**
	 * Get a list of the compounds.
	 * 
	 * @return List<ICompound>
	 */
	List<ICompound> getCompounds();

	/**
	 * Returns the compound at the given index. The index starts at 1. If no
	 * compound is available, null will be returned.
	 * 
	 * @param index
	 * @return ICompound
	 */
	ICompound getCompound(int index);

	/**
	 * Returns the size of the list.
	 * 
	 * @return int
	 */
	int size();
}
