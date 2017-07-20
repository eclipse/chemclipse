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

public class Sample implements ISample {

	private String groupName;
	private String name;
	private IPcaResult pcaResult;
	private List<ISampleData> sampleData;
	private boolean selected;

	public Sample(String name) {
		this.name = name;
		this.selected = true;
		this.pcaResult = new PcaResult();
		this.sampleData = new ArrayList<>();
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		ISample other = (ISample)otherObject;
		return name.equals(other.getName());
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
	public IPcaResult getPcaResult() {

		return this.pcaResult;
	}

	@Override
	public List<ISampleData> getSampleData() {

		return sampleData;
	}

	@Override
	public int hashCode() {

		return name.hashCode();
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

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("name=" + name);
		builder.append(",");
		builder.append("selected=" + selected);
		builder.append("]");
		return builder.toString();
	}
}
