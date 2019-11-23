/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.gson.model;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public class JsonIdentificationTarget extends IdentificationTarget {

	private JsonIdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult, String identifier) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult, identifier);
	}

	private static final long serialVersionUID = 1L;
	private static final String KEY_TARGET_IDENTIFIER = "identifier";
	private static final String KEY_TARGET_RESULT_MF = "mf";
	private static final String KEY_TARGET_RESULT_RMF = "rmf";
	private static final String KEY_TARGET_RESULT_MFD = "mfd";
	private static final String KEY_TARGET_RESULT_RMFD = "rmfsd";
	private static final String KEY_LIBRARYINFORMATION = "libraryinformation";
	private static final String KEY_LIBRARY_CAS = "cas";
	private static final String KEY_LIBRARY_NAME = "name";

	public static void writeTargets(JsonWriter writer, Collection<? extends IIdentificationTarget> targets) throws IOException {

		writer.beginArray();
		for(IIdentificationTarget target : targets) {
			writer.beginObject();
			writer.name(KEY_TARGET_IDENTIFIER);
			writer.value(target.getIdentifier());
			IComparisonResult result = target.getComparisonResult();
			writer.name(KEY_TARGET_RESULT_MF);
			writer.value(result.getMatchFactor());
			writer.name(KEY_TARGET_RESULT_RMF);
			writer.value(result.getReverseMatchFactor());
			writer.name(KEY_TARGET_RESULT_MFD);
			writer.value(result.getMatchFactorDirect());
			writer.name(KEY_TARGET_RESULT_RMFD);
			writer.value(result.getReverseMatchFactorDirect());
			writer.name(KEY_LIBRARYINFORMATION);
			writeLibraryInformation(writer, target.getLibraryInformation());
			writer.endObject();
		}
		writer.endArray();
	}

	public static void readTargets(JsonArray json, Consumer<? super IIdentificationTarget> consumer) {

		for(JsonElement element : json) {
			JsonObject target = element.getAsJsonObject();
			ComparisonResult result = new ComparisonResult(target.get(KEY_TARGET_RESULT_MF).getAsFloat(), target.get(KEY_TARGET_RESULT_RMF).getAsFloat(), target.get(KEY_TARGET_RESULT_MFD).getAsFloat(), target.get(KEY_TARGET_RESULT_RMF).getAsFloat());
			consumer.accept(new JsonIdentificationTarget(readLibraryInformation(target.get(KEY_LIBRARYINFORMATION)), result, target.get(KEY_TARGET_IDENTIFIER).getAsString()));
		}
	}

	private static ILibraryInformation readLibraryInformation(JsonElement jsonElement) {

		JsonObject object = jsonElement.getAsJsonObject();
		LibraryInformation information = new LibraryInformation();
		information.setName(object.get(KEY_LIBRARY_NAME).getAsString());
		information.setCasNumber(object.get(KEY_LIBRARY_CAS).getAsString());
		return information;
	}

	private static void writeLibraryInformation(JsonWriter json, ILibraryInformation libraryInformation) throws IOException {

		json.beginObject();
		json.name(KEY_LIBRARY_NAME);
		json.value(libraryInformation.getName());
		json.name(KEY_LIBRARY_CAS);
		json.value(libraryInformation.getCasNumber());
		json.endObject();
	}
}
