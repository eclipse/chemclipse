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

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedTargetsUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TargetsPart extends AbstractPart {

	// private static final String TOPIC = IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION;
	//
	private ExtendedTargetsUI control;
	//
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
	private IDataUpdateListener updateListener = new IDataUpdateListener() {

		@Override
		public void update(String topic, List<Object> objects) {

			updateSelection(objects, topic);
		}
	};

	@Inject
	public TargetsPart(Composite parent, MPart part) {

		control = new ExtendedTargetsUI(parent, SWT.NONE);
		dataUpdateSupport.add(updateListener);
	}

	@Focus
	public void setFocus() {

		/*
		 * This could lead to unintended updates.
		 */
		// updateSelection(dataUpdateSupport.getUpdates(TOPIC), TOPIC);
	}

	@PreDestroy
	protected void preDestroy() {

		dataUpdateSupport.remove(updateListener);
		super.preDestroy();
	}

	private void updateSelection(List<Object> objects, String topic) {

		if(DataUpdateSupport.isVisible(control)) {
			/*
			 * 0 => because only one property was used to register the event.
			 */
			if(objects.size() == 1) {
				if(isChromatogramUnloadEvent(topic) || isOtherUnloadEvent(topic)) {
					control.update(null);
				} else {
					Object object = objects.get(0);
					if(isChromatogramTopic(topic)) {
						if(object instanceof IChromatogramSelection) {
							IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)object;
							object = chromatogramSelection.getChromatogram();
							control.update(object);
						}
					} else if(isScanOrPeakTopic(topic) || isIdentificationTopic(topic)) {
						control.update(object);
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
		}
		return false;
	}

	private boolean isOtherUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isScanOrPeakTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isIdentificationTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION)) {
			return true;
		}
		return false;
	}
}
