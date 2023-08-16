/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.util.ValueParserSupport;

public class ProcessorSupport {

	private static final Logger logger = Logger.getLogger(ProcessorSupport.class);
	//
	public static final String PROCESSOR_IMAGE_DEFAULT = IApplicationImage.IMAGE_EXECUTE_EXTENSION;
	/*
	 * The processors must not contain the following persistence delimiters.
	 * : can't be used as it could be part of the id. Regex keywords can be also
	 * not used, like $.
	 */
	private static final String VALUE_DELIMITER = "§";
	private static final String PROCESSOR_DELIMITER = "%";
	//
	private static ValueParserSupport valueParseSupport = new ValueParserSupport();

	public static List<Processor> getActiveProcessors(Set<IProcessSupplier<?>> processSuppliers, String settings) {

		List<Processor> processors = new ArrayList<>();
		for(IProcessSupplier<?> processSupplier : processSuppliers) {
			processors.add(new Processor(processSupplier));
		}
		/*
		 * Fetch the processor list.
		 */
		if(settings != null && !settings.isEmpty()) {
			for(String processorSettings : settings.split(PROCESSOR_DELIMITER)) {
				String[] values = processorSettings.split(VALUE_DELIMITER);
				if(values != null) {
					/*
					 * Parse the settings
					 */
					String id = getString(values, 0, "");
					String imageFileName = getString(values, 1, "");
					boolean active = getBoolean(values, 2, true);
					int index = getInteger(values, 3, Processor.INDEX_NONE);
					/*
					 * Transfer
					 */
					if(!id.isEmpty()) {
						Processor processor = getProcessor(processors, id);
						if(processor != null) {
							processor.setImageFileName(imageFileName);
							processor.setActive(active);
							processor.setIndex(index);
						}
					}
				}
			}
		}
		/*
		 * Sort by index
		 */
		return sortProcessorsByIndex(processors);
	}

	public static String getActiveProcessors(List<Processor> processors) {

		StringBuilder builder = new StringBuilder();
		//
		if(processors != null) {
			List<Processor> activeProcessors = processors.stream().filter(Processor::isActive).toList();
			Iterator<Processor> iterator = activeProcessors.iterator();
			while(iterator.hasNext()) {
				Processor processor = iterator.next();
				if(isValid(processor)) {
					IProcessSupplier<?> processSupplier = processor.getProcessSupplier();
					//
					builder.append(processSupplier.getId());
					builder.append(VALUE_DELIMITER);
					builder.append(processor.getImageFileName());
					builder.append(VALUE_DELIMITER);
					builder.append(processor.isActive());
					builder.append(VALUE_DELIMITER);
					builder.append(processor.getIndex());
					//
					if(iterator.hasNext()) {
						builder.append(PROCESSOR_DELIMITER);
					}
				} else {
					logger.warn("The processor is null or contains at least one unvalid char: " + processor);
				}
			}
		}
		//
		return builder.toString();
	}

	public static List<Processor> filterProcessors(List<Processor> processors, boolean active, boolean updateIndex) {

		/*
		 * Filter Inactive/Active
		 */
		List<Processor> processorsFiltered = processors.stream().filter(p -> (p.isActive() == active)).collect(Collectors.toList());
		if(active) {
			/*
			 * Update indices and sort on demand.
			 */
			if(updateIndex) {
				ProcessorSupport.updateProcessorIndices(processorsFiltered);
			}
			Collections.sort(processors, (p1, p2) -> Integer.compare(p1.getIndex(), p2.getIndex()));
		}
		//
		return processorsFiltered;
	}

