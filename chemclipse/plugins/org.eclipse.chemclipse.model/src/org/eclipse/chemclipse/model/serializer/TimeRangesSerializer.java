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
package org.eclipse.chemclipse.model.serializer;

import java.io.IOException;

import org.eclipse.chemclipse.model.ranges.TimeRanges;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimeRangesSerializer extends JsonSerializer<TimeRanges> {

	@Override
	public void serialize(TimeRanges timeRanges, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

		if(timeRanges != null) {
			jsonGenerator.writeString(timeRanges.save());
		} else {
			jsonGenerator.writeString("");
		}
	}
}