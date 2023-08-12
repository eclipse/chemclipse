/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.MeasurementResultNotification;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedMeasurementResultUI;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MeasurementResultsPart extends AbstractPart<ExtendedMeasurementResultUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	//
	@Inject
	private MeasurementResultNotification measurementResultNotification;

	@Inject
	public MeasurementResultsPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedMeasurementResultUI createControl(Composite parent) {

		ExtendedMeasurementResultUI extendedMeasurementResultUI = new ExtendedMeasurementResultUI(parent, SWT.NONE);
		ComboViewer comboViewer = extendedMeasurementResultUI.getComboMeasurementResults();
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				ISelection selection = event.getSelection();
				if(selection instanceof IStructuredSelection structuredSelection) {
					Object element = structuredSelection.getFirstElement();
					if(element instanceof IMeasurementResult<?>) {
						measurementResultNotification.select((IMeasurementResult<?>)element);
					} else {
						measurementResultNotification.select(null);
					}
				} else {
					measurementResultNotification.select(null);
				}
			}
		});
		//
		return extendedMeasurementResultUI;
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		Collection<IMeasurementResult<?>> results = Collections.emptyList();
		String infoLabel = "";
		//
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(isUpdateEvent(topic)) {
				if(object instanceof IChromatogramSelection<?, ?> selection) {
					IChromatogram<?> chromatogram = selection.getChromatogram();
					results = new ArrayList<>(chromatogram.getMeasurementResults());
					infoLabel = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
				}
			}
		}
		//
		measurementResultNotification.select(null);
		getControl().setInput(results, infoLabel);
		//
		return true;
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