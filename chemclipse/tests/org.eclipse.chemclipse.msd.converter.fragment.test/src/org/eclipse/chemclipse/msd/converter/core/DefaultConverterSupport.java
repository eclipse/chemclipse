/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.core;

import org.eclipse.chemclipse.converter.core.AbstractConverterSupport;
import org.eclipse.chemclipse.processing.DataCategory;

/**
 * THIS IS A TESTCLASS. DO NOT USE IN PRODUCTION!
 * 
 * @author Philip Wenig
 * 
 */
public class DefaultConverterSupport extends AbstractConverterSupport {

	@Override
	public DataCategory getDataCategory() {

		return DataCategory.AUTO_DETECT;
	}

	@Override
	public String getName() {

		return "Test";
	}
}
