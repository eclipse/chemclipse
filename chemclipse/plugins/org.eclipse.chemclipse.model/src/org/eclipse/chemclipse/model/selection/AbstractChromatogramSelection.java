/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.model.selection;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.numeric.core.Point;

public abstract class AbstractChromatogramSelection<T extends IChromatogramPeak, C extends IChromatogram<T>> implements IChromatogramSelection<T, C> {

	private C chromatogram;
	private int startRetentionTime;
	private int stopRetentionTime;
	private float startAbundance;
	private float stopAbundance;
	/*
	 * UI fields
	 */
	private boolean overlaySelected;
	private boolean lockOffset;
	private Point offset;

	public AbstractChromatogramSelection(C chromatogram) throws ChromatogramIsNullException {
		this(chromatogram, true);
	}

	public AbstractChromatogramSelection(C chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {
		/*
		 * Check
		 */
		if(chromatogram == null) {
			throw new ChromatogramIsNullException("The chromatogram must not be null.");
		}
		this.chromatogram = chromatogram;
		overlaySelected = true;
		lockOffset = false;
		offset = new Point(0.0d, 0.0d);
		/*
		 * If scans of the chromatogram will be deleted, this selection will be
		 * adjusted to valid values.
		 */
		this.chromatogram.addChromatogramUpdateListener(this);
	}

	public void dispose() {

		chromatogram = null;
	}

	@Override
	public C getChromatogram() {

		return chromatogram;
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		setStartRetentionTime(startRetentionTime, true);
	}

	private void setStartRetentionTime(int startRetentionTime, boolean update) {

		if(startRetentionTime > 0 && startRetentionTime <= stopRetentionTime && startRetentionTime >= chromatogram.getStartRetentionTime()) {
			this.startRetentionTime = startRetentionTime;
		} else {
			this.startRetentionTime = chromatogram.getStartRetentionTime();
		}
		/*
		 * Fire update change if neccessary.
		 */
		if(update) {
			fireUpdateChange(false);
		}
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		setStopRetentionTime(stopRetentionTime, true);
	}

	private void setStopRetentionTime(int stopRetentionTime, boolean update) {

		if(stopRetentionTime > 0 && stopRetentionTime >= startRetentionTime && stopRetentionTime <= chromatogram.getStopRetentionTime()) {
			this.stopRetentionTime = stopRetentionTime;
		} else {
			this.stopRetentionTime = chromatogram.getStopRetentionTime();
		}
		/*
		 * Fire update change if neccessary.
		 */
		if(update) {
			fireUpdateChange(false);
		}
	}

	@Override
	public float getStartAbundance() {

		return startAbundance;
	}

	@Override
	public void setStartAbundance(float startAbundance) {

		setStartAbundance(startAbundance, true);
	}

	private void setStartAbundance(float startAbundance, boolean update) {

		if(!Float.isInfinite(startAbundance) && !Float.isNaN(startAbundance)) {
			this.startAbundance = startAbundance;
		} else {
			this.startAbundance = 0.0f;
		}
		/*
		 * Fire update change if neccessary.
		 */
		if(update) {
			fireUpdateChange(false);
		}
	}

	@Override
	public float getStopAbundance() {

		return stopAbundance;
	}

	@Override
	public void setStopAbundance(float stopAbundance) {

		setStopAbundance(stopAbundance, true);
	}

	private void setStopAbundance(float stopAbundance, boolean update) {

		if(!Float.isInfinite(stopAbundance) && !Float.isNaN(stopAbundance)) {
			this.stopAbundance = stopAbundance;
		} else {
			this.stopAbundance = chromatogram.getMaxSignal();
		}
		/*
		 * Fire update change if neccessary.
		 */
		if(update) {
			fireUpdateChange(false);
		}
	}

	@Override
	public void reset(boolean fireUpdate) {

		startRetentionTime = chromatogram.getStartRetentionTime();
		stopRetentionTime = chromatogram.getStopRetentionTime();
		startAbundance = chromatogram.getMinSignal();
		stopAbundance = chromatogram.getMaxSignal();
	}

	@Override
	public void setRanges(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, true);
	}

	@Override
	public void setRangeRetentionTime(int startRetentionTime, int stopRetentionTime) {

		setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, true);
	}

	@Override
	public void setRangeRetentionTime(int startRetentionTime, int stopRetentionTime, boolean validate) {

		setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, validate);
	}

	// TODO JUnit
	@Override
	public void setRanges(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance, boolean validate) {

		/*
		 * Set the values first and check them afterwards. Why? Cause sometimes
		 * the e.g. stop retention time is before the next start retention time.
		 * This would throw an exception. So set the values and then check them.
		 */
		this.startRetentionTime = startRetentionTime;
		this.stopRetentionTime = stopRetentionTime;
		this.startAbundance = startAbundance;
		this.stopAbundance = stopAbundance;
		//
		if(validate) {
			setStartRetentionTime(startRetentionTime, false);
			setStopRetentionTime(stopRetentionTime, false);
			setStartAbundance(startAbundance, false);
			setStopAbundance(stopAbundance, false);
			update(false);
		}
	}

	@Override
	public void update(boolean forceReload) {

		setStopAbundance(stopAbundance, false);
		setStartAbundance(startAbundance, false);
		setStartAbundance(startAbundance, false);
		setStopAbundance(stopAbundance, false);
	}

	@Override
	public boolean isOverlaySelected() {

		return overlaySelected;
	}

	@Override
	public void setOverlaySelected(boolean overlaySelected) {

		this.overlaySelected = overlaySelected;
	}

	@Override
	public boolean isLockOffset() {

		return lockOffset;
	}

	@Override
	public void setLockOffset(boolean lockOffset) {

		this.lockOffset = lockOffset;
	}

	@Override
	public Point getOffset() {

		return offset;
	}

	@Override
	public void resetOffset() {

		offset.setX(0.0d);
		offset.setY(0.0d);
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		AbstractChromatogramSelection other = (AbstractChromatogramSelection)otherObject;
		return getChromatogram() == other.getChromatogram() && getStartRetentionTime() == other.getStartRetentionTime() && getStopRetentionTime() == other.getStopRetentionTime();
	}

	@Override
	public int hashCode() {

		return 7 * getChromatogram().hashCode() + 11 * Integer.valueOf(getStartRetentionTime()).hashCode() + 13 * Integer.valueOf(getStopRetentionTime()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("chromatogram=" + chromatogram);
		builder.append(",");
		builder.append("startRetentionTime=" + startRetentionTime);
		builder.append(",");
		builder.append("stopRetentionTime=" + stopRetentionTime);
		builder.append("]");
		return builder.toString();
	}
	// ------------------------------------equals, hashCode, toString
}
