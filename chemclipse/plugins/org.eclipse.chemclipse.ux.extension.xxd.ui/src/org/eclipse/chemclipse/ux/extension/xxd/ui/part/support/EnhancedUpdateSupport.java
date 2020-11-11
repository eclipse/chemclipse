/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class EnhancedUpdateSupport extends AbstractUpdateSupport implements IEnhancedUpdateSupport {

	private static final Logger logger = Logger.getLogger(EnhancedUpdateSupport.class);
	//
	private DataUpdateSupport dataUpdateSupport;
	private String defaultTopic = "";
	private boolean isVisible = false;

	public EnhancedUpdateSupport(Composite parent, DataUpdateSupport dataUpdateSupport, String defaultTopic, MPart part) {

		super(part);
		/*
		 * Initialize
		 * The AbstractDataUpdateSupport haven't had a chance yet to listen to updates.
		 */
		this.dataUpdateSupport = dataUpdateSupport;
		this.defaultTopic = defaultTopic;
		createControl(parent);
		updateLatestSelection();
		dataUpdateSupport.add(new IDataUpdateListener() {

			@Override
			public void update(String topic, List<Object> objects) {

				try {
					isVisible = doUpdate();
					if(isVisible && parent != null) {
						Display display = parent.getDisplay();
						if(display != null) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {

									updateSelection(objects, topic);
								}
							});
						}
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		});
		isVisible = doUpdate();
	}

	@Focus
	public void setFocus() {

		if(!isVisible) {
			updateLatestSelection();
			isVisible = true;
		}
	}

	@PreDestroy
	protected void preDestroy() {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_PART_CLOSED, getClass().getSimpleName());
		}
	}

	private void updateLatestSelection() {

		updateSelection(dataUpdateSupport.getUpdates(defaultTopic), defaultTopic);
	}
}
