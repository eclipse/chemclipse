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

public class Samples extends AbstractSamples {

	private List<IRetentionTime> extractedRetentionTimes;
	private List<IGroup> groups;
	private List<ISample> samples;

	public Samples() {
		samples = new ArrayList<>();
		extractedRetentionTimes = new ArrayList<>();
		groups = new ArrayList<>();
	}

	public Samples(List<IDataInputEntry> dataInputEntries) {
		this();
		dataInputEntries.forEach(d -> samples.add(new Sample(d)));
	}

	@Override
	public List<IRetentionTime> getExtractedRetentionTimes() {

		return extractedRetentionTimes;
	}

	@Override
	public List<IGroup> getGroupList() {

		return groups;
	}

	@Override
	public List<ISample> getSampleList() {

		return samples;
	}
}
