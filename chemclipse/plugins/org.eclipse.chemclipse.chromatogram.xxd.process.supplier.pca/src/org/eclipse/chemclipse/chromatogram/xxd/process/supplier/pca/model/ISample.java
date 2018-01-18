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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

public interface ISample<D extends ISampleData> {

	String getGroupName();

	String getName();

	Object getObject();

	List<D> getSampleData();

	boolean isSelected();

	void setGroupName(String groupName);
	
	void setObject(Object o);

	void setSelected(boolean selected);
}