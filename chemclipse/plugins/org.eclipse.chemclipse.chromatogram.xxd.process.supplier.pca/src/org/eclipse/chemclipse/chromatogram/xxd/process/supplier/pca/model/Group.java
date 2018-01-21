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

import java.util.List;

/**
 * this class contains means of samples which contain same group name
 *
 * @author Jan Holy
 *
 */
public class Group<S extends ISample<? extends ISampleData>> extends AbstractSample<ISampleDataGroup> implements IGroup<S> {

	private List<S> samples;

	public Group(List<S> samplesSomeGroupName) {
		super("Group");
		this.samples = samplesSomeGroupName;
		if(!samplesSomeGroupName.isEmpty()) {
			setGroupName(samplesSomeGroupName.get(0).getGroupName());
			int countData = samplesSomeGroupName.get(0).getSampleData().size();
			for(int i = 0; i < countData; i++) {
				getSampleData().add(new SampleDataGroup<S>(samplesSomeGroupName, i));
			}
		}
	}

	@Override
	public List<S> getSamples() {

		return samples;
	}
}
