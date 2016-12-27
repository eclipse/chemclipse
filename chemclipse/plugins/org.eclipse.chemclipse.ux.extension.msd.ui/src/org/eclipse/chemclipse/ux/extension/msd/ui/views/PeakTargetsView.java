/*******************************************************************************
 * Copyright (c) 2010, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.PeakTargetsContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractTargetsView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class PeakTargetsView extends AbstractTargetsView {

	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandlerChromatogramSelection;
	private EventHandler eventHandlerPeak;
	//
	private IPeakMSD peak;
	private Map<String, Object> map;

	@Inject
	public PeakTargetsView(IEventBroker eventBroker) {
		super(new PeakTargetsContentProvider(), eventBroker);
		this.eventBroker = eventBroker;
		map = new HashMap<String, Object>();
	}

	@PostConstruct
	private void createControl() {

		super.createPartControl(parent);
		subscribe();
		TableViewer tableViewer = getTableViewer();
		tableViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode != 127 && e.stateMask == 0) {
					propagateSelectedTargetAndMassSpectrum();
				}
			}
		});
		//
		tableViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateSelectedTargetAndMassSpectrum();
			}
		});
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		super.setFocus();
		updatePeakTargets(peak, false);
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandlerChromatogramSelection = new EventHandler() {

				public void handleEvent(Event event) {

					IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					if(chromatogramSelection != null) {
						peak = chromatogramSelection.getSelectedPeak();
						updatePeakTargets(peak, forceReload);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandlerChromatogramSelection);
			/*
			 * Receives and handles peak updates.
			 */
			eventHandlerPeak = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					peak = (IPeakMSD)event.getProperty(IChemClipseEvents.PROPERTY_PEAK_MSD);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					updatePeakTargets(peak, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK, eventHandlerPeak);
		}
	}

	/**
	 * Unsubscribes the selection update events.
	 */
	private void unsubscribe() {

		if(eventBroker != null) {
			if(eventHandlerChromatogramSelection != null) {
				eventBroker.unsubscribe(eventHandlerChromatogramSelection);
			}
			if(eventHandlerPeak != null) {
				eventBroker.unsubscribe(eventHandlerPeak);
			}
		}
	}

	private boolean doUpdate(IPeakMSD peak) {

		if(isPartVisible() && peak != null) {
			return true;
		}
		return false;
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	private void updatePeakTargets(IPeakMSD peak, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(peak)) {
			super.update(peak, forceReload);
			/*
			 * Propagate the first selection
			 */
			getTableViewer().getTable().setSelection(0);
			propagateSelectedTargetAndMassSpectrum();
		}
	}

	private void propagateSelectedTargetAndMassSpectrum() {

		Table table = getTableViewer().getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
				map.clear();
				map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, peak.getExtractedMassSpectrum());
				map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, identificationTarget);
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
			}
		}
	}
}
