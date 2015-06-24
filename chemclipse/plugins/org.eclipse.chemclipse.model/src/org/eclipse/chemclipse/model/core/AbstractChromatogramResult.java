/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public abstract class AbstractChromatogramResult implements IChromatogramResult {

	String identifier = "";
	String description = "";
	Object result = null;

	public AbstractChromatogramResult(String identifier, String description, Object result) {

		this.identifier = identifier;
		this.description = description;
		this.result = result;
	}

	@Override
	public String getIdentifier() {

		return identifier;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public Object getResult() {

		return result;
	}
}
