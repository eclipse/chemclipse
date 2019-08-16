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

import org.eclipse.core.runtime.IAdaptable;

/**
 * An arbitrary signal, that can be displayed in a X/Y plot.
 */
public interface ISignal extends IAdaptable {

	double TOTAL_INTENSITY = 0.0d;
	String TOTAL_INTENSITY_DESCRIPTION = "TIC";

	public double getX();

	public double getY();

	/**
	 * Signals might provide access to to special non standard properties
	 */
	@Override
	default <T> T getAdapter(Class<T> adapter) {

		return null;
	}
}
