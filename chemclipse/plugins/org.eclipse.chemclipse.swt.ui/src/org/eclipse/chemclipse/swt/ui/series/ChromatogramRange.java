/*******************************************************************************
 * Copyright (c) 2014, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

public class ChromatogramRange implements IChromatogramRange {

	private int startRetentionTime;
	private int stopRetentionTime;
	private float startAbundance;
	private float stopAbundance;

	public ChromatogramRange() {
		reset();
	}

	@Override
	public void reset() {

		startRetentionTime = 0;
		stopRetentionTime = 0;
		startAbundance = 0.0f;
		stopAbundance = 0.0f;
	}

	@Override
	public boolean isValid() {

		if(startRetentionTime < stopRetentionTime && startAbundance < stopAbundance) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		this.startRetentionTime = startRetentionTime;
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		this.stopRetentionTime = stopRetentionTime;
	}

	@Override
	public float getStartAbundance() {

		return startAbundance;
	}

	@Override
	public void setStartAbundance(float startAbundance) {

		this.startAbundance = startAbundance;
	}

	@Override
	public float getStopAbundance() {

		return stopAbundance;
	}

	@Override
	public void setStopAbundance(float stopAbundance) {

		this.stopAbundance = stopAbundance;
	}
}
