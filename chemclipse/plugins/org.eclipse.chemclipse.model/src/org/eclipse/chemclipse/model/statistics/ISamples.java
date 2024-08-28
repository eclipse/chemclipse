/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - optimize valid calculation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.List;

public interface ISamples<V extends IVariable, S extends ISample> {

	List<S> getSamples();

	List<V> getVariables();

	/**
	 * Returns true if at least 2 samples contain
	 * data for the given feature.
	 * 
	 * @param row
	 * @return boolean
	 */
	default boolean containsValidData(int row) {

		int counter = 0;
		for(ISample sample : getSamples()) {
			if(sample.isSelected()) {
				if(!sample.getSampleData().get(row).isEmpty()) {
					counter++;
					if(counter >= 2) {
						return true;
					}
				}
			}
		}
		//
		return false;
	}
}