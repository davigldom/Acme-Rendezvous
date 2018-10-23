
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.AcmeService;

@Component
@Transactional
public class AcmeServiceToStringConverter implements Converter<AcmeService, String> {

	@Override
	public String convert(final AcmeService acmeService) {
		String result;
		if (acmeService == null)
			result = null;
		else
			result = String.valueOf(acmeService.getId());
		return result;
	}

}
