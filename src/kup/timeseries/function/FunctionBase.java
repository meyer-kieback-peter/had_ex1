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

import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;
import kup.timeseries.TimeSeriesProducer;

abstract class FunctionBase implements Function, TimeSeriesProducer {
	TimeSeries input = null;
	TimeSeries result = null;

	public final void setInput(TimeSeries input) {
		this.input = input;
		//System.out.println("FunctionBase.setInput() " + this);
	}

	String describeArgs() { return ""; }
	
	public final String describe() {
		String s = describeArgs();
		String args = s != "" ? " , " + s : s;
		return this.getClass().getSimpleName() + "( " + (null == input ? "?" : input.name()) + args + " )";
	}
	
	public String toString() {
		return "Function " + describe(); // + "\n input " + input.toString();
	}
	
	DataPoint computeNext(DataPoint in) {
		return in;
	}
	
	public TimeSeries compute(String name) {
		TimeSeries ret = new TimeSeries(name);
		for (int i = 0; i<input.size(); i++) {
			DataPoint dp = computeNext(input.getDataPoint(i));
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

	public TimeSeries compute() {
		return compute(describe());
	}
}
