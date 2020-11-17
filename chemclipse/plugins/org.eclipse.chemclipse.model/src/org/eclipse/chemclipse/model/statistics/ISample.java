/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.List;

public interface ISample {

	String getName();

	void setName(String name);

	String getGroupName();

	void setGroupName(String groupName);

	String getClassification();

	void setClassification(String classification);

	String getDescription();

	void setDescription(String description);

	@SuppressWarnings("rawtypes")
	List<? extends ISampleData> getSampleData();

	boolean isSelected();

	void setSelected(boolean selected);
}