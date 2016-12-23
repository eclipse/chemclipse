/*******************************************************************************
 * Copyright (c) 2010, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.Set;

public interface IIdentificationResult {

	/**
	 * Adds a {@link IIdentificationTarget} to the results.
	 * 
	 * @param entry
	 */
	void add(IIdentificationTarget entry);

	/**
	 * Removes a {@link IIdentificationTarget} from the results.
	 * 
	 * @param entry
	 */
	void remove(IIdentificationTarget entry);

	/**
	 * Removes all {@link IIdentificationTarget} entries.
	 */
	void removeAll();

	/**
	 * Returns the best {@link IIdentificationTarget} from the list.
	 * 
	 * @return
	 */
	IIdentificationTarget getBestHit();

	Set<IIdentificationTarget> getIdentificationEntries();
}
