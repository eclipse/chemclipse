/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.parts;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.ui.swt.EditHistoryListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class EditHistoryPart {

	private EPartService partService;
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandler = null;
	//
	private EditHistoryListUI editHistoryListUI;
	private IEditHistory editHistory;

	@Inject
	public EditHistoryPart(Composite parent, EPartService partService, MPart part, IEventBroker eventBroker) {
		this.partService = partService;
		this.part = part;
		this.eventBroker = eventBroker;
		createControl(parent);
		subscribe();
	}

	private void createControl(Composite parent) {

		parent.setLayout(new FillLayout());
		editHistoryListUI = new EditHistoryListUI(parent, SWT.NONE | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	}

	@Focus
	public void setFocus() {

		editHistoryListUI.getControl().setFocus();
		update();
	}

	public void setInput(IEditHistory editHistory) {

		this.editHistory = editHistory;
		update();
	}

	private void update() {

		if(doUpdate(editHistory)) {
			editHistoryListUI.setInput(editHistory);
		} else {
			editHistoryListUI.setInput(null);
		}
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	private void subscribe() {

		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					editHistory = (IEditHistory)event.getProperty(IChemClipseEvents.PROPERTY_EDIT_HISTORY);
					update();
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_EDIT_HISTORY_UPDATE, eventHandler);
		}
	}

	private void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	private boolean doUpdate(IEditHistory editHistory) {

		if(isPartVisible() && editHistory != null) {
			return true;
		}
		return false;
	}
}