	public static String getDefaultIcon(IProcessSupplier<?> processSupplier) {

		String imageFileName = PROCESSOR_IMAGE_DEFAULT;
		/*
		 * Quick-Access Toolbar default icons.
		 * -------------
		 * This is a quick solution to assign specific process type icons.
		 * In a further version, the process supplier itself may define their specific symbol.
		 * To implement this feature, the platform needs to be reviewed thoroughly, hence
		 * assigning the specific icons here is a compromise.
		 */
		if(processSupplier.getCategory().equals("Baseline Detector")) {
			imageFileName = IApplicationImage.IMAGE_BASELINE;
			if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_BASELINE_SNIP;
			} else if(processSupplier.getName().contains("Delete")) {
				imageFileName = IApplicationImage.IMAGE_BASELINE_DELETE;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Calculator")) {
			imageFileName = IApplicationImage.IMAGE_CALCULATE;
			if(processSupplier.getName().contains("Retention Index Calculator")) {
				imageFileName = IApplicationImage.IMAGE_RETENION_INDEX;
			} else if(processSupplier.getName().contains("Reset")) {
				imageFileName = IApplicationImage.IMAGE_RESET;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Classifier")) {
			imageFileName = IApplicationImage.IMAGE_CLASSIFIER;
			if(processSupplier.getName().contains("WNC")) {
				imageFileName = IApplicationImage.IMAGE_CLASSIFIER_WNC;
			} else if(processSupplier.getName().contains("Durbin-Watson")) {
				imageFileName = IApplicationImage.IMAGE_CLASSIFIER_DW;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Export")) {
			imageFileName = IApplicationImage.IMAGE_SAVE;
			if(processSupplier.getName().contains("*.CAL")) {
				imageFileName = IApplicationImage.IMAGE_SAMPLE_CALIBRATION;
			} else if(processSupplier.getName().contains("Excel")) {
				imageFileName = IApplicationImage.IMAGE_EXCEL_DOCUMENT;
			} else if(processSupplier.getName().contains("ZIP")) {
				imageFileName = IApplicationImage.IMAGE_ZIP_FILE;
			} else if(processSupplier.getName().contains("mzML") || processSupplier.getName().contains("mzXML") || processSupplier.getName().contains("mzData") || processSupplier.getName().contains("AnIML") || processSupplier.getName().contains("GAML")) {
				imageFileName = IApplicationImage.IMAGE_XML_FILE;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Filter")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM;
			if(processSupplier.getName().contains("(1:1)")) {
				imageFileName = IApplicationImage.IMAGE_RESET_EQUAL;
			} else if(processSupplier.getName().contains("CODA")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_CODA;
			} else if(processSupplier.getName().contains("Backfolding")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_BACKFOLDING;
			} else if(processSupplier.getName().contains("Denoising")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_DENOISING;
			} else if(processSupplier.getName().contains("Mean Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_MEAN_NORMALIZER;
			} else if(processSupplier.getName().contains("Median Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_MEDIAN_NORMALIZER;
			} else if(processSupplier.getName().equals("Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_NORMALIZER;
			} else if(processSupplier.getName().contains("Savitzky-Golay")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SAVITZKY_GOLAY;
			} else if(processSupplier.getName().contains("Scan Remover")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SCANREMOVER;
			} else if(processSupplier.getName().contains("Subtract")) {
				imageFileName = IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Integrator")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM_INTEGRATOR;
			if(processSupplier.getName().contains("Sumarea")) {
				imageFileName = IApplicationImage.IMAGE_INTEGRATOR_SUMAREA;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Reports")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM_REPORT;
		} else if(processSupplier.getCategory().equals("External Programs")) {
			if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST;
			}
		} else if(processSupplier.getCategory().equals("Combined Chromatogram and Peak Integrator")) {
			imageFileName = IApplicationImage.IMAGE_COMBINED_INTEGRATOR;
		} else if(processSupplier.getCategory().equals("Peak Detector")) {
			imageFileName = IApplicationImage.IMAGE_PEAK_DETECTOR;
			if(processSupplier.getName().contains("AMDIS") || processSupplier.getName().contains("MCR-AR")) {
				imageFileName = IApplicationImage.IMAGE_DECONVOLUTION;
			}
		} else if(processSupplier.getCategory().equals("Peak Export")) {
			imageFileName = IApplicationImage.IMAGE_EXPORT;
		} else if(processSupplier.getCategory().equals("Peak Filter")) {
			imageFileName = IApplicationImage.IMAGE_PEAKS;
			if(processSupplier.getName().contains("Add")) {
				imageFileName = IApplicationImage.IMAGE_ADD;
			} else if(processSupplier.getName().contains("Remove")) {
				imageFileName = IApplicationImage.IMAGE_REMOVE;
			} else if(processSupplier.getName().contains("Delete")) {
				imageFileName = IApplicationImage.IMAGE_DELETE;
			}
			if(processSupplier.getName().contains("Delete Peak")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAKS;
			} else if(processSupplier.getName().contains("Delete Integration")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAK_INTEGRATIONS;
			} else if(processSupplier.getName().contains("Delete Target")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAK_IDENTIFICATIONS;
			} else if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SNIP_ALL_PEAKS;
			}
		} else if(processSupplier.getCategory().equals("Peak Identifier")) {
			imageFileName = IApplicationImage.IMAGE_IDENTIFY_PEAKS;
			if(processSupplier.getName().equals("Library File (MS)")) {
				imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM_LIBRARY;
			} else if(processSupplier.getName().equals("Unknown Marker")) {
				imageFileName = IApplicationImage.IMAGE_UNKNOWN;
			} else if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST_PEAKS;
			}
		} else if(processSupplier.getCategory().equals("Peak Integrator")) {
			imageFileName = IApplicationImage.IMAGE_PEAK_INTEGRATOR;
			if(processSupplier.getName().contains("Peak Max")) {
				imageFileName = IApplicationImage.IMAGE_PEAK_INTEGRATOR_MAX;
			}
		} else if(processSupplier.getCategory().equals("Peak Quantifier")) {
			imageFileName = IApplicationImage.IMAGE_QUANTIFY_ALL_PEAKS;
			if(processSupplier.getName().contains("ISTD")) {
				imageFileName = IApplicationImage.IMAGE_INTERNAL_STANDARDS_DEFAULT;
			} else if(processSupplier.getName().contains("ESTD")) {
				imageFileName = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_DEFAULT;
			}
			if(processSupplier.getName().contains("Add Peaks to DB")) {
				imageFileName = IApplicationImage.IMAGE_ADD_PEAKS_TO_QUANTITATION_TABLE;
			}
		} else if(processSupplier.getCategory().equals("Peak Massspectrum Filter") || processSupplier.getCategory().equals("Scan Massspectrum Filter")) {
			imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM;
			if(processSupplier.getName().contains("Savitzky-Golay")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SAVITZKY_GOLAY;
			} else if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SNIP_ALL_PEAKS;
			} else if(processSupplier.getName().contains("Subtract")) {
				imageFileName = IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT;
			}
		} else if(processSupplier.getCategory().equals("Scan Identifier")) {
			imageFileName = IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM;
			if(processSupplier.getName().equals("Library File (MS)")) {
				imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM_LIBRARY;
			} else if(processSupplier.getName().equals("Unknown Marker")) {
				imageFileName = IApplicationImage.IMAGE_UNKNOWN;
			} else if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST_MASS_SPECTRUM;
			}
		} else if(processSupplier.getCategory().equals("System")) {
			imageFileName = IApplicationImage.IMAGE_PREFERENCES;
		}
		//
		return imageFileName;
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
					updateProcessorIndices(processors);
				}
			}
		}
	}

