/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - use specific file extension
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.xxd.classification.validators.ClassificationRuleValidator;
import org.eclipse.core.runtime.IStatus;

public class ClassificationDictionary extends ArrayList<ClassificationRule> {

	public static final String DESCRIPTION = "Classification Dictionary";
	public static final String FILE_EXTENSION = ".cld";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
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
				ClassificationRule classificationRule = extract(line);
				if(classificationRule != null) {
					if(!contains(classificationRule)) {
						add(classificationRule);
					}
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	public boolean exportRules(File file) {

		boolean success = false;
		try (PrintWriter printWriter = new PrintWriter(file)) {
			List<ClassificationRule> classificationRules = new ArrayList<>(this);
			Collections.sort(classificationRules, (r1, r2) -> r1.getSearchExpression().compareTo(r2.getSearchExpression()));
			for(ClassificationRule classificationRule : classificationRules) {
				StringBuilder builder = new StringBuilder();
				builder.append(classificationRule.getSearchExpression());
				addSeparator(builder);
				builder.append(classificationRule.getClassification());
				addSeparator(builder);
				builder.append(classificationRule.getReference().name());
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			success = true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
		}
		//
		return success;
	}

	private String extract() {

		StringBuilder builder = new StringBuilder();
		Iterator<ClassificationRule> iterator = iterator();
		//
		while(iterator.hasNext()) {
			ClassificationRule classificationRule = iterator.next();
			builder.append(classificationRule.getSearchExpression());
			addSeparator(builder);
			builder.append(classificationRule.getClassification());
			addSeparator(builder);
			builder.append(classificationRule.getReference().name());
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		//
		return builder.toString().trim();
	}

	private ClassificationRule extract(String text) {

		ClassificationRule classificationRule = null;
		ClassificationRuleValidator validator = new ClassificationRuleValidator();
		//
		IStatus status = validator.validate(text);
		if(status.isOK()) {
			classificationRule = validator.getSetting();
		} else {
			logger.warn(status.getMessage());
		}
		//
		return classificationRule;
	}

	private void loadDictionary(String input) {

		String[] lines;
		if(input.contains(SEPARATOR_TOKEN)) {
			lines = input.split(SEPARATOR_TOKEN);
		} else {
			lines = new String[1];
			lines[0] = input;
		}
		//
		for(String line : lines) {
			ClassificationRule rule = extractClassificationRule(line);
			if(rule != null && !contains(rule)) {
				add(rule);
			}
		}
	}

	private void addSeparator(StringBuilder builder) {

		builder.append(WHITE_SPACE);
		builder.append(SEPARATOR_ENTRY);
		builder.append(WHITE_SPACE);
	}
}