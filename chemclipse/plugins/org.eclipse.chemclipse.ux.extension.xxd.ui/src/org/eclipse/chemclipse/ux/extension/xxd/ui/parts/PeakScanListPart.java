/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakScanListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class PeakScanListPart extends AbstractUpdateSupport implements IUpdateSupport {

	private ExtendedPeakScanListUI extendedPeakScanListUI;
	//
	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();

	@Inject
	public PeakScanListPart(Composite parent, MPart part, IEventBroker eventBroker) {
		super(part);
		extendedPeakScanListUI = new ExtendedPeakScanListUI(parent, eventBroker, Activator.getDefault().getPreferenceStore());
		/*
		 * Initialize
		 * The AbstractDataUpdateSupport haven't had a chance yet to listen to updates.
		 */
		updateLatestSelection();
		dataUpdateSupport.add(new IDataUpdateListener() {

			@Override
			public void update(String topic, List<Object> objects) {

				if(doUpdate()) {
					parent.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							updateSelection(objects, topic);
						}
					});
				}
			}
		});
	}

	@Focus
	public void setFocus() {

		updateLatestSelection();
	}

	private void updateLatestSelection() {

		updateSelection(dataUpdateSupport.getUpdates(TOPIC), TOPIC);
	}

	@SuppressWarnings("rawtypes")
	private void updateSelection(List<Object> objects, String topic) {

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
}
