/*******************************************************************************
 * Copyright (c) 2016 Dr. Janos Binder.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.documents;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@SuppressWarnings("unchecked")
public class SettingsDocument extends AbstractDocument implements ISettingsDocument {

	public SettingsDocument() {
		super(CLASS_NAME);
	}

	public SettingsDocument(ODocument document) {
		super(document);
	}

	@Override
	public void setFields() {

		setType(getType());
	}

	@Override
	public void setType(String type) {

		field(FIELD_TYPE, type, OType.STRING);
	}

	@Override
	public String getType() {

		return getString(FIELD_TYPE);
	}
}
