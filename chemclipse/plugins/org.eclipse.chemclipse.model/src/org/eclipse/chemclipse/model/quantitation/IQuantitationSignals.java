/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import java.io.Serializable;
import java.util.List;
import java.util.NavigableSet;

public interface IQuantitationSignals extends NavigableSet<IQuantitationSignal>, Serializable {

	/**
	 * Returns the list of selected signals.
	 * 
	 * @return List<Double>
	 */
	List<Double> getSelectedSignals();

	/**
	 * Set isUse = false to all stored signals.
	 */
	void deselectAllSignals();

	/**
	 * Set isUse = true to all stored signals.
	 */
	void selectAllSignals();

	/**
	 * Set isUse = true to the given signal.
	 * 
	 * @param ion
	 */
	void selectSignal(double signal);
}
