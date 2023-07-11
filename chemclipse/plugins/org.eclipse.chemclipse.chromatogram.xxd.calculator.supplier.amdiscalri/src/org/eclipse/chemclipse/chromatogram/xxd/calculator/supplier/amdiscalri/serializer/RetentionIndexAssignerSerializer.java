/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexAssigner;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RetentionIndexAssignerSerializer extends JsonSerializer<RetentionIndexAssigner> {

	@Override
	public void serialize(RetentionIndexAssigner retentionIndexAssigner, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

		if(retentionIndexAssigner != null) {
			jsonGenerator.writeString(retentionIndexAssigner.save());
		} else {
			jsonGenerator.writeString("");
		}
	}
}