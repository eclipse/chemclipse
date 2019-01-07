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

/**
 * signal, e.g.: 104 for Styrene or 0 for TIC
 * relative response, e.g.: 100 for 100%
 * uncertainty, e.g.: 0.02 for a uncertainty of 0.02%
 *
 */
public interface IQuantitationSignal extends Comparable<IQuantitationSignal> {

	/*
	 * 100 is used for the TIC signal
	 */
	double ABSOLUTE_RELATIVE_RESPONSE = 100.0d;

	double getSignal();

	double getRelativeResponse();

	void setRelativeResponse(double relativeResponse);

	double getUncertainty();

	void setUncertainty(double uncertainty);

	boolean isUse();

	void setUse(boolean use);
}
