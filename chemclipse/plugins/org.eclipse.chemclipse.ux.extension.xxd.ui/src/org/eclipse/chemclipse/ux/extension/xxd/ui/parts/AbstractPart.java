/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPart<T extends Composite> {

	private T control;
	private boolean initialUpdate = true;
	private String topic = "";
	//
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
	private IDataUpdateListener updateListener = new IDataUpdateListener() {

		@Override
		public void update(String topic, List<Object> objects) {

			updateSelection(objects, topic);
		}
	};

	/**
	 * Create the part with the given main topic.
	 * 
	 * @param parent
	 * @param topic
	 */
	public AbstractPart(Composite parent, String topic) {

		this.topic = topic;
		control = createControl(parent);
		dataUpdateSupport.add(updateListener);
		subscribeAdditionalTopics();
	}

	@Focus
	public void setFocus() {

		if(initialUpdate) {
			if(updateSelection(dataUpdateSupport.getUpdates(topic), topic)) {
				initialUpdate = false;
			}
		}
	}

	@PreDestroy
	protected void preDestroy() {

		dataUpdateSupport.remove(updateListener);
		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_PART_CLOSED, getClass().getSimpleName());
		}
	}

	/**
	 * Return the control of this part.
	 * 
	 * @return T
	 */
	protected T getControl() {

		return control;
	}

	/**
	 * Overwrite, if additional topics shall be added.
	 */
	protected void subscribeAdditionalTopics() {

	}

	protected void subscribeAdditionalTopic(String topic, String property) {

		dataUpdateSupport.subscribe(topic, property);
	}

	protected void subscribeAdditionalTopic(String topic, String[] properties) {

		dataUpdateSupport.subscribe(topic, properties);
	}

	/**
	 * Create the control.
	 * 
	 * @param parent
	 * @return T
	 */
	protected abstract T createControl(Composite parent);

	/**
	 * Implement to update the object of the given topic.
	 * If consuming the topic was successful, return true.
	 * 
	 * @param objects
	 * @param topic
	 * @return boolean
	 */
	protected abstract boolean updateData(List<Object> objects, String topic);

	/**
	 * Returns whether this topic shall be consumed or not.
	 * 
	 * @param topic
	 * @return boolean
	 */
	protected boolean isUpdateTopic(String topic) {

		return this.topic.equals(topic);
	}

	private boolean updateSelection(List<Object> objects, String topic) {

		if(DataUpdateSupport.isVisible(control)) {
			if(isUpdateTopic(topic)) {
				return updateData(objects, topic);
			}
		}
		//
		return false;
	}
}
