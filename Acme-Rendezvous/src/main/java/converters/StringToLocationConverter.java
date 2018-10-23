
package converters;

import java.net.URLDecoder;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Location;

@Component
@Transactional
public class StringToLocationConverter implements Converter<String, Location> {

	@Override
	public Location convert(final String text) {
		Location result = null;
		String parts[];

		if (text == null)
			result = null;
		else if (text == "")
			result = null;
		else
			try {
				parts = text.split("\\,");
				result = new Location();
				result.setLatitude(Double.valueOf(URLDecoder.decode(parts[0], "UTF-8")));
				result.setLongitude(Double.valueOf(URLDecoder.decode(parts[1], "UTF-8")));
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			}

		return result;
	}

}
