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
package org.eclipse.chemclipse.model.core;

/**
 * Extends the {@link ISignal} interface by means of providing access to the complex part of a signal
 * 
 * @author Christoph Läubrich
 *
 */
public interface IComplexSignal extends ISignal {

	/**
	 * 
	 * @return the imaginary part of the x-component
	 */
	public double getImaginaryX();

	/**
	 * 
	 * @return the imaginary part of the y-component
	 */
	public double getImaginaryY();
}
