/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.NoRetentionIndexAvailableException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexExistsException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexValueException;

public interface IRetentionIndices {

	/**
	 * Returns the actual selected retention index.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getActualRetentionIndex() throws NoRetentionIndexAvailableException;

	/**
	 * Returns the first retention index of the list.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getFirstRetentionIndex() throws NoRetentionIndexAvailableException;

	/**
	 * Returns the last retention index of the list.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getLastRetentionIndex() throws NoRetentionIndexAvailableException;

	/**
	 * Returns the previous retention index. If the actual retention index is
	 * the first one, the last one will be returned.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getPreviousRetentionIndex() throws NoRetentionIndexAvailableException;

	/**
	 * Returns the next retention index. If the last retention index is selected
	 * the first one will be returned.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getNextRetentionIndex() throws NoRetentionIndexAvailableException;

	/**
	 * Returns the previous retention index next to the given retention time.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getPreviousRetentionIndex(int retentionTime) throws NoRetentionIndexAvailableException;

	/**
	 * Returns the next retention index next to the given retention time.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getNextRetentionIndex(int retentionTime) throws NoRetentionIndexAvailableException;

	/**
	 * Returns the previous retention index next to the given retention time.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getPreviousRetentionIndex(float index) throws NoRetentionIndexAvailableException;

	/**
	 * Returns the next retention index next to the given index.
	 * 
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	IRetentionIndex getNextRetentionIndex(float index) throws NoRetentionIndexAvailableException;

	/**
	 * Adds an retention index to the list of retention indices.
	 * 
	 * @param retentionIndex
	 * @throws RetentionIndexExistsException
	 * @throws RetentionIndexValueException
	 */
	void addRetentionIndex(IRetentionIndex retentionIndex) throws RetentionIndexExistsException, RetentionIndexValueException;

	/**
	 * Removes an retention index from the list.
	 * 
	 * @param retentionIndex
	 */
	void removeRetentionIndex(IRetentionIndex retentionIndex);

	/**
	 * Removes an retention index from the list its index.
	 * 
	 * @param index
	 */
	void removeRetentionIndex(float index);

	/**
	 * Removes an retention index from the list by its retention time.
	 * 
	 * @param retentionTime
	 */
	void removeRetentionIndex(int retentionTime);

	/**
	 * Removes an retention index from the list by its name.
	 * 
	 * @param name
	 */
	void removeRetentionIndex(String name);
}
