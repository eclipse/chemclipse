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

/**
 * This interface ensures that it is possible to make a deep copy of a mass
 * spectrum.<br/>
 * It provides some additional methods especially one to return an instance of
 * IMassSpectrum.<br/>
 * Clone and this interface methods perform a "deep copy" instead of a
 * "shallow copy" operation.
 * 
 * @author eselmeister
 */
public interface IMassSpectrumCloneable extends Cloneable {

	/**
	 * Returns a deep copy of the actual mass spectrum.<br/>
	 * This method performs a "deep copy" of this object instead of a
	 * "shallow copy" operation.
	 * 
	 * @throws CloneNotSupportedException
	 * @return {@link IScanMSD}
	 */
	IScanMSD makeDeepCopy() throws CloneNotSupportedException;
}
