/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.internal.provider.Tuple;

public class ProcessorSupport {

	private static final String VALUE_DELIMITER = ";";
	private static final String PROCESSOR_DELIMITER = ":";

	public static List<Processor> getActiveProcessors(ProcessSupplierContext context, String preference) {

		List<Processor> activeProcessors = new ArrayList<>();
		//
		if(preference != null && !preference.isEmpty()) {
			String[] processors = preference.split(PROCESSOR_DELIMITER);
			for(String processor : processors) {
				String[] values = processor.split(VALUE_DELIMITER);
				if(values != null && values.length == 3) {
					String id = values[0];
					String imageFileName = values[1];
					boolean isActive = Boolean.valueOf(values[2]);
					IProcessSupplier<?> processSupplier = context.getSupplier(id);
					Processor activeProcessor = new Processor(processSupplier);
					activeProcessor.setImageFileName(imageFileName);
					activeProcessor.setActive(isActive);
					activeProcessors.add(activeProcessor);
				}
			}
		}
		//
		return activeProcessors;
	}

	public static String getActiveProcessors(List<Processor> processors) {

		StringBuilder builder = new StringBuilder();
		//
		if(processors != null) {
			List<Processor> activeProcessors = processors.stream().filter(p -> p.isActive()).collect(Collectors.toList());
			Iterator<Processor> iterator = activeProcessors.iterator();
			while(iterator.hasNext()) {
				Processor processor = iterator.next();
				IProcessSupplier<?> processSupplier = processor.getProcessSupplier();
				//
				builder.append(processSupplier.getId());
				builder.append(VALUE_DELIMITER);
				builder.append(processor.getImageFileName());
				builder.append(VALUE_DELIMITER);
				builder.append(processor.isActive());
				//
				if(iterator.hasNext()) {
					builder.append(PROCESSOR_DELIMITER);
				}
			}
		}
		//
		return builder.toString();
	}

	public static void switchProcessor(List<Processor> processors, Processor processorActive, boolean moveUp) {

		if(processors != null && processorActive != null) {
			/*
			 * Map active processor by index.
			 */
			TreeMap<Integer, Processor> processorMap = new TreeMap<>();
			int indexActive = -1;
			//
			for(int i = 0; i < processors.size(); i++) {
				Processor processor = processors.get(i);
				if(processor.isActive()) {
					processorMap.put(i, processor);
					if(processor == processorActive) {
						indexActive = i;
					}
				}
			}
			//
			if(indexActive >= 0) {
				Integer indexSwitch = moveUp ? processorMap.lowerKey(indexActive) : processorMap.higherKey(indexActive);
				if(indexSwitch != null) {
					Collections.swap(processors, indexActive, indexSwitch);
				}
			}
		}
	}

	public static List<Processor> getProcessors(Set<IProcessSupplier<?>> processSuppliers, String preference) {

		List<Processor> processors = new ArrayList<>();
		Map<String, String> activeProcessorMap = getActiveProcessorMap(preference);
		/*
		 * Fetch the processor list.
		 */
		for(IProcessSupplier<?> processSupplier : processSuppliers) {
			Processor processor = new Processor(processSupplier);
			String id = processSupplier.getId();
			String imageFileName = activeProcessorMap.get(id);
			if(imageFileName != null) {
				processor.setImageFileName(imageFileName);
				processor.setActive(true);
			}
			processors.add(processor);
		}
		/*
		 * Swap elements on demand.
		 */
		List<Tuple> activeProcessorSwapList = getActiveProcessorSwapList(processors, preference);
		int sizeSwapList = activeProcessorSwapList.size();
		for(int i = 0; i < sizeSwapList; i++) {
			Tuple tupleActive = activeProcessorSwapList.get(i);
			int delta = tupleActive.getIndex() - i;
			if(delta != 0) {
				int deltaIndex = i + delta;
				if(deltaIndex >= 0 && deltaIndex < sizeSwapList) {
					Tuple tupleSwitch = activeProcessorSwapList.get(deltaIndex);
					int indexActive = tupleActive.getPosition();
					int indexSwitch = tupleSwitch.getPosition();
					Collections.swap(processors, indexActive, indexSwitch);
				}
			}
		}
		//
		return processors;
	}

	private static Map<String, String> getActiveProcessorMap(String preference) {

		Map<String, String> activeProcessorMap = new HashMap<>();
		//
		if(preference != null && !preference.isEmpty()) {
			String[] processors = preference.split(PROCESSOR_DELIMITER);
			for(String processor : processors) {
				String[] values = processor.split(VALUE_DELIMITER);
				if(values != null && values.length == 3) {
					String id = values[0];
					String imageFileName = values[1];
					activeProcessorMap.put(id, imageFileName);
				}
			}
		}
		//
		return activeProcessorMap;
	}

	private static List<Tuple> getActiveProcessorSwapList(List<Processor> processors, String preference) {

		List<String> activeProcessorIds = getActiveProcessorIds(preference);
		List<Tuple> activeProcessorSwapList = new ArrayList<>();
		//
		for(int i = 0; i < processors.size(); i++) {
			Processor processor = processors.get(i);
			if(processor.isActive()) {
				int j = getProcessorIndex(activeProcessorIds, processor.getProcessSupplier().getId());
				if(j >= 0) {
					/*
					 * i = position in list
					 * j = index active
					 */
					activeProcessorSwapList.add(new Tuple(i, j));
				}
			}
		}
		//
		return activeProcessorSwapList;
	}

	private static int getProcessorIndex(List<String> activeProcessorIds, String processorId) {

		for(int i = 0; i < activeProcessorIds.size(); i++) {
			String activeProcessorId = activeProcessorIds.get(i);
			if(activeProcessorId.equals(processorId)) {
				return i;
			}
		}
		//
		return -1;
	}

	private static List<String> getActiveProcessorIds(String preference) {

		List<String> activeProcessorIds = new ArrayList<>();
		//
		if(preference != null && !preference.isEmpty()) {
			String[] processors = preference.split(PROCESSOR_DELIMITER);
			for(String processor : processors) {
				String[] values = processor.split(VALUE_DELIMITER);
				if(values != null && values.length == 3) {
					String id = values[0];
					activeProcessorIds.add(id);
				}
			}
		}
		//
		return activeProcessorIds;
	}
}