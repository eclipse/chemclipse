/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.List;

public class Slopes implements ISlopes {

	private List<Float> slopes;
	private List<Integer> retentionTimes;

	public Slopes() {
		slopes = new ArrayList<Float>();
		retentionTimes = new ArrayList<Integer>();
	}

	@Override
	public void setSlopes(List<Float> slopes) {

		this.slopes = slopes;
	}

	@Override
	public void setRetentionTimes(List<Integer> retentionTimes) {

		this.retentionTimes = retentionTimes;
	}

	@Override
	public List<Float> getSlopes() {

		return this.slopes;
	}

	@Override
	public List<Integer> getRetentionTimes() {

		return this.retentionTimes;
	}
}
