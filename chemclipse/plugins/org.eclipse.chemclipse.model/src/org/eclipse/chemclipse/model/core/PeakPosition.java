/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

/**
 * Interface of Objects that can describe the position of a single peak, the position is always related to some kind of indexed (e.g. sampled) data
 * 
 * @author Christoph Läubrich
 *
 */
public interface PeakPosition {

	/**
	 * 
	 * @return the index of the peak maximum (zero based) or -1 if the index is unknown
	 */
	int getPeakMaximum();

	/**
	 * 
	 * @return the index of the peak start (zero based) or -1 if the index is unknown
	 */
	int getPeakStart();

	/**
	 * 
	 * @return the index of the peak end (zero based) or -1 if the index is unknown
	 */
	int getPeakEnd();

	/**
	 * 
	 * @return the type of the peak or {@link PeakType#DEFAULT} if unknown
	 */
	PeakType getPeakType();
}
