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
package org.eclipse.chemclipse.model.quantitation;

import java.io.Serializable;

import org.eclipse.chemclipse.model.core.IPeak;

public interface IQuantitationPeak extends Serializable {

	double getConcentration();

	void setConcentration(double concentration);

	String getConcentrationUnit();

	void setConcentrationUnit(String concentrationUnit);

	IPeak getReferencePeak();
}
