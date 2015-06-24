package com.arsdigita.xml.formatters;


import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

public final class DateFormatterConfig extends AbstractConfig {

	private final Parameter m_locale;
	
	public DateFormatterConfig() {
		m_locale = new StringParameter("waf.xml.formatters.locale", Parameter.OPTIONAL, null);
		register(m_locale);
		loadInfo();
	}

	public final String getLocale() {
		return (String) get (m_locale);
	}
}
