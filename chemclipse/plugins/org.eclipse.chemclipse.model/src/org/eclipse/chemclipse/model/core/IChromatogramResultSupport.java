/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collection;

public interface IChromatogramResultSupport {

	/**
	 * An existing result will be replaced if a chromatogram result with the same id
	 * is still stored.
	 * 
	 * @param chromatogramResult
	 */
	void addChromatogramResult(IChromatogramResult chromatogramResult);

	/**
	 * Returns the result stored by the given identifier.
	 * 
	 * @param identifier
	 * @return {@link IChromatogramResult}
	 */
	IChromatogramResult getChromatogramResult(String identifier);

	/**
	 * Delete the chromatogram result stored by the given identifier.
	 * 
	 * @param identifier
	 */
	void deleteChromatogramResult(String identifier);

	/**
	 * Deletes all chromatogram results.
	 */
	void removeAllChromatogramResults();

	/**
	 * Returns a collection of all chromatogram results.
	 * 
	 * @return Collection
	 */
	Collection<IChromatogramResult> getChromatogramResults();
}
