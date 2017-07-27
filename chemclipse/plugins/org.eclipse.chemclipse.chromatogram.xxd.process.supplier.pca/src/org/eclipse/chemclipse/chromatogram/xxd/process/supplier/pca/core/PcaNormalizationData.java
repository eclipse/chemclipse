/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;

public class PcaNormalizationData {

	public enum Centering {
		MEAN, MEDIAN;
	}

	public enum Normalization {
		CENTERING, SCALING_AUTO, SCALING_LEVEL, SCALING_MAXIMUM, SCALING_PARETO, SCALING_RANGE, SCALING_VAST, TRANSFORMING
	}

	public enum Transformation {
		LOG10, NONE, POWER
	}

	private Centering centering;
	private Normalization norlamalizationType;
	private Function<Double, Double> transformation;
	private Function<Double, Double> transformationNone = d -> d;
	private Function<Double, Double> transfromationLog10 = d -> 10.0 * Math.log10(Math.abs(d));
	private Function<Double, Double> transfromationPower = d -> Math.sqrt(Math.abs(d));
	private Transformation transfromationType;

	public PcaNormalizationData() {
		this.transformation = transformationNone;
		this.transfromationType = Transformation.NONE;
		centering = Centering.MEAN;
		norlamalizationType = Normalization.SCALING_AUTO;
	}

	private void autoscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double deviation = getStandartDeviation(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(deviation != 0) {
					normData = (data - mean) / deviation;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	private void centerig(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = data - mean;
				d.setNormalizedData(normData);
			});
		}
	}

	public Centering getCentering() {

		return centering;
	}

	private double getCenteringValue(ISample sample) {

		List<ISampleData> sampleData = sample.getSampleData();
		switch(centering) {
			case MEAN:
				return sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getAverage();
			case MEDIAN:
				List<Double> data = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).sorted().boxed().collect(Collectors.toList());
				int lenght = data.size();
				double median = lenght % 2 == 0 ? (data.get(lenght / 2 - 1) + data.get(lenght / 2)) / 2.0 // even
						: data.get(lenght / 2); //
				return median;
			default:
				throw new RuntimeException("undefine centering");
		}
	}

	private double getMax(ISample sample) {

		return sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMax();
	}

	private double getMin(ISample sample) {

		return sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMin();
	}

	public Normalization getNorlamalizationType() {

		return norlamalizationType;
	}

	private double getStandartDeviation(ISample sample) {

		return Math.sqrt(Math.abs(getVariance(sample)));
	}

	public Transformation getTransformationType() {

		return transfromationType;
	}

	private double getVariance(ISample sample) {

		List<ISampleData> sampleData = sample.getSampleData();
		int count = sampleData.size();
		if(count > 1) {
			final double mean = getCenteringValue(sample);
			double sum = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(d -> {
				double data = transformation.apply(d.getData());
				return (data - mean) * (data - mean);
			}).sum();
			return sum / (count - 1);
		}
		return 0;
	}

	private void levelscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(mean != 0) {
					normData = (data - mean) / mean;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	private void maxScaling(IPcaResults pcaResults) {

		pcaResults.getSampleList().forEach(s -> {
			final double mean = getCenteringValue(s);
			final double max = getMax(s);
			s.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(max != 0) {
					normData = (data - mean) / (max);
				}
				d.setNormalizedData(normData);
			});
		});
	}

	public void normalize(IPcaResults pcaResults) {

		switch(norlamalizationType) {
			case SCALING_AUTO:
				autoscaling(pcaResults);
				break;
			case SCALING_LEVEL:
				levelscaling(pcaResults);
				break;
			case SCALING_PARETO:
				paretoScaling(pcaResults);
				break;
			case SCALING_RANGE:
				rangeScaling(pcaResults);
				break;
			case SCALING_VAST:
				vastscaling(pcaResults);
				break;
			case SCALING_MAXIMUM:
				maxScaling(pcaResults);
				break;
			case CENTERING:
				centerig(pcaResults);
				break;
			case TRANSFORMING:
				transforming(pcaResults);
				break;
		}
	}

	private void paretoScaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double deviationSqrt = Math.sqrt(getStandartDeviation(sample));
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(deviationSqrt != 0) {
					normData = (data - mean) / deviationSqrt;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	private void rangeScaling(IPcaResults pcaResults) {

		pcaResults.getSampleList().forEach(s -> {
			final double mean = getCenteringValue(s);
			final double max = getMax(s);
			final double min = getMin(s);
			s.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(max != min) {
					normData = (data - mean) / (max - min);
				}
				d.setNormalizedData(normData);
			});
		});
	}

	public void setCentering(Centering centering) {

		this.centering = centering;
	}

	public void setNorlamalizationType(Normalization norlamalizationType) {

		this.norlamalizationType = norlamalizationType;
	}

	public void setRawData(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = d.getData();
				d.setNormalizedData(data);
			});
		}
	}

	public void setTransformationType(Transformation transformation) {

		switch(transformation) {
			case NONE:
				this.transformation = transformationNone;
				break;
			case LOG10:
				this.transformation = transfromationLog10;
				break;
			case POWER:
				this.transformation = transfromationPower;
				break;
		}
		this.transfromationType = transformation;
	}

	private void transforming(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				d.setNormalizedData(data);
			});
		}
	}

	private void vastscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double variace = getVariance(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = (data - mean) / variace * mean;
				d.setNormalizedData(normData);
			});
		}
	}
}
