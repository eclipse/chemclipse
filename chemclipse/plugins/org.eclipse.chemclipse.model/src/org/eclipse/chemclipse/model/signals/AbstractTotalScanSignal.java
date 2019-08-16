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
package org.eclipse.chemclipse.model.signals;

public abstract class AbstractTotalScanSignal implements ITotalScanSignal {

	private int retentionTime = 0; // in milliseconds
	private float retentionIndex = 0.0f;
	private float totalSignal = 0.0f;

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		this.retentionTime = retentionTime;
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		this.retentionIndex = retentionIndex;
	}

	@Override
	public float getTotalSignal() {

		return totalSignal;
	}

	@Override
	public void setTotalSignal(float totalSignal) {

		setTotalSignal(totalSignal, false);
	}

	@Override
	public void setTotalSignal(float totalSignal, boolean validatePositive) {

		if(validatePositive) {
			if(totalSignal >= 0.0f) {
				this.totalSignal = totalSignal;
			}
		} else {
			this.totalSignal = totalSignal;
		}
	}

	// ------------------------------------hashCode, toString, equals
	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(this.getClass() != otherObject.getClass()) {
			return false;
		}
		ITotalScanSignal totalIonSignal = (ITotalScanSignal)otherObject;
		return getRetentionTime() == totalIonSignal.getRetentionTime() && getRetentionIndex() == totalIonSignal.getRetentionIndex() && getTotalSignal() == totalIonSignal.getTotalSignal();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(getRetentionTime()).hashCode() + 9 * Float.valueOf(getRetentionIndex()).hashCode() + 11 * Float.valueOf(getTotalSignal()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("retentionTime=" + getRetentionTime());
		builder.append(",");
		builder.append("retentionIndex=" + getRetentionIndex());
		builder.append(",");
		builder.append("totalSignal=" + getTotalSignal());
		builder.append("]");
		return builder.toString();
	}
	// ------------------------------------hashCode, toString, equals
}
