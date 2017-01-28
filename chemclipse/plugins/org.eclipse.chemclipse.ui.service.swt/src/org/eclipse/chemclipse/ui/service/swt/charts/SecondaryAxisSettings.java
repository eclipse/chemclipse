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
package org.eclipse.chemclipse.ui.service.swt.charts;

public class SecondaryAxisSettings extends AbstractAxisSettings implements ISecondaryAxisSettings {

	private String identifier;

	public SecondaryAxisSettings(String title, String id) {
		super(title);
	}

	@Override
	public String getIdentifier() {

		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	@Override
	public double getConversion(double value) {

		return 0;
	}
}
