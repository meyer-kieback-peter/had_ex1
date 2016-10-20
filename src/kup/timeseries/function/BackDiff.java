/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie k�nnen diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * �Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland�
 * in Version 3.0 (abgek�rzt �CC-by-sa 3.0/de�) ver�ffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverst�ndlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielf�ltigen, zu verbreiten und �ffentlich zug�nglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie m�ssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage f�r ein neues Werk verwenden, d�rfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie m�ssen anderen alle Lizenzbedingungen mitteilen, die f�r dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschr�nken k�nnen.
 * 
 */

package kup.timeseries.function;

import java.util.Date;

import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;

public final class BackDiff extends FunctionBase implements Function {
	private final long timeMillis;

	public static final long MSECS_PER_SEC = 1000;
	public static final long MSECS_PER_MIN = 60 * MSECS_PER_SEC;
	public static final long MSECS_PER_HOUR = 60 * MSECS_PER_MIN;
	public static final long MSECS_PER_DAY = 24 * MSECS_PER_HOUR;
	
	public BackDiff(TimeSeries input, long timeMillis) {
		this.timeMillis = timeMillis;
		setInput(input);
	}
	
	String describeArgs() { return String.valueOf(timeMillis); }

	DataPoint computeNext(DataPoint in) {
		DataPoint back = input.interpolate(new Date(in.date().getTime() - timeMillis));
		return new DataPoint(in.date(), in.value() - back.value());
	}
}
