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

import java.util.Date;

import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;

public final class FakeBoolLine extends FunctionBase implements Function {
	private final float limit;

	public FakeBoolLine(TimeSeries input, float limit) {
		this.limit = limit;
		setInput(input);
	}
	
	String describeArgs() { return String.valueOf(limit); }
	
	
	private DataPoint toBool(DataPoint dp) {
		if (dp.value() > limit) return new DataPoint(dp.date(), 1.0f);
		else return new DataPoint(dp.date(), 0.0f);
	}
	
	public TimeSeries compute(String name) {
		TimeSeries ret = new TimeSeries(name);
		if (input.size() > 0) {
			DataPoint last = toBool(input.getDataPoint(0));
			try {
				ret.addDataPoint(last);
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
			for (int i = 1; i< input.size(); i++) {
				DataPoint dp = toBool(input.getDataPoint(i));
				if (Math.abs(last.value() - dp.value()) > 0.0f) {
					try {
						ret.addDataPoint(new DataPoint(new Date(dp.date().getTime() - 1), last.value()));
						ret.addDataPoint(dp);
					} catch (TimeSeriesException e) {
						e.printStackTrace();
					}
					last = dp;
				}
			}
		}
		return ret;
	}

}
