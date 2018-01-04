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
package org.eclipse.chemclipse.msd.model.core;

/**
 * An abstract combined mass spectrum represents a mass spectrum which has been
 * calculated by e.g. summing up a range.<br/>
 * It has no distinct retention time, retention index and scan number like
 * (@link: AbstractRegularMassSpectrum).<br/>
 * Instead of the range is marked, e.g. startScan, stopScan.
 * 
 * @author eselmeister
 */
public abstract class AbstractCombinedMassSpectrum extends AbstractScanMSD implements ICombinedMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3237875153822801589L;
	private int startRetentionTime;
	private int stopRetentionTime;
	private float startRetentionIndex;
	private float stopRetentionIndex;
	private int startScan;
	private int stopScan;

	@Override
	public float getStartRetentionIndex() {

		return startRetentionIndex;
	}

	@Override
	public void setStartRetentionIndex(float startRetentionIndex) {

		if(startRetentionIndex >= 0) {
			this.startRetentionIndex = startRetentionIndex;
		}
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		if(startRetentionTime >= 0) {
			this.startRetentionTime = startRetentionTime;
		}
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public void setStartScan(int startScan) {

		if(startScan >= 0) {
			this.startScan = startScan;
		}
	}

	@Override
	public float getStopRetentionIndex() {

		return stopRetentionIndex;
	}

	@Override
	public void setStopRetentionIndex(float stopRetentionIndex) {

		if(stopRetentionIndex >= 0) {
			this.stopRetentionIndex = stopRetentionIndex;
		}
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		if(stopRetentionTime >= 0) {
			this.stopRetentionTime = stopRetentionTime;
		}
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public void setStopScan(int stopScan) {

		if(stopScan >= 0) {
			this.stopScan = stopScan;
		}
	}
	// TODO equals, hashCode, toString
	// -----------------------------equals, hashCode, toString
}
