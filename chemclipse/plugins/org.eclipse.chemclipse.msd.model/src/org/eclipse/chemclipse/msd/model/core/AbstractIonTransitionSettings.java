/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.msd.model.implementation.IonTransition;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionGroup;

public abstract class AbstractIonTransitionSettings implements IIonTransitionSettings {

	private List<IIonTransitionGroup> ionTransitionGroups;

	protected AbstractIonTransitionSettings() {

		ionTransitionGroups = new ArrayList<>();
	}

	@Override
	public IIonTransition getIonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		return getIonTransition("", filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
	}

	@Override
	public IIonTransition getIonTransition(String compoundName, double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		IIonTransition ionTransition = new IonTransition(filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, compoundName);
		/*
		 * The index (transition group) must be lower than the size of the
		 * number of transition groups. The index is 0 based.
		 */
		if(size() <= transitionGroup) {
			/*
			 * If the group is null, create a new group and add the transition.
			 */
			IIonTransitionGroup ionTransitionGroup = new IonTransitionGroup();
			ionTransitionGroup.add(ionTransition);
			ionTransitionGroups.add(ionTransitionGroup);
			return ionTransition;
		} else {
			/*
			 * If the group exists (valid index), than check if the transition exists.
			 */
			IIonTransitionGroup ionTransitionGroup = ionTransitionGroups.get(transitionGroup);
			if(ionTransitionGroup.contains(ionTransition)) {
				/*
				 * Return the existing instance of the ion transition object.
				 */
				return ionTransitionGroup.get(ionTransition);
			} else {
				/*
				 * Add the ion transition and return it.
				 */
				ionTransitionGroup.add(ionTransition);
				return ionTransition;
			}
		}
	}

	@Override
	public IIonTransition getIonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		return getIonTransition(filter1Ion, filter1Ion, filter3Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
	}

	@Override
	public IIonTransition getIonTransition(IIonTransition ionTransition, String compoundName) {

		if(ionTransition != null) {
			int transitionGroup = ionTransition.getTransitionGroup();
			IIonTransitionGroup ionTransitionGroup = ionTransitionGroups.get(transitionGroup);
			//
			if(ionTransition.getCompoundName().equals(compoundName) && ionTransitionGroup.contains(ionTransition)) {
				return ionTransition;
			} else {
				IIonTransition ionTransitionWithCompoundName = new IonTransition(ionTransition, compoundName);
				ionTransitionGroup.add(ionTransitionWithCompoundName);
				return ionTransitionWithCompoundName;
			}
		}
		return ionTransition;
	}

	@Override
	public IIonTransitionGroup get(int index) {

		if(ionTransitionGroups.size() > index) {
			return ionTransitionGroups.get(index);
		} else {
			return null;
		}
	}

	@Override
	public List<IIonTransitionGroup> getIonTransitionGroups() {

		return ionTransitionGroups;
	}

	@Override
	public Set<IIonTransition> getIonTransitions() {

		Set<IIonTransition> ionTransitions = new HashSet<>();
		for(IIonTransitionGroup ionTransitionGroup : ionTransitionGroups) {
			ionTransitions.addAll(ionTransitionGroup.getIonTransitions());
		}
		return ionTransitions;
	}

	@Override
	public int size() {

		return ionTransitionGroups.size();
	}
}
