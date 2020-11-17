/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedBaselineUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramBaselinePart extends AbstractPart<ExtendedBaselineUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;

	@Inject
	public ChromatogramBaselinePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedBaselineUI createControl(Composite parent) {

		return new ExtendedBaselineUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = null;
			if(!isUnloadEvent(topic)) {
				object = objects.get(0);
				if(object instanceof IChromatogramSelection) {
					getControl().update((IChromatogramSelection<?, ?>)object);
					return true;
				}
			} else {
				getControl().update(null);
				return false;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic) || isUnloadEvent(topic);
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}
}
