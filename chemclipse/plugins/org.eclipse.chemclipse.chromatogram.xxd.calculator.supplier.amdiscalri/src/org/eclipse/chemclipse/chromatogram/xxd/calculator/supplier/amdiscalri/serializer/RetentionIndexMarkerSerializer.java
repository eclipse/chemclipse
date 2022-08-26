/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.serializer;

import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RetentionIndexMarkerSerializer extends JsonSerializer<RetentionIndexMarker> {

	@Override
	public void serialize(RetentionIndexMarker retentionIndexMarker, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

		if(retentionIndexMarker != null) {
			jsonGenerator.writeString(retentionIndexMarker.save());
		} else {
			jsonGenerator.writeString("");
		}
	}
}