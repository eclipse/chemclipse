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
package org.eclipse.chemclipse.chromatogram.vsd.filter.serializer;

import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignals;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class WavenumberSignalsDeserializer extends JsonDeserializer<WavenumberSignals> {

	@Override
	public WavenumberSignals deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

		WavenumberSignals settings = new WavenumberSignals();
		settings.load(jsonParser.getText());
		return settings;
	}
}