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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

public interface ISettingStatus {

	/**
	 * Returns whether the peak should be used in the integration report or not.<br/>
	 * This decision depends on several factors. If for example a minimum peak
	 * area of 10000 was chosen and the actual peak has an area of 9999, it will
	 * not be reported.<br/>
	 * 
	 * @return boolean
	 */
	boolean report();

	/**
	 * Returns whether the area of a peak should be summed.
	 * 
	 * @return boolean
	 */
	boolean sumOn();
}
