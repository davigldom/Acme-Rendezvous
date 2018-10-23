
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Request;

@Component
@Transactional
public class RequestToStringConverter implements Converter<Request, String> {

	@Override
	public String convert(final Request request) {
		String result;
		if (request == null)
			result = null;
		else
			result = String.valueOf(request.getId());
		return result;
	}

}
