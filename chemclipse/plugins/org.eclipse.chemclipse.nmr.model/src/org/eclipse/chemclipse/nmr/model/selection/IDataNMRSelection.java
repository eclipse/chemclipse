/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - complete redesign
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.selection;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurement;

public interface IDataNMRSelection {

	/**
	 * 
	 * @return the current active {@link IMeasurement}
	 */
	IComplexSignalMeasurement<?> getMeasurement();

	/**
	 * 
	 * @param type
	 *            the desired type
	 * @return the given type of measurement or <code>null</code> if such a type is not available
	 */
	<T extends IComplexSignalMeasurement<?>> T getMeasurement(Class<T> type);
}
