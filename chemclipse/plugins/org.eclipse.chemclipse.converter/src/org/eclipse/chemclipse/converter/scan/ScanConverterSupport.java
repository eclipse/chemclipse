/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - Adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.converter.scan;

import org.eclipse.chemclipse.converter.core.AbstractConverterSupport;
import org.eclipse.chemclipse.processing.DataCategory;

public class ScanConverterSupport extends AbstractConverterSupport implements IScanConverterSupport {

	private final DataCategory category;

	public ScanConverterSupport(DataCategory category) {

		this.category = category;
	}

	@Override
	public DataCategory getDataCategory() {

		return category;
	}
}