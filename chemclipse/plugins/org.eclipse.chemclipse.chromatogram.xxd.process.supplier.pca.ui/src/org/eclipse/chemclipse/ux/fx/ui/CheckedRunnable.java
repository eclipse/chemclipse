/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

@FunctionalInterface
public interface CheckedRunnable {

	// TODO: move somewhere else
	/**
	 *
	 * @throws Exception
	 */
	void run() throws Exception;
}
