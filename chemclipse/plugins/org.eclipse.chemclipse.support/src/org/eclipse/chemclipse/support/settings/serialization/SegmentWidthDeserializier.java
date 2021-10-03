/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.serialization;

import java.io.IOException;

import org.eclipse.chemclipse.support.model.SegmentWidth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class SegmentWidthDeserializier extends StdScalarDeserializer<Object> {

	private static final long serialVersionUID = 1L;

	protected SegmentWidthDeserializier() {

		super(Object.class);
	}

	@Override
	public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException {

		String text = parser.getText();
		int segmentWidth = SegmentWidth.getAdjustedSetting(text);
		return segmentWidth;
	}
}