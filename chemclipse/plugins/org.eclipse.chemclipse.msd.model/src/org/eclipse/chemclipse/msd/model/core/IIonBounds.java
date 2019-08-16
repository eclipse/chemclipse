/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

public interface IIonBounds {

	/**
	 * This method returns the ion with the lowest ion value of the
	 * scan you have received this object from.<br/>
	 * Be careful, it is possible that IIon contains null.
	 * 
	 * @return {@link IIon}
	 */
	public IIon getLowestIon();

	/**
	 * This method returns the ion with the highest ion value of the
	 * scan you have received this object from.<br/>
	 * Be careful, it is possible that IIon contains null.
	 * 
	 * @return {@link IIon}
	 */
	public IIon getHighestIon();
}
