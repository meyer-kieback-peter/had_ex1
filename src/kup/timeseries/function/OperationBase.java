/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie können diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * „Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland“
 * in Version 3.0 (abgekürzt „CC-by-sa 3.0/de“) veröffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverständlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielfältigen, zu verbreiten und öffentlich zugänglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie müssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage für ein neues Werk verwenden, dürfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie müssen anderen alle Lizenzbedingungen mitteilen, die für dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschränken können.
 * 
 */

package kup.timeseries.function;

import kup.timeseries.CrossSampleException;
import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;
import kup.timeseries.TimeSeriesProducer;

abstract class OperationBase implements Operation, TimeSeriesProducer {
	TimeSeries input1 = null;
	TimeSeries input2 = null;
	TimeSeries result = null;

	public final void setInputs(TimeSeries input1, TimeSeries input2) {
		this.input1 = input1;
		this.input2 = input2;
	}

	public final String describe() {
		return this.getClass().getSimpleName() + "( " + (null == input1 ? "?" : input1.name()) + " , " + (null == input2 ? "?" : input2.name()) + " )";
	}
	
	public String toString() {
		return "Operation " + this.getClass().getName(); // + "\n input1 " + input1.toString() + "\n input2 " + input2.toString();
	}
	
	DataPoint computeNext(DataPoint in1, DataPoint in2) {
		return null;
	}

	public TimeSeries compute(String name) throws CrossSampleException {
		TimeSeries ret = new TimeSeries(name);
		if (input1.size() != input2.size()) {
			throw new CrossSampleException("size mismatch");
		}
		for (int i= 0; i<input1.size(); i++) {
			DataPoint dp1 = input1.getDataPoint(i);
			DataPoint dp2 = input2.getDataPoint(i);
			if (!dp1.date().equals(dp2.date())) {
				throw new CrossSampleException("missing cross sample");
			}
			DataPoint dp = computeNext(dp1, dp2);
			if (null != dp) {
				try {
					ret.addDataPoint(dp);
				} catch (TimeSeriesException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	public TimeSeries compute() throws CrossSampleException {
		return compute(describe());
	}
}
