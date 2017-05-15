/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

public class SequenceRecord implements ISequenceRecord {

	private String substance;
	private String method;

	@Override
	public String getSubstance() {

		return substance;
	}

	@Override
	public void setSubstance(String substance) {

		this.substance = substance;
	}

	@Override
	public String getMethod() {

		return method;
	}

	@Override
	public void setMethod(String method) {

		this.method = method;
	}
}
