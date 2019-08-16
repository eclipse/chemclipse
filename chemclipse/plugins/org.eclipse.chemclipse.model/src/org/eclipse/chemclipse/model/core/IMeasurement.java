/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
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

import java.io.Serializable;

import org.eclipse.core.runtime.IAdaptable;

public interface IMeasurement extends IMeasurementInfo, IMeasurementResultSupport, Serializable, IAdaptable {

	/**
	 * Measurements might adapt to different other specialized objects
	 */
	@Override
	default <T> T getAdapter(Class<T> adapter) {

		return null;
	}
}
