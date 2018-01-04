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
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;
import java.util.Set;

public interface IIonTransitionSettings {

	/**
	 * Returns the ion transition. If it doesn't exists, a the element and the group will be created.
	 * Keep in mind that the transition group index is 0 based.
	 * 
	 * @param filter1FirstIon
	 * @param filter1LastIon
	 * @param filter3FirstIon
	 * @param filter3LastIon
	 * @param collisionEnergy
	 * @param filter1Resolution
	 * @param filter3Resolution
	 * @param transitionGroup
	 * @return {@link IIonTransition}
	 */
	IIonTransition getIonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup);

	IIonTransition getIonTransition(String compoundName, double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup);

	/**
	 * Returns the ion transition. If it doesn't exists, a the element and the group will be created.
	 * Keep in mind that the transition group index is 0 based.
	 * 
	 * @param filter1Ion
	 * @param filter3Ion
	 * @param collisionEnergy
	 * @param filter1Resolution
	 * @param filter3Resolution
	 * @param transitionGroup
	 * @return {@link IIonTransition}
	 */
	IIonTransition getIonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup);

	/**
	 * Returns a new ion transition if an ion transition with the given compound name doesn't exist yet.
	 * 
	 * @param ionTransition
	 * @param compoundName
	 * @return {@link IIonTransition}
	 */
	IIonTransition getIonTransition(IIonTransition ionTransition, String compoundName);

	/**
	 * The index is 0 based.
	 * The method may return null.
	 * 
	 * @param index
	 * @return {@link IIonTransitionGroup}
	 */
	IIonTransitionGroup get(int index);

	/**
	 * Do not remove elements from the list.
	 * It will not affect the original list.
	 * 
	 * @return
	 */
	List<IIonTransitionGroup> getIonTransitionGroups();

	/**
	 * Size of transition groups.
	 * 
	 * @return
	 */
	int size();

	/**
	 * Returns a set of all ion transitions.
	 * 
	 * @return Set<IIonTransition>
	 */
	Set<IIonTransition> getIonTransitions();
}