	public static List<Processor> sortProcessorsByIndex(List<Processor> processors) {

		Collections.sort(processors, (p1, p2) -> Integer.compare(p1.getIndex(), p2.getIndex()));
		return processors;
	}

	private static Processor getProcessor(List<Processor> processors, String id) {

		for(Processor processor : processors) {
			if(processor.getProcessSupplier().getId().equals(id)) {
				return processor;
			}
		}
		//
		return null;
	}

	/*
	 * Assign the processor indices.
	 */
	private static void updateProcessorIndices(List<Processor> processors) {

		for(int i = 0; i < processors.size(); i++) {
			Processor processor = processors.get(i);
			processor.setIndex(i);
		}
	}

	private static boolean isValid(Processor processor) {

		if(processor == null) {
			return false;
		}
		//
		IProcessSupplier<?> processSupplier = processor.getProcessSupplier();
		String id = processSupplier.getId();
		if(id.contains(VALUE_DELIMITER) || id.contains(PROCESSOR_DELIMITER)) {
			return false;
		}
		//
		String imageFileName = processor.getImageFileName();
		if(imageFileName.contains(VALUE_DELIMITER) || imageFileName.contains(PROCESSOR_DELIMITER)) {
			return false;
		}
		//
		return true;
	}

	private static String getString(String[] values, int index, String def) {

		return valueParseSupport.parseString(values, index, def);
	}

	private static boolean getBoolean(String[] values, int index, boolean def) {

		return valueParseSupport.parseBoolean(values, index, def);
	}

	private static int getInteger(String[] values, int index, int def) {

		return valueParseSupport.parseInteger(values, index, def);
	}
}