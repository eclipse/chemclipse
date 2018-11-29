/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.model.identifier.TargetTemplate;
import org.eclipse.chemclipse.support.util.TargetListUtil;

public class TargetTemplates extends HashMap<String, ITargetTemplate> {

	private static final long serialVersionUID = -439374805911311705L;
	private TargetListUtil targetListUtil = new TargetListUtil();

	public void add(ITargetTemplate targetTemplate) {

		if(targetTemplate != null) {
			put(targetTemplate.getName(), targetTemplate);
		}
	}

	public void load(String targetTemplates) {

		loadSettings(targetTemplates);
	}

	public void loadDefault(String targetTemplates) {

		loadSettings(targetTemplates);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		Iterator<ITargetTemplate> iterator = values().iterator();
		while(iterator.hasNext()) {
			ITargetTemplate targetTemplate = iterator.next();
			extractTargetTemplate(targetTemplate, builder);
			if(iterator.hasNext()) {
				builder.append(TargetListUtil.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	private void loadSettings(String targetTemplates) {

		if(!"".equals(targetTemplates)) {
			String[] items = targetListUtil.parseString(targetTemplates);
			if(items.length > 0) {
				for(String item : items) {
					ITargetTemplate targetTemplate = extractTargetTemplate(item);
					if(targetTemplate != null) {
						add(targetTemplate);
					}
				}
			}
		}
	}

	public String extractTargetTemplate(ITargetTemplate targetTemplate) {

		StringBuilder builder = new StringBuilder();
		extractTargetTemplate(targetTemplate, builder);
		return builder.toString();
	}

	public ITargetTemplate extractTargetTemplate(String item) {

		ITargetTemplate targetTemplate = null;
		//
		if(!"".equals(item)) {
			String[] values = item.split("\\" + TargetListUtil.SEPARATOR_ENTRY);
			targetTemplate = new TargetTemplate();
			targetTemplate.setName((values.length > 0) ? values[0].trim() : "");
			targetTemplate.setCasNumber((values.length > 1) ? values[1].trim() : "");
			targetTemplate.setComments((values.length > 2) ? values[2].trim() : "");
			targetTemplate.setContributor((values.length > 3) ? values[3].trim() : "");
			targetTemplate.setReferenceId((values.length > 4) ? values[4].trim() : "");
		}
		//
		return targetTemplate;
	}

	private void extractTargetTemplate(ITargetTemplate targetTemplate, StringBuilder builder) {

		builder.append(targetTemplate.getName());
		builder.append(" ");
		builder.append(TargetListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(targetTemplate.getCasNumber());
		builder.append(" ");
		builder.append(TargetListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(targetTemplate.getComments());
		builder.append(" ");
		builder.append(TargetListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(targetTemplate.getContributor());
		builder.append(" ");
		builder.append(TargetListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(targetTemplate.getReferenceId());
	}
}
