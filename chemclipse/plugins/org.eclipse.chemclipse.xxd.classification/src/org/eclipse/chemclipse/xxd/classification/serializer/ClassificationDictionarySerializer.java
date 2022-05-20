/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.serializer;

import java.io.IOException;

import org.eclipse.chemclipse.xxd.classification.model.ClassificationDictionary;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ClassificationDictionarySerializer extends JsonSerializer<ClassificationDictionary> {

	@Override
	public void serialize(ClassificationDictionary classifcationDictionary, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

		if(classifcationDictionary != null) {
			jsonGenerator.writeString(classifcationDictionary.save());
		} else {
			jsonGenerator.writeString("");
		}
	}
}
