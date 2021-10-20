/*******************************************************************************
 * Copyright (c) 2021 Labliate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.text;

import java.nio.charset.Charset;

/**
 * See:
 * https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
 *
 */
public enum CharsetNIO implements ILabel {
	US_ASCII("US-ASCII", "ASCII (American Standard Code for Information Interchange)"), //
	UTF_8("UTF-8", "UTF-8"), //
	UTF_16("UTF-16", "UTF-16"), //
	UTF_16BE("UTF-16BE", "UTF-16 (Big-Endian)"), //
	UTF_16LE("UTF-16LE", "UTF-16 (Little-Endian)"), //
	ISO_8859_1("ISO-8859-1", "ISO-8859-1 (Latin Alphabet No. 1)"), ///
	ISO_8859_7("ISO-8859-7", "ISO-8859-7 (Latin/Greek Alphabet)"), //
	ISO_8859_15("ISO-8859-15", "ISO-8859-15 (Latin Alphabet No. 9)"), //
	WINDOWS_1252("windows-1252", "CP1252 (Windows Latin-1)"), //
	WINDOWS_1253("windows-1253", "CP1253 (Windows Greek)"); //

	private String canonicalName;
	private String description;

	private CharsetNIO(String canonicalName, String description) {

		this.canonicalName = canonicalName;
		this.description = description;
	}

	@Override
	public String label() {

		return description;
	}

	public Charset getCharset() {

		return Charset.forName(canonicalName);
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}