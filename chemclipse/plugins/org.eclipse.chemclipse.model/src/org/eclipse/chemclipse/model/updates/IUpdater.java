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
 * Classes can add themselves to each chromatogram instance as listeners.<br/>
 * They must implement the interface {@link IChromatogramUpdateListener}.<br/>
 * The controller of the graphical representation of the chromatogram, for
 * instance, can add himself to the chromatogram model as a listener.<br/>
 * Each time some values has been changed, e.g. abundance of mass spectrum x,
 * the controller will be informed, that the model has been edited.
 * 
 * @author eselmeister
 * @see IChromatogramUpdateListener
 */
public interface IUpdater {

	/**
	 * Adds a IChromatogramUpdateListener to the chromatogram.<br/>
	 * This could be for example an GUI controller.
	 * 
	 * @param listener
	 */
	void addChromatogramUpdateListener(IChromatogramUpdateListener listener);

	/**
	 * Removes a IChromatogramUpdateListener from the chromatogram.<br/>
	 * This could be for example an GUI controller.
	 * 
	 * @param listener
	 */
	void removeChromatogramUpdateListener(IChromatogramUpdateListener listener);
}
