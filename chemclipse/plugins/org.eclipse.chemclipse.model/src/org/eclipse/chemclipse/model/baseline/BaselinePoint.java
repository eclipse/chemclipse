/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.baseline;

public class BaselinePoint implements IBaselineSegment {

	private int retentionTime;
	private float abundance;

	public BaselinePoint(int retentionTime) {

		this.retentionTime = retentionTime;
	}

	@Override
	public int getStartRetentionTime() {

		return retentionTime;
	}

	@Override
	public float getStartBackgroundAbundance() {

		return retentionTime;
	}

	@Override
	public void setStartBackgroundAbundance(float startBackgroundAbundance) {

		this.abundance = startBackgroundAbundance;
	}

	@Override
	public int getStopRetentionTime() {

		return retentionTime;
	}

	@Override
	public float getStopBackgroundAbundance() {

		return abundance;
	}

	@Override
	public void setStopBackgroundAbundance(float stopBackgroundAbundance) {

		this.abundance = stopBackgroundAbundance;
	}

	@Override
	public float getBackgroundAbundance(int retentionTime) {

		return abundance;
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		this.retentionTime = startRetentionTime;
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		this.retentionTime = stopRetentionTime;
	}
}
