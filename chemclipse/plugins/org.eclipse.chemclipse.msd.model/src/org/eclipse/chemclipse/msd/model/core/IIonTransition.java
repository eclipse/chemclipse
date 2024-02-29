/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.io.Serializable;

public interface IIonTransition extends Serializable {

	String getCompoundName();

	void setCompoundName(String compoundName);

	double getQ1StartIon();

	double getQ1StopIon();

	int getQ1Ion(); // Q1 - precision 0

	double getDeltaQ1Ion();

	double getQ3StartIon();

	double getQ3StopIon();

	double getQ3Ion(); // Q3 - precision 1

	double getDeltaQ3Ion();

	double getCollisionEnergy();

	int getTransitionGroup();

	double getQ1Resolution();

	double getQ3Resolution();

	int getDwell();

	void setDwell(int dwell);
}