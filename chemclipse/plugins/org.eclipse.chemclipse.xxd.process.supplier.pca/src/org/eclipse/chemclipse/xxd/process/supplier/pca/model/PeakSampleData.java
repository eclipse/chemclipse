/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - adjustment type
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.Optional;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.SampleData;

public class PeakSampleData extends SampleData<IPeak> implements ISampleData<IPeak> {

	private Optional<IPeak> peak;

	public PeakSampleData() {

		super();
		peak = Optional.empty();
	}

	public PeakSampleData(double data, IPeak data2) {

		super(data, data2);
		peak = Optional.empty();
	}

	public Optional<IPeak> getPeak() {

		return peak;
	}

	public void setPeak(IPeak peak) {

		this.peak = Optional.of(peak);
	}
}
