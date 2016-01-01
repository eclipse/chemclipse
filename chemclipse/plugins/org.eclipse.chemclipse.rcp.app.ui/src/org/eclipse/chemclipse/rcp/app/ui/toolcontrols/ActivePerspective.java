/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.toolcontrols;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ActivePerspective {

	@Inject
	private Composite parent;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private EventHandler eventHandler;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new GridLayout(1, true));
		//
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Active Perspective: " + getActiveInitialPerspective());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.minimumWidth = 400;
		label.setLayoutData(gridData);
		//
		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					String perspectiveName = (String)event.getProperty(IChemClipseEvents.PROPERTY_PERSPECTIVE_NAME);
					label.setText("Active Perspective: " + perspectiveName);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, eventHandler);
		}
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private String getActiveInitialPerspective() {

		List<MPerspectiveStack> perspectiveStacks = modelService.findElements(application, null, MPerspectiveStack.class, null);
		if(perspectiveStacks.size() > 0) {
			MPerspectiveStack perspectiveStack = perspectiveStacks.get(0);
			return perspectiveStack.getSelectedElement().getLabel();
		} else {
			return "n.a.";
		}
	}
}
