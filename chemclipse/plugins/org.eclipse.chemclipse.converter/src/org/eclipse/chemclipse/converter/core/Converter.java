/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

	/*
	 * These are the attributes of the extension point elements.
	 */
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	public static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	public static final String FILE_NAME = "fileName"; //$NON-NLS-1$
	public static final String DIRECTORY_EXTENSION = "directoryExtension"; //$NON-NLS-1$
	public static final String IS_EXPORTABLE = "isExportable"; //$NON-NLS-1$
	public static final String IS_IMPORTABLE = "isImportable"; //$NON-NLS-1$
	public static final String EXPORT_CONVERTER = "exportConverter"; //$NON-NLS-1$
	public static final String IMPORT_CONVERTER = "importConverter"; //$NON-NLS-1$
	public static final String IMPORT_MAGIC_NUMBER_MATCHER = "importMagicNumberMatcher"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private Converter() {
	}

	/**
	 * This method return true if the input string contains a not allowed
	 * character like \/:*?"<>| It returns true if the input string is a valid
	 * string and false if not.<br/>
	 * If the input string is null it returns false.
	 * 
	 * @return boolean
	 */
	public static boolean isValid(final String input) {

		/*
		 *
		 */
		if(input == null) {
			return false;
		}
		/*
		 * Use four times backslash to search after a normal backslash. See
		 * "Mastering Regular Expressions" from Jeffrey Friedl, ISBN:
		 * 0596528124.
		 */
		String regex = "[\\\\/:*?\"<>|]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return !matcher.find();
	}
}
