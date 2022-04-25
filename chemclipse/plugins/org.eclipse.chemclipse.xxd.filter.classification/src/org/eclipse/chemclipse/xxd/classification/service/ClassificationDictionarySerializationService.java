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
package org.eclipse.chemclipse.xxd.classification.service;

import org.eclipse.chemclipse.support.settings.serialization.ISerializationService;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationDictionary;
import org.eclipse.chemclipse.xxd.classification.serializer.ClassificationDictionaryDeserializer;
import org.eclipse.chemclipse.xxd.classification.serializer.ClassificationDictionarySerializer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

@Component(service = {ISerializationService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ClassificationDictionarySerializationService implements ISerializationService {

	@Override
	public Class<?> getSupportedClass() {

		return ClassificationDictionary.class;
	}

	@Override
	public JsonSerializer<ClassificationDictionary> getSerializer() {

		return new ClassificationDictionarySerializer();
	}

	@Override
	public JsonDeserializer<ClassificationDictionary> getDeserializer() {

		return new ClassificationDictionaryDeserializer();
	}
}
