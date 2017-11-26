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
public class Group<S extends ISample<? extends ISampleData>> implements IGroup<S> {

	private String groupName;
	private String name;
	private List<ISampleDataGroup> sampleData;
	private List<S> samples;
	private boolean selected = true;

	public Group(List<S> samplesSomeGroupName) {
		this.samples = samplesSomeGroupName;
		this.name = "Group";
		this.sampleData = new ArrayList<>();
		if(!samplesSomeGroupName.isEmpty()) {
			int countData = samplesSomeGroupName.get(0).getSampleData().size();
			for(int i = 0; i < countData; i++) {
				sampleData.add(new SampleDataGroup<S>(samplesSomeGroupName, i));
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
	public Object getObject() {

		return this;
	}

	@Override
	public List<ISampleDataGroup> getSampleData() {

		return sampleData;
	}

	@Override
	public List<S> getSamples() {

		return samples;
	}

	@Override
	public boolean isSelected() {

		return selected;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected = selected;
	}
}
