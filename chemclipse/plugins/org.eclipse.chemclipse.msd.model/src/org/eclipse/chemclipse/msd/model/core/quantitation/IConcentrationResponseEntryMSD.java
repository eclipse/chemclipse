/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

public interface IConcentrationResponseEntryMSD {

	/**
	 * E.g. 104 for styrene.
	 * 
	 * @return double
	 */
	double getIon();

	/**
	 * Concentration for the given response.
	 * 
	 * @return double
	 */
	double getConcentration();

	/**
	 * The response value.
	 * 
	 * @return double
	 */
	double getResponse();
}
