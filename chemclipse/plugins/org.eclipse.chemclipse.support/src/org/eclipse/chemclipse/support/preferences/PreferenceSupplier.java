/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import org.eclipse.chemclipse.support.Activator;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_CLIPBOARD_TABLE_DEFAULT_SORTING = "clipboardTableDefaultSorting";
	public static final boolean DEF_CLIPBOARD_TABLE_DEFAULT_SORTING = false;
	public static final String P_UNDO_LIMIT = "undoLimit";
	public static final int DEF_UNDO_LIMIT = 5;
	/*
	 * The prefix to store the clipboard settings.
	 * They are not initialized here as they will be stored
	 * dynamically by the table name.
	 */
	public static final String P_CLIPBOARD_COPY_HEADER = "clipboardCopyHeader_";
	public static final boolean DEF_CLIPBOARD_COPY_HEADER = true;
	public static final String P_CLIPBOARD_COPY_COLUMNS = "clipboardCopyColumns_";
	public static final String DEF_CLIPBOARD_COPY_COLUMNS = "";
	public static final String P_COLUMN_ORDER = "columnOrder_";
	public static final String DEF_COLUMN_ORDER = "";
	public static final String P_COLUMN_WIDTH = "columnWidth_";
	public static final String DEF_COLUMN_WIDTH = "";
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_CLIPBOARD_TABLE_DEFAULT_SORTING, Boolean.toString(DEF_CLIPBOARD_TABLE_DEFAULT_SORTING));
		putDefault(P_UNDO_LIMIT, Integer.toString(DEF_UNDO_LIMIT));
	}

	public static boolean isClipboardCopyHeader(String key) {

		return INSTANCE().getBoolean(key, DEF_CLIPBOARD_COPY_HEADER);
	}

	public static void setClipboardCopyHeader(String key, boolean value) {

		INSTANCE().putBoolean(key, value);
	}

	public static String getClipboardCopyColumns(String key) {

		return INSTANCE().get(key, DEF_CLIPBOARD_COPY_COLUMNS);
	}

	public static void setClipboardCopyColumns(String key, String value) {

		INSTANCE().put(key, value);
	}

	public static String getColumnOrder(String key) {

		return INSTANCE().get(key, DEF_COLUMN_ORDER);
	}

	public static void setColumnOrder(String key, String value) {

		INSTANCE().put(key, value);
	}

	public static String getColumnWidth(String key) {

		return INSTANCE().get(key, DEF_COLUMN_WIDTH);
	}

	public static void setColumnWidth(String key, String value) {

		INSTANCE().put(key, value);
	}

	public static boolean isClipboardDefaultSorting() {

		return INSTANCE().getBoolean(P_CLIPBOARD_TABLE_DEFAULT_SORTING, DEF_CLIPBOARD_TABLE_DEFAULT_SORTING);
	}

	public static int getUndoLimit() {

		return INSTANCE().getInteger(P_UNDO_LIMIT, DEF_UNDO_LIMIT);
	}
}