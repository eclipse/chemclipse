/*******************************************************************************
 * Copyright (c) 2017 Jan Holy
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;

public class RetentionTime implements IRetentionTime {

	private String description;
	private boolean isSelected;
	private int retentioTime;

	public RetentionTime(int retentioTime) {
		this.retentioTime = retentioTime;
		isSelected = true;
	}

	public RetentionTime(int retentioTime, String description) {
		this(retentioTime);
		this.description = description;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public int getRetentionTime() {

		return retentioTime;
	}

	@Override
	public double getRetentionTimeMinutes() {

		return retentioTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setSelected(boolean selected) {

		this.isSelected = selected;
	}
}
