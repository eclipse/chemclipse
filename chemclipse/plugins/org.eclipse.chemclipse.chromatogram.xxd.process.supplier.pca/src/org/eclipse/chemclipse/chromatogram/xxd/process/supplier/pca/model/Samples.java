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

import java.util.ArrayList;
import java.util.List;

public class Samples implements ISamples<RetentionTime, Sample> {

	private List<IGroup<Sample>> groupList;
	private List<RetentionTime> retentionTimes;
	private List<Sample> samples;

	public Samples() {
		samples = new ArrayList<>();
		retentionTimes = new ArrayList<>();
		groupList = new ArrayList<>();
	}

	public Samples(List<IDataInputEntry> dataInputEntries) {
		this();
		dataInputEntries.forEach(d -> samples.add(new Sample(d)));
	}

	@Override
	public List<IGroup<Sample>> getGroupList() {

		return groupList;
	}

	@Override
	public List<Sample> getSampleList() {

		return samples;
	}

	@Override
	public List<RetentionTime> getVariables() {

		return retentionTimes;
	}
}
