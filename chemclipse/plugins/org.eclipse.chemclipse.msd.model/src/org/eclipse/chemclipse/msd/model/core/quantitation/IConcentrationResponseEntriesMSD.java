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
import java.util.Set;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.equations.QuadraticEquation;

public interface IConcentrationResponseEntriesMSD extends Serializable {

	/**
	 * Adds a concentration response entry.
	 * 
	 * @param concentrationResponseEntry
	 */
	void add(IConcentrationResponseEntry concentrationResponseEntry);

	void addAll(List<IConcentrationResponseEntry> concentrationResponseEntries);

	/**
	 * Removes the concentration response entry.
	 * 
	 * @param concentrationResponseEntry
	 */
	void remove(IConcentrationResponseEntry concentrationResponseEntry);

	/**
	 * Removes all listed entries.
	 * 
	 * @param concentrationResponseEntriesMSD
	 */
	void removeAll(List<IConcentrationResponseEntry> concentrationResponseEntriesMSD);

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
	 * @return IConcentrationResponseEntryMSD
	 */
	IConcentrationResponseEntry get(int index);

	/**
	 * Returns the list of response entries.
	 * 
	 * @return List<IConcentrationResponseEntryMSD>
	 */
	List<IConcentrationResponseEntry> getList();

	/**
	 * Returns the linear equation.
	 * 
	 * @param ion
	 * @param isCrossZero
	 * @return {@link LinearEquation}
	 */
	LinearEquation getLinearEquation(double ion, boolean isCrossZero);

	/**
	 * Returns the quadratic equation.
	 * 
	 * @param ion
	 * @param isCrossZero
	 * @return {@link QuadraticEquation}
	 */
	QuadraticEquation getQuadraticEquation(double ion, boolean isCrossZero);

	/**
	 * Returns the average factor to calculate the unknown concentration
	 * of a known intensity.
	 * 
	 * factor = Concentration Average / Intensity Average
	 * 
	 * Concentration Unknown = factor * Intensity Unknown
	 * 
	 * @param ion
	 * @param isCrossZero
	 * @return double
	 */
	double getAverageFactor(double ion, boolean isCrossZero);

	/**
	 * Returns the max response value of the stored concentration response entries.
	 * Or 0 if none value is stored.
	 * 
	 * @return double
	 */
	double getMaxResponseValue();

	/**
	 * Returns the set of used ions.
	 * 
	 * @return Set<Double>
	 */
	Set<Double> getIonSet();

	/**
	 * Returns the list of concentration response entries,
	 * denoted by the given ion.
	 * 
	 * @param ion
	 * @return
	 */
	List<IConcentrationResponseEntry> getList(double ion);
}
