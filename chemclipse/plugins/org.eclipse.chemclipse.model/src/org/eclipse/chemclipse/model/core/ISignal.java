/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

/**
 * An arbitrary signal, that can be displayed in a X/Y plot.
 *
 * @author Philip Wenig
 *
 */
public interface ISignal {

	/**
	 * Returns the signal's X value
	 * 
	 * @return the signal's X value
	 */
	public double getX();

	/**
	 * Returns the signal's Y value
	 * 
	 * @return the singnal'S Y value
	 */
	public double getY();
}
