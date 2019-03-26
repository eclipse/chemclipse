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

import java.util.Collection;

public interface IComplexSignalMeasurement<T extends IComplexSignal> extends IMeasurement {

	/**
	 * 
	 * @return the signals that makes up this {@link IComplexSignalMeasurement}
	 */
	Collection<? extends T> getSignals();
}
