/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.LibraryField;

public class AbstractMeasurementTarget extends AbstractMeasurement implements ITargetDisplaySettings {

	private static final long serialVersionUID = -8315576867955378766L;
	//
	private boolean showPeakLabels = true;
	private boolean showScanLabels = true;
	private LibraryField libraryField = LibraryField.NAME;
	private int rotation = 90;
	private int collisionDepth = 0;
	private Map<String, Boolean> visibilityMap = new HashMap<>();

	@Override
	public boolean isShowPeakLabels() {

		return showPeakLabels;
	}

	@Override
	public void setShowPeakLabels(boolean showPeakLabels) {

		this.showPeakLabels = showPeakLabels;
	}

	@Override
	public boolean isShowScanLabels() {

		return showScanLabels;
	}

	@Override
	public void setShowScanLabels(boolean showScanLabels) {

		this.showScanLabels = showScanLabels;
	}

	@Override
	public int getRotation() {

		return rotation;
	}

	@Override
	public void setRotation(int rotation) {

		this.rotation = rotation;
	}

	@Override
	public int getCollisionDetectionDepth() {

		return collisionDepth;
	}

	@Override
	public void setCollisionDetectionDepth(int collisionDepth) {

		this.collisionDepth = collisionDepth;
	}

	@Override
	public LibraryField getLibraryField() {

		return libraryField;
	}

	@Override
	public void setLibraryField(LibraryField libraryField) {

		this.libraryField = libraryField;
	}

	@Override
	public boolean isVisible(ITargetReference targetReference) {

		if(targetReference == null) {
			return false;
		}
		//
		return visibilityMap.getOrDefault(targetReference.getID(), false);
	}

	@Override
	public void setVisible(ITargetReference targetReference, boolean visible) {

		visibilityMap.put(targetReference.getID(), visible);
	}

	@Override
	public boolean isMapped(ITargetReference targetReference) {

		if(targetReference == null) {
			return false;
		}
		//
		return visibilityMap.containsKey(targetReference.getID());
	}
}
