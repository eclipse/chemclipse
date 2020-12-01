/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support connection between list and editor
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakScanListUI;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PeakScanListPart extends AbstractPart<ExtendedPeakScanListUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private boolean linkWithEditor = true;

	@Inject
	public PeakScanListPart(Composite parent) {

		super(parent, TOPIC);
	}

	public static final class LinkWithEditorHandler {

		@Execute
		void execute(MPart part, MDirectToolItem toolItem) {

			Object object = part.getObject();
			if(object instanceof PeakScanListPart) {
				PeakScanListPart listPart = (PeakScanListPart)object;
				listPart.linkWithEditor = toolItem.isSelected();
				listPart.updatePeakSelection(null);
			}
		}
	}

	@Inject
	@Optional
	public void updatePeakSelection(@UIEventTopic(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION) IPeak peak) {

		if(linkWithEditor) {
			getControl().updateSelection();
		}
	}

	@Override
	protected ExtendedPeakScanListUI createControl(Composite parent) {

		return new ExtendedPeakScanListUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isChromatogramUnloadEvent(topic)) {
				getControl().updateChromatogramSelection(null);
				return false;
			} else {
				if(isChromatogramTopic(topic)) {
					Object object = objects.get(0);
					if(object instanceof IChromatogramSelection) {
						IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)object;
						getControl().updateChromatogramSelection(chromatogramSelection);
						return true;
					}
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic) || isChromatogramUnloadEvent(topic) || isChromatogramTopic(topic);
	}

	private boolean isChromatogramUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isChromatogramTopic(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION);
	}
}
