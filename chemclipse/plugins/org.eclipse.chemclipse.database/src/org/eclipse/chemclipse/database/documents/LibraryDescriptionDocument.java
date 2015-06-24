/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.documents;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LibraryDescriptionDocument extends AbstractDocument implements ILibraryDescriptionDocument {

	/**
	 * Renew this uid on each change.
	 */
	private static final long serialVersionUID = 1841510692926352886L;

	public LibraryDescriptionDocument() {

		super(CLASS_NAME);
	}

	public LibraryDescriptionDocument(ODocument document) {

		super(document);
	}

	@Override
	public void setFields() {

		setName(getName());
		setDescription(getDescription());
	}

	@Override
	public void setName(String name) {

		field(FIELD_NAME, name, OType.STRING);
	}

	@Override
	public String getName() {

		return getString(FIELD_NAME);
	}

	@Override
	public void setDescription(String description) {

		field(FIELD_DESCRIPTION, description, OType.STRING);
	}

	@Override
	public String getDescription() {

		return getString(FIELD_DESCRIPTION);
	}
}
