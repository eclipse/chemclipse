/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

	String getGroupName();

	String getName();

	List<? extends ISampleData> getSampleData();
	// long getSampleDataHasBeenChanged();

	// void setSampleDataHasBeenChanged();
	boolean isSelected();

	void setGroupName(String groupName);

	void setName(String name);

	void setSelected(boolean selected);
}