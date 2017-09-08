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

/**
 * this class contains means of samples which contain same group name
 *
 * @author Jan Holy
 *
 */
public class Group implements IGroup {

	private String groupName;
	private String name;
	private List<ISampleData> sampleData;
	private List<ISample> samples;

	public Group(List<ISample> samples) {
		this.samples = samples;
		this.name = "Group";
		this.sampleData = new ArrayList<>();
		if(!samples.isEmpty()) {
			int countData = samples.get(0).getSampleData().size();
			for(int i = 0; i < countData; i++) {
				sampleData.add(new SampleDataGroup(samples, i));
			}
		}
	}

	@Override
	public String getGroupName() {

		return groupName;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public List<ISampleData> getSampleData() {

		return sampleData;
	}

	@Override
	public List<ISample> getSamples() {

		return samples;
	}

	@Override
	public boolean isSelected() {

		return !samples.isEmpty();
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	@Override
	public void setSelected(boolean selected) {

	}
}
