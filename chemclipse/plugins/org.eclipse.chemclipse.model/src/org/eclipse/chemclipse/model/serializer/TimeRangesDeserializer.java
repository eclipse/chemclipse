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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TimeRangesDeserializer extends JsonDeserializer<TimeRanges> {

	@Override
	public TimeRanges deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

		TimeRanges timeRanges = new TimeRanges();
		timeRanges.load(jsonParser.getText());
		//
		return timeRanges;
	}
}