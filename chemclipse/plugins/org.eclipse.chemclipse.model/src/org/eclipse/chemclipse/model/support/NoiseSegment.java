/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IScan;

/**
 * A segment that seems to only contain noise
 * 
 * @author christoph
 *
 */
public interface NoiseSegment extends IAnalysisSegment {

	double getNoiseFactor();

	/**
	 * 
	 * @return the scan that makes up this noise segment or <code>null</code> if no scan is avaiable
	 */
	default IScan getScan() {

		return null;
	}
}
