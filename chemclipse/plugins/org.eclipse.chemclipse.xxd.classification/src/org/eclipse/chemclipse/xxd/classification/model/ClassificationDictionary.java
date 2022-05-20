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
package org.eclipse.chemclipse.xxd.classification.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.xxd.classification.validators.ClassificationRuleValidator;
import org.eclipse.core.runtime.IStatus;

public class ClassificationDictionary extends ArrayList<ClassificationRule> {

	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final long serialVersionUID = 7575344470693039105L;
	private static final Logger logger = Logger.getLogger(ClassificationDictionary.class);

	public void load(String items) {

		loadDictionary(items);
	}

	public String save() {

		return extract();
	}

	public ClassificationRule extractClassificationRule(String item) {

		return extract(item);
	}

	public void importRules(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				ClassificationRule rule = extract(line);
				if(rule != null) {
					add(rule);
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	public boolean exportRules(File file) {

		try {
			PrintWriter printWriter = new PrintWriter(file);
			Iterator<ClassificationRule> iterator = iterator();
			while(iterator.hasNext()) {
				ClassificationRule rule = iterator.next();
				StringBuilder builder = new StringBuilder();
				builder.append(rule.getSearchExpression());
				builder.append(WHITE_SPACE);
				builder.append(SEPARATOR_ENTRY);
				builder.append(WHITE_SPACE);
				builder.append(rule.getClassification());
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			printWriter.close();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private String extract() {

		StringBuilder builder = new StringBuilder();
		Iterator<ClassificationRule> iterator = iterator();
		while(iterator.hasNext()) {
			ClassificationRule rule = iterator.next();
			builder.append(rule.getSearchExpression());
			builder.append(WHITE_SPACE);
			builder.append(SEPARATOR_ENTRY);
			builder.append(WHITE_SPACE);
			builder.append(rule.getClassification());
			if(iterator.hasNext())
				builder.append(SEPARATOR_TOKEN);
		}
		return builder.toString().trim();
	}

	private ClassificationRule extract(String text) {

		ClassificationRule rule = null;
		ClassificationRuleValidator validator = new ClassificationRuleValidator();
		//
		IStatus status = validator.validate(text);
		if(status.isOK()) {
			rule = validator.getSetting();
		} else {
			logger.warn(status.getMessage());
		}
		//
		return rule;
	}

	private void loadDictionary(String input) {

		String[] lines;
		if(input.contains(SEPARATOR_TOKEN)) {
			lines = input.split(SEPARATOR_TOKEN);
		} else {
			lines = new String[1];
			lines[0] = input;
		}
		for(String line : lines) {
			ClassificationRule rule = extractClassificationRule(line);
			if(rule != null && !contains(rule)) {
				add(rule);
			}
		}
	}
}
