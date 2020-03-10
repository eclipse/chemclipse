/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.List;

public interface ISamples<V extends IVariable, S extends ISample> {

	List<S> getSampleList();

	List<V> getVariables();

	/**
	 * return true if there are at least two not empty data
	 * 
	 * @param row
	 *            - row index start from 0
	 * @return
	 */
	default boolean selectVariable(int row) {

		int numEmptyValues = 0;
		for(ISample sample : getSampleList()) {
			if(sample.isSelected()) {
				if(!sample.getSampleData().get(row).isEmpty()) {
					numEmptyValues++;
				}
			}
		}
		//
		if(numEmptyValues <= 1) {
			return false;
		}
		//
		return true;
	}
}
