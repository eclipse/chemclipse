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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EnhancedUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakScanListUI;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.swt.widgets.Composite;

public class PeakScanListPart extends EnhancedUpdateSupport implements IUpdateSupport {

	private ExtendedPeakScanListUI extendedPeakScanListUI;
	private boolean linkWithEditor = true;

	@Inject
	public PeakScanListPart(Composite parent, MPart part, IEventBroker eventBroker) {
		super(parent, Activator.getDefault().getDataUpdateSupport(), IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, part);
	}

	@Override
	public void createControl(Composite parent) {

		extendedPeakScanListUI = new ExtendedPeakScanListUI(parent, Activator.getDefault().getEventBroker(), Activator.getDefault().getPreferenceStore());
	}

	@Inject
	@Optional
	public void updatePeakSelection(@UIEventTopic(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION) IPeak peak) {

		if(linkWithEditor) {
			extendedPeakScanListUI.updateSelection();
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(isChromatogramUnloadEvent(topic)) {
				extendedPeakScanListUI.updateChromatogramSelection(null);
			} else {
				if(isChromatogramTopic(topic)) {
					Object object = objects.get(0);
					if(object instanceof IChromatogramSelection) {
						IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
						extendedPeakScanListUI.updateChromatogramSelection(chromatogramSelection);
					}
				}
			}
		}
	}

	private boolean isChromatogramUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isChromatogramTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION)) {
			return true;
		}
		return false;
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
}
