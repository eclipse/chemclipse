/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.io.Serializable;

public interface IIonTransition extends Serializable {

	double getFilter1FirstIon();

	double getFilter1LastIon();

	int getFilter1Ion();

	double getFilter3FirstIon();

	double getFilter3LastIon();

	int getFilter3Ion();

	double getCollisionEnergy();

	int getTransitionGroup();

	double getFilter1Resolution();

	double getFilter3Resolution();
}