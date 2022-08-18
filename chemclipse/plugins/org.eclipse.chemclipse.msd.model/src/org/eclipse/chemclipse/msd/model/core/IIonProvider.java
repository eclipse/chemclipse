/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

public interface IIonProvider {

	/**
	 * Returns the ion list.
	 * 
	 * @return List<Iion> - ion list
	 */
	List<IIon> getIons();

	/**
	 * Returns the number of stored ions.<br/>
	 * If no ions are stored, 0 will be returned.
	 * 
	 * @return int
	 */
	int getNumberOfIons();

	/**
	 * Returns true when there are no ions.
	 */
	boolean isEmpty();
}
