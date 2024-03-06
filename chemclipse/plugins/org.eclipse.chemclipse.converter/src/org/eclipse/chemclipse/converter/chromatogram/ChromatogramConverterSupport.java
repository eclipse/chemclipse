/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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
package org.eclipse.chemclipse.converter.chromatogram;

import org.eclipse.chemclipse.converter.core.AbstractConverterSupport;
import org.eclipse.chemclipse.processing.DataCategory;

/**
 * This class encapsulated information about the registered chromatogram
 * converters.<br/>
 * It offers some convenient methods for getting information about the
 * registered converters.<br/>
 * SWT FileDialog may use it to set the correct dialog information.
 * 
 * @author eselmeister
 */
public class ChromatogramConverterSupport extends AbstractConverterSupport implements IChromatogramConverterSupport {

	private final DataCategory dataCategory;

	public ChromatogramConverterSupport(DataCategory dataCategory) {

		this.dataCategory = dataCategory;
	}

	@Override
	public DataCategory getDataCategory() {

		return dataCategory;
	}
}
