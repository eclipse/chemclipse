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
package org.eclipse.chemclipse.model.quantitation;

public class RetentionTimeWindow extends AbstractIdentificationWindow implements IRetentionTimeWindow {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -8489097558854054118L;
	//
	private int retentionTime; // milliseconds
	private boolean useMilliseconds = true; // Otherwise use percentage deviation

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		if(retentionTime >= 0) {
			this.retentionTime = retentionTime;
		}
	}

	@Override
	public boolean isUseMilliseconds() {

		return useMilliseconds;
	}

	@Override
	public void setUseMilliseconds(boolean useMilliseconds) {

		this.useMilliseconds = useMilliseconds;
	}

	// TODO JUnit
	@Override
	public boolean isRetentionTimeInWindow(int retentionTime) {

		int leftBorder;
		int rightBorder;
		//
		if(useMilliseconds) {
			leftBorder = (int)(this.retentionTime - getAllowedNegativeDeviation());
			rightBorder = (int)(this.retentionTime + getAllowedPositiveDeviation());
		} else {
			leftBorder = (int)(this.retentionTime - this.retentionTime * getAllowedNegativeDeviation());
			rightBorder = (int)(this.retentionTime + this.retentionTime * getAllowedPositiveDeviation());
		}
		//
		if(retentionTime >= leftBorder && retentionTime <= rightBorder) {
			return true;
		} else {
			return false;
		}
	}
}
