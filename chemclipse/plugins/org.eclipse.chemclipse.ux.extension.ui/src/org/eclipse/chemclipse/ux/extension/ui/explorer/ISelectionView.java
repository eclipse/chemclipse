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
package org.eclipse.chemclipse.ux.extension.ui.explorer;

public interface ISelectionView {

	/**
	 * Returns whether the part is visible or not.
	 * 
	 * @return boolean
	 */
	boolean isPartVisible();

	/**
	 * Unsubscribes the chromatogram selection updates.
	 */
	void unsubscribe();

	/**
	 * Checks if the part is visible and the document is not null.
	 * 
	 * @param chromatogramSelection
	 */
	boolean doUpdate(Object document);
}
