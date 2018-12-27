/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.io.Serializable;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;

public interface IQuantitationSignalsMSD extends Serializable {

	/**
	 * Adds the quantitation signal.
	 * 
	 * @param quantitationSignal
	 */
	void add(IQuantitationSignal quantitationSignalMSD);

	void addAll(List<IQuantitationSignal> quantitationSignals);

	/**
	 * Removes the quantitation signal.
	 * 
	 * @param quantitationSignal
	 */
	void remove(IQuantitationSignal quantitationSignalMSD);

	/**
	 * Removes the quantitation signals.
	 * 
	 * @param quantitationSignal
	 */
	void removeAll(List<IQuantitationSignal> quantitationSignalsMSD);

	/**
	 * Returns the list of selected signals.
	 * 
	 * @return List<Double>
	 */
	List<Double> getSelectedIons();

	/**
	 * Clears the list.
	 */
	void clear();

	/**
	 * The size of stored elements.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the response entry at the given position.
	 * The index is 0 based.
	 * 
	 * @param index
	 * @return IQuantitationSignalMSD
	 */
	IQuantitationSignal get(int index);

	/**
	 * Returns the list of all quantitation signals.
	 * 
	 * @return List<IQuantitationSignalMSD>
	 */
	List<IQuantitationSignal> getList();

	/**
	 * Set isUse = false to all stored signals.
	 */
	void deselectAllSignals();

	/**
	 * Set isUse = true to all stored signals.
	 */
	void selectAllSignals();

	/**
	 * Set isUse = true to the given ion.
	 * 
	 * @param ion
	 */
	void selectSignal(double ion);
}
