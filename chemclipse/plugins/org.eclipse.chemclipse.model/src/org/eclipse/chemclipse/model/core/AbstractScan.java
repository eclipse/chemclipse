/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

public abstract class AbstractScan extends AbstractSignal implements IScan {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 642924518234776409L;
	@SuppressWarnings("rawtypes")
	private transient IChromatogram parentChromatogram;
	//
	private int retentionTime = 0;
	private int retentionTimeColumn1 = 0; // GCxGC, LCxLC
	private int retentionTimeColumn2 = 0; // GCxGC, LCxLC
	/*
	 * RRT
	 */
	private int relativeRetentionTime = 0;
	/*
	 * The retention index stores the default value.
	 * The map is loaded lazily. Normally it is not used.
	 */
	private float retentionIndex = 0;
	private Map<RetentionIndexType, Float> additionalRetentionIndices = null;
	//
	private int scanNumber = 0;
	private int timeSegmentId = 1; // Default 1
	private int cycleNumber = 1; // Default 1
	/*
	 * Do not persist (only for internal use)
	 */
	private boolean isDirty = false;
	private String identifier = "";

	public AbstractScan() {
	}

	/**
	 * Creates a new instance of {@code AbstractScan} by creating a
	 * shallow copy of provided {@code templateScan}.
	 * 
	 * @param templateScan
	 *            {@link IScan scan} that is used as a template
	 */
	public AbstractScan(IScan templateScan) {
		this.parentChromatogram = templateScan.getParentChromatogram();
		this.retentionIndex = templateScan.getRetentionIndex();
		this.scanNumber = templateScan.getScanNumber();
		this.timeSegmentId = templateScan.getTimeSegmentId();
		this.cycleNumber = templateScan.getCycleNumber();
		this.identifier = templateScan.getIdentifier();
		this.isDirty = templateScan.isDirty();
	}

	@Override
	public double getX() {

		return retentionTime;
	}

	@Override
	public double getY() {

		return getTotalSignal();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public IChromatogram getParentChromatogram() {

		return parentChromatogram;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setParentChromatogram(IChromatogram parentChromatogram) {

		this.parentChromatogram = parentChromatogram;
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
	public int getRetentionTimeColumn1() {

		return retentionTimeColumn1;
	}

	@Override
	public void setRetentionTimeColumn1(int retentionTimeColumn1) {

		if(retentionTimeColumn1 >= 0) {
			this.retentionTimeColumn1 = retentionTimeColumn1;
			setDirty(true);
		}
	}

	@Override
	public int getRetentionTimeColumn2() {

		return retentionTimeColumn2;
	}

	@Override
	public void setRetentionTimeColumn2(int retentionTimeColumn2) {

		if(retentionTimeColumn2 >= 0) {
			this.retentionTimeColumn2 = retentionTimeColumn2;
			setDirty(true);
		}
	}

	@Override
	public int getRelativeRetentionTime() {

		return relativeRetentionTime;
	}

	@Override
	public void setRelativeRetentionTime(int relativeRetentionTime) {

		if(relativeRetentionTime >= 0) {
			this.relativeRetentionTime = relativeRetentionTime;
			setDirty(true);
		}
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
	public boolean hasAdditionalRetentionIndices() {

		if(additionalRetentionIndices == null) {
			return false;
		} else {
			return (additionalRetentionIndices.size() > 0) ? true : false;
		}
	}

	@Override
	public float getRetentionIndex(RetentionIndexType retentionIndexType) {

		if(additionalRetentionIndices != null && additionalRetentionIndices.containsKey(retentionIndexType)) {
			return additionalRetentionIndices.get(retentionIndexType);
		} else {
			return 0.0f;
		}
	}

	@Override
	public void setRetentionIndex(RetentionIndexType retentionIndexType, float retentionIndex) {

		if(additionalRetentionIndices == null) {
			additionalRetentionIndices = new HashMap<RetentionIndexType, Float>();
		}
		/*
		 * Add the index.
		 */
		additionalRetentionIndices.put(retentionIndexType, retentionIndex);
	}

	@Override
	public Map<RetentionIndexType, Float> getRetentionIndicesTyped() {

		if(additionalRetentionIndices == null) {
			return new HashMap<RetentionIndexType, Float>();
		} else {
			return new HashMap<RetentionIndexType, Float>(additionalRetentionIndices);
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

	@Override
	public void removeAllTargets() {

		System.out.println("AbstractScan - please override removeAllTargets() in specific implementation.");
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object getAdapter(Class adapter) {

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

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
