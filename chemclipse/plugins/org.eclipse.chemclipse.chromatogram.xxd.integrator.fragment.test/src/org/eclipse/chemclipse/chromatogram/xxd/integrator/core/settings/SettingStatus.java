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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AbstractSettingStatus;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;

/**
 * THIS IS A TEST CLASS! DO NOT USE EXCEPT FOR TESTS!
 * 
 * @author eselmeister
 */
public class SettingStatus extends AbstractSettingStatus implements ISettingStatus {

	public SettingStatus(boolean report, boolean sumOn) {
		super(report, sumOn);
	}
}
