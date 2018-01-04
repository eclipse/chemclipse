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
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIonTransition;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;

public class IonTransition extends AbstractIonTransition implements IIonTransition {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 4950792640210598561L;

	public IonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {
		super(filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
	}

	public IonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {
		super(filter1Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
	}

	public IonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup, String compoundName) {
		super(filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, compoundName);
	}

	public IonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup, String compoundName) {
		super(filter1Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, compoundName);
	}

	public IonTransition(IIonTransition ionTransition, String compoundName) {
		super(ionTransition, compoundName);
	}
}
