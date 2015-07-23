/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import org.eclipse.core.runtime.Platform;

public abstract class AbstractScan implements IScan {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -7263229576142314628L;
	private transient IChromatogram parentChromatogram;
	private float retentionIndex = 0;
	private int retentionTime = 0;
	private int scanNumber = 0;
	private int timeSegmentId = 1; // Default 1
	private int cycleNumber = 1; // Default 1
	/*
	 * Do not persist (only for internal use)
	 */
	private boolean isDirty = false;
	private String identifier = "";

	@Override
	public IChromatogram getParentChromatogram() {

		return parentChromatogram;
	}

	@Override
	public void setParentChromatogram(IChromatogram parentChromatogram) {

		this.parentChromatogram = parentChromatogram;
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		if(retentionIndex > 0) {
			this.retentionIndex = retentionIndex;
			setDirty(true);
		}
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		if(retentionTime >= 0) {
			this.retentionTime = retentionTime;
			setDirty(true);
		}
	}

	@Override
	public int getScanNumber() {

		return scanNumber;
	}

	@Override
	public void setScanNumber(int scanNumber) {

		if(scanNumber >= 0) {
			this.scanNumber = scanNumber;
			setDirty(true);
		}
	}

	@Override
	public int getTimeSegmentId() {

		return timeSegmentId;
	}

	@Override
	public void setTimeSegmentId(int timeSegmentId) {

		this.timeSegmentId = timeSegmentId;
	}

	@Override
	public int getCycleNumber() {

		return cycleNumber;
	}

	@Override
	public void setCycleNumber(int cycleNumber) {

		this.cycleNumber = cycleNumber;
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	@Override
	public void setDirty(boolean isDirty) {

		this.isDirty = isDirty;
	}

	@Override
	public String getIdentifier() {

		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {

		if(identifier != null) {
			this.identifier = identifier;
		}
	}

	// -----------------------------IAdaptable
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	// -----------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object otherObject) {

		return super.equals(otherObject);
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("retentionIndex=" + retentionIndex);
		builder.append(",");
		builder.append("retentionTime=" + retentionTime);
		builder.append(",");
		builder.append("scanNumber=" + scanNumber);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
