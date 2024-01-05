/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.literature.LiteratureSupport;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/*
 * Description of the RIS format.
 * https://en.wikipedia.org/wiki/RIS_(file_format)
 */
public class LiteratureUI extends StyledText {

	private static final String LINE_DELIMITER = OperatingSystemUtils.getLineDelimiter();
	//
	private String content = "";

	public LiteratureUI(Composite parent, int style) {

		super(parent, style);
	}

	@Override
	public void setText(String content) {

		this.content = content;
		updateInput();
	}

	public void updateSearchResult(String searchText, boolean caseSensitive) {

		if(!content.isEmpty()) {
			if(searchText.isEmpty()) {
				updateInput();
			} else {
				/*
				 * Format
				 */
				String literatureContent;
				if(LiteratureSupport.isFormatRIS(content)) {
					literatureContent = LiteratureSupport.getFormattedRIS(content, true);
				} else {
					literatureContent = content;
				}
				/*
				 * Display Search Result
				 */
				String contentSearch = caseSensitive ? literatureContent : literatureContent.toLowerCase();
				String textSearch = caseSensitive ? searchText : searchText.toLowerCase();
				if(contentSearch.contains(textSearch)) {
					formatSearch(contentSearch, textSearch);
				}
			}
		}
	}

	private void updateInput() {

		if(LiteratureSupport.isFormatRIS(content)) {
			formatRIS(content);
		} else {
			super.setText(content);
		}
	}

	private void formatRIS(String content) {

		Color color = Colors.DARK_RED;
		StringBuilder builder = new StringBuilder();
		List<StyleRange> styleRanges = new ArrayList<>();
		//
		String formattedRIS = LiteratureSupport.getFormattedRIS(content, false);
		String[] lines = formattedRIS.split(LiteratureSupport.LINE_DELIMITER);
		int offset = 0;
		for(String line : lines) {
			String[] entries = line.split(LiteratureSupport.ENTTRY_DELIMITER);
			if(entries.length == 2) {
				String identifier = entries[0].trim();
				if(!LiteratureSupport.isEndOfReferenceRIS(identifier)) {
					String key = LiteratureSupport.getMappedIdentifierRIS(identifier);
					String data = entries[1].trim();
					offset = append(builder, styleRanges, offset, key, data, color, false);
				}
			}
		}
		//
		super.setText(builder.toString());
		super.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
	}

	private void formatSearch(String content, String textSearch) {

		Color color = Colors.RED;
		StringBuilder builder = new StringBuilder();
		List<StyleRange> styleRanges = new ArrayList<>();
		//
		String[] lines = content.split(LiteratureSupport.LINE_DELIMITER);
		for(String line : lines) {
			if(line.contains(textSearch)) {
				String data = line;
				int offset = line.indexOf(textSearch);
				offset = append(builder, styleRanges, offset, textSearch, data, color, true);
			}
		}
		//
		super.setText(builder.toString());
		super.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
	}

	private int append(StringBuilder builder, List<StyleRange> styleRanges, int offset, String key, String value, Color color, boolean search) {

		int keyLength = 0;
		if(!search) {
			builder.append(key);
			builder.append(": ");
			keyLength = key.length() + 2;
		}
		builder.append(value);
		builder.append(LINE_DELIMITER);
		//
		StyleRange styleRange = new StyleRange();
		styleRange.start = offset;
		styleRange.length = key.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = color;
		styleRanges.add(styleRange);
		//
		return offset + keyLength + value.length() + LINE_DELIMITER.length();
	}
}