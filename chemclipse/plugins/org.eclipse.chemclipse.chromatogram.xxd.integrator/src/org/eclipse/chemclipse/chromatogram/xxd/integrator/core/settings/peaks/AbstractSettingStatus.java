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

public abstract class AbstractSettingStatus implements ISettingStatus {

	boolean report = true;
	boolean sumOn = false;

	public AbstractSettingStatus(boolean report, boolean sumOn) {
		this.report = report;
		this.sumOn = sumOn;
	}

	@Override
	public boolean report() {

		return report;
	}

	@Override
	public boolean sumOn() {

		return sumOn;
	}
}
