/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class TargetReferenceTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private ITargetDisplaySettings targetDisplaySettings = null;

	public void setTargetDisplaySettings(ITargetDisplaySettings targetDisplaySettings) {

		this.targetDisplaySettings = targetDisplaySettings;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ITargetReference entry1 && e2 instanceof ITargetReference entry2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					if(targetDisplaySettings != null) {
						sortOrder = Boolean.compare(targetDisplaySettings.isVisible(entry2), targetDisplaySettings.isVisible(entry1));
					}
					break;
				case 1:
					sortOrder = entry2.getID().compareTo(entry1.getID());
					break;
				case 2:
					sortOrder = entry2.getType().compareTo(entry1.getType());
					break;
				case 3:
					if(targetDisplaySettings != null) {
						LibraryField libraryField = targetDisplaySettings.getLibraryField();
						sortOrder = entry2.getTargetLabel(libraryField).compareTo(entry1.getTargetLabel(libraryField));
					}
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
