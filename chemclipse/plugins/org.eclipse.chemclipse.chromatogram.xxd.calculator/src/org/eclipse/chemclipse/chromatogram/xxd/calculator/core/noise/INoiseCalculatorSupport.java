/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoNoiseCalculatorAvailableException;

public interface INoiseCalculatorSupport {

	/**
	 * Returns the id of the selected calculator name.<br/>
	 * The id of the selected filter is used to determine which calculator should
	 * be used to set the baseline of the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoNoiseCalculatorAvailableException
	 */
	String getCalculatorId(int index) throws NoNoiseCalculatorAvailableException;

	/**
	 * Returns an INoiseCalculatorSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param detectorId
	 * @return {@link INoiseCalculatorSupplier}
	 * @throws NoNoiseCalculatorAvailableException
	 */
	INoiseCalculatorSupplier getCalculatorSupplier(String detectorId) throws NoNoiseCalculatorAvailableException;

	/**
	 * Returns an ArrayList with all available noise calculator ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoNoiseCalculatorAvailableException
	 */
	List<String> getAvailableCalculatorIds() throws NoNoiseCalculatorAvailableException;

	/**
	 * Returns the list of available noise calculator names.
	 * 
	 * @return String[]
	 * @throws NoNoiseCalculatorAvailableException
	 */
	String[] getCalculatorNames() throws NoNoiseCalculatorAvailableException;

	Collection<INoiseCalculatorSupplier> getCalculatorSupplier();
}
