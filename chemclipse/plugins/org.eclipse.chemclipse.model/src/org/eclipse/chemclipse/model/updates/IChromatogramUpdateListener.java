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
package org.eclipse.chemclipse.model.updates;

/**
 * All classes which should be informed if values of the chromatogram have been
 * changed must implement this interface.<br/>
 * The controller for instance of the graphical representation of a chromatogram
 * will announce himself to the chromatogram.
 * 
 * @author eselmeister
 * @see AbstractChromatogram
 * @see IUpdater
 */
public interface IChromatogramUpdateListener {

	/**
	 * See IChromatogramSelectionUpdateNotifier for the explanation of forceReload.
	 */
	void update(boolean forceReload);
}
