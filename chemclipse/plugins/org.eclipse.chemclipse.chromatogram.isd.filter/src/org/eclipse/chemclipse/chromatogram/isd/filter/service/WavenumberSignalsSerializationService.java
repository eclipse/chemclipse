/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.service;

import org.eclipse.chemclipse.chromatogram.isd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.chromatogram.isd.filter.serializer.WavenumberSignalsDeserializer;
import org.eclipse.chemclipse.chromatogram.isd.filter.serializer.WavenumberSignalsSerializer;
import org.eclipse.chemclipse.support.settings.serialization.ISerializationService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

@Component(service = {ISerializationService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class WavenumberSignalsSerializationService implements ISerializationService {

	@Override
	public Class<?> getSupportedClass() {

		return WavenumberSignals.class;
	}

	@Override
	public JsonSerializer<WavenumberSignals> getSerializer() {

		return new WavenumberSignalsSerializer();
	}

	@Override
	public JsonDeserializer<WavenumberSignals> getDeserializer() {

		return new WavenumberSignalsDeserializer();
	}
}