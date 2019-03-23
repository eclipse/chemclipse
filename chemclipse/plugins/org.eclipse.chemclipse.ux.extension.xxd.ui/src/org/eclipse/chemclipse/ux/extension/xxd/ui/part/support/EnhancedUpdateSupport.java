/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public abstract class EnhancedUpdateSupport extends AbstractUpdateSupport implements IEnhancedUpdateSupport {

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

				isVisible = doUpdate();
				if(isVisible) {
					parent.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							updateSelection(objects, topic);
						}
					});
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

	private void updateLatestSelection() {

		updateSelection(dataUpdateSupport.getUpdates(defaultTopic), defaultTopic);
	}
}
