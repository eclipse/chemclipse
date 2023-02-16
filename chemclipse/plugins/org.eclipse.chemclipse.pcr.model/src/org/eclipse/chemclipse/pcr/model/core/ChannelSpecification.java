/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;

public class ChannelSpecification extends AbstractMeasurement implements IChannelSpecification {

	private static final long serialVersionUID = 6179615149777166714L;

	public ChannelSpecification() {

		addProtectedKey(NAME);
	}

	@Override
	public String getName() {

		return getHeaderDataOrDefault(NAME, "");
	}
}
