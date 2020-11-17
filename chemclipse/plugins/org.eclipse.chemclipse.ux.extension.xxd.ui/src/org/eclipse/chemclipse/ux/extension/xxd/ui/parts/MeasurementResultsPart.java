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
	@Inject
	private MeasurementResultNotification notification;

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
				if(selection instanceof IStructuredSelection) {
					Object element = ((IStructuredSelection)selection).getFirstElement();
					if(element instanceof IMeasurementResult<?>) {
						notification.select((IMeasurementResult<?>)element);
						return;
					}
				}
				notification.select(null);
			}
		});
		//
		return extendedMeasurementResultUI;
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		Collection<IMeasurementResult<?>> results = Collections.emptyList();
		String infoLabel = "";
		if(!isUnloadEvent(topic)) {
			if(objects.size() == 1) {
				Object object = objects.get(0);
				if(object instanceof IChromatogramSelection<?, ?>) {
					IChromatogramSelection<?, ?> selection = (IChromatogramSelection<?, ?>)object;
					IChromatogram<?> chromatogram = selection.getChromatogram();
					results = new ArrayList<>(chromatogram.getMeasurementResults());
					infoLabel = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
				}
			}
		}
		notification.select(null);
		getControl().update(results, infoLabel);
		return true;
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
