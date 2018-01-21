/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import java.util.Optional;

import org.eclipse.chemclipse.model.core.IPeak;

public class SampleData extends AbstractSampleData implements ISampleData {

	private Optional<IPeak> peak;

	public SampleData() {
		super();
		peak = Optional.empty();
	}

	public SampleData(double data) {
		super(data);
		peak = Optional.empty();
	}

	public Optional<IPeak> getPeak() {

		return peak;
	}

	public void setPeak(IPeak peak) {

		this.peak = Optional.of(peak);
	}
}
