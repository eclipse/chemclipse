/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class AbstractSample<D extends ISampleData> implements ISample {

	private String name;
	private String groupName;
	private String classification;
	private String description;
	private List<D> sampleData;
	private boolean selected;

	public AbstractSample(String name) {

		this.name = name;
		sampleData = new ArrayList<>();
		selected = true;
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
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getGroupName() {

		return groupName;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	@Override
	public String getClassification() {

		return classification;
	}

	@Override
	public void setClassification(String classification) {

		this.classification = classification;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public List<D> getSampleData() {

		return sampleData;
	}

	@Override
	public boolean isSelected() {

		return selected;
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected = selected;
	}
}
