/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
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
	//
	private IEventBroker eventBroker = null;
	private EventHandler eventHandler = null;

	@PostConstruct
	private void initialize() {

		parent.setLayout(new GridLayout(1, true));
		//
		Label label = new Label(parent, SWT.NONE);
		String activePerspective = Activator.getDefault().getActivePerspective();
		setPerspectiveLabel(label, activePerspective);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.minimumWidth = 300;
		label.setLayoutData(gridData);
		//
		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					String perspectiveName = (String)event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA);
					setPerspectiveLabel(label, perspectiveName);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, eventHandler);
		}
	}

	private void setPerspectiveLabel(Label label, String perspectiveName) {

		perspectiveName = perspectiveName.replaceAll("<", "");
		perspectiveName = perspectiveName.replaceAll(">", "");
		label.setText("Perspective: " + perspectiveName);
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
