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
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support;

public class BaselineElement implements IBaselineElement {

	private int retentionTime;
	private float backgroundAbundance;

	public BaselineElement(int retentionTime, float backgroundAbundance) {
		this.retentionTime = retentionTime;
		this.backgroundAbundance = backgroundAbundance;
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public float getBackgroundAbundance() {

		return backgroundAbundance;
	}
}
