/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedChromatogramIndicesUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramIndicesPart extends AbstractPart<ExtendedChromatogramIndicesUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;

	@Inject
	public ChromatogramIndicesPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedChromatogramIndicesUI createControl(Composite parent) {

		return new ExtendedChromatogramIndicesUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			IChromatogramSelection<?, ?> chromatogramSelection = null;
			if(isUpdateEvent(topic)) {
				if(objects.get(0) instanceof IChromatogramSelection<?, ?> selection) {
					chromatogramSelection = selection;
				}
			}
			getControl().setInput(chromatogramSelection);
			return true;
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isUpdateEvent(topic) || isCloseEvent(topic);
	}

	private boolean isUpdateEvent(String topic) {

		return TOPIC.equals(topic);
	}

	private boolean isCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic);
	}
}