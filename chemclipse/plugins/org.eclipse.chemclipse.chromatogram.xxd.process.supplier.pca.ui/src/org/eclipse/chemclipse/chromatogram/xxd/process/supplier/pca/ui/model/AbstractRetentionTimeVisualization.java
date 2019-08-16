/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.model.statistics.IRetentionTime;

public class AbstractRetentionTimeVisualization extends AbstractVariableVisualization implements IRetentionTime {

	private IRetentionTime delegator;

	public AbstractRetentionTimeVisualization(IRetentionTime retentionTimeModel) {
		super(retentionTimeModel);
		this.delegator = retentionTimeModel;
	}

	@Override
	public int getRetentionTime() {

		return delegator.getRetentionTime();
	}

	@Override
	public double getRetentionTimeMinutes() {

		return delegator.getRetentionTimeMinutes();
	}

	@Override
	public void setRetentioTime(int retentionTime) {

		delegator.setRetentioTime(retentionTime);
	}
}
