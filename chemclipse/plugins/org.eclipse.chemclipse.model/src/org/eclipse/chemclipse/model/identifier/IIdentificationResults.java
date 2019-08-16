/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.List;

public interface IIdentificationResults {

	/**
	 * Adds a {@link IIdentificationResult} to the results.
	 * 
	 * @param result
	 */
	void add(IIdentificationResult result);

	/**
	 * Removes a {@link IIdentificationResult} from the results.
	 * 
	 * @param result
	 */
	void remove(IIdentificationResult result);

	/**
	 * Removes all {@link IIdentificationResult} entries.
	 */
	void removeAll();

	/**
	 * Returns the IIdentificationResult instance with the given index.<br/>
	 * If no object is available or the index is out of range, null will be
	 * returned. The index is 0 based.
	 * 
	 * @param index
	 * @return {@link IIdentificationResult}
	 */
	IIdentificationResult getIdentificationResult(int index);

	/**
	 * Returns a list of all stored elements.
	 * 
	 * @return List<IIdentificationResult>
	 */
	List<IIdentificationResult> getIdentificationResults();
}
