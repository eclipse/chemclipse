/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedLiteratureUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LiteraturePart extends AbstractPart<ExtendedLiteratureUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_LITERATURE_UPDATE;

	@Inject
	public LiteraturePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedLiteratureUI createControl(Composite parent) {

		return new ExtendedLiteratureUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof String content) {
				getControl().setInput(Arrays.asList(content));
				return true;
			} else if(object instanceof List<?> list) {
				getControl().setInput(list);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}
}