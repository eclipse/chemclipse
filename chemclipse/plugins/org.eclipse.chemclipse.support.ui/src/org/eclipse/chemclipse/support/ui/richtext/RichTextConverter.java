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
package org.eclipse.chemclipse.support.ui.richtext;

import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

public class RichTextConverter {

	private static final String MIME_TYPE_RTF = "text/rtf";
	private static final String MIME_TYPE_HTML = "text/html";

	public static String convertRtfToHtml(String content, boolean bodyOnly) {

		String body;
		if(content == null) {
			body = "";
		} else {
			body = content;
			if(!content.isEmpty()) {
				if(content.contains("\\par") && content.contains("{") && content.contains("}")) {
					try {
						/*
						 * <html>
						 * <head>
						 * <style>
						 * <!--
						 * -->
						 * </style>
						 * </head>
						 * <body>
						 * ...
						 * </body>
						 * </html>
						 * ---------------------
						 * Replace <p class=default> ...
						 */
						StringBuilder builder = new StringBuilder();
						String[] lines = convert(content).split("\\n");
						int size = lines.length;
						int limit = bodyOnly ? 11 : 0;
						if(size > limit) {
							int start = bodyOnly ? 8 : 0;
							int max = bodyOnly ? size - 2 : size;
							for(int i = start; i < max; i++) {
								String line = lines[i].replace("<p class=default>", "<p>").trim();
								if(!line.isBlank()) {
									builder.append(line);
								}
							}
							body = builder.toString();
						}
					} catch(Exception e) {
					}
				}
			}
		}
		//
		return body;
	}

	private static String convert(String value) throws Exception {

		JEditorPane jEditorPane = new JEditorPane(MIME_TYPE_RTF, value);
		return extract(jEditorPane, MIME_TYPE_HTML);
	}

	private static String extract(JEditorPane jEditorPane, String mimeType) throws Exception {

		String content = "";
		EditorKit editorKitHTML = getEditorKit(jEditorPane, mimeType);
		try (Writer writer = new StringWriter()) {
			editorKitHTML.write(writer, jEditorPane.getDocument(), 0, jEditorPane.getDocument().getLength());
			content = writer.toString();
		}
		//
		return content;
	}

	private static EditorKit getEditorKit(JEditorPane jEditorPane, String mimeType) {

		return jEditorPane.getEditorKitForContentType(mimeType);
	}
}
