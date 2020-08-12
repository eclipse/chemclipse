/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public interface ITargetDisplaySettings {

	static final TargetExtendedComparator COMPARATOR = new TargetExtendedComparator(SortOrder.DESC);

	boolean isShowPeakLabels();

	boolean isShowScanLabels();

	void setShowPeakLabels(boolean showPeakLabels);

	void setShowScanLabels(boolean showScanLabels);

	int getRotation();

	int getCollisionDetectionDepth();

	void setCollisionDetectionDepth(int depth);

	/**
	 * Sets the rotation angel of the labels in degree
	 * 
	 * @param degree
	 */
	void setRotation(int degree);

	LibraryField getLibraryField();

	void setLibraryField(LibraryField libraryField);

	boolean isVisible(ITargetReference targetReference);

	void setVisible(ITargetReference targetReference, boolean visible);

	boolean isMapped(ITargetReference targetReference);
}
