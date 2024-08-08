/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.chemclipse.support.ui.editors.SystemEditor;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	private static final Logger logger = Logger.getLogger(Activator.class);
	/*
	 * Instance
	 */
	private static Activator plugin;
	private List<EventHandler> registeredEventHandler = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		registerEventBroker(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AbstractActivatorUI getDefault() {

		return plugin;
	}

	private void registerEventBroker(BundleContext bundleContext) {

		IEventBroker eventBroker = getEventBroker(bundleContext);
		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.EVENT_BROKER_DATA, IChemClipseEvents.TOPIC_PROCESSING_FILE_CREATED));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				try {
					Object object = event.getProperty(property);
					if(object instanceof File file) {
						if(IChemClipseEvents.TOPIC_PROCESSING_FILE_CREATED.equals(topic)) {
							if(PreferenceSupplier.isOpenReport()) {
								SystemEditor.open(file);
							}
						}
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private IEventBroker getEventBroker(BundleContext bundleContext) {

		IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
		eclipseContext.set(Logger.class, null);
		return eclipseContext.get(IEventBroker.class);
	}
}
