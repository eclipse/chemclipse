/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.jface.viewers.Viewer;

public interface IRecordTableComparator {

	int ASCENDING = 0;
	int DESCENDING = -1;

	void setColumn(int column);

	int compare(Viewer viewer, Object e1, Object e2);

	int getPropertyIndex();

	int getDirection();

	void setPropertyIndex(int propertyIndex);

	void setDirection(int direction);
}