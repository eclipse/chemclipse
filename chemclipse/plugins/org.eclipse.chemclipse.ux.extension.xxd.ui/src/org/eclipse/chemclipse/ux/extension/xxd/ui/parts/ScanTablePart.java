/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanTableUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScanTablePart extends AbstractPart<ExtendedScanTableUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public ScanTablePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedScanTableUI createControl(Composite parent) {

		return new ExtendedScanTableUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isLoadEvent(topic)) {
				getControl().setInput(objects.get(0));
				return true;
			} else {
				if(isUnloadEvent(topic)) {
					getControl().setInput(null);
					return false;
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isLoadEvent(topic) || isUnloadEvent(topic);
	}

	private boolean isLoadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION)) {
			return true;
		} else {
			return false;
		}
	}
}
