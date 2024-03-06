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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class WavenumberSignalsSerializer extends JsonSerializer<WavenumberSignals> {

	@Override
	public void serialize(WavenumberSignals settings, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

		if(settings != null) {
			jsonGenerator.writeString(settings.save());
		} else {
			jsonGenerator.writeString("");
		}
	}
}