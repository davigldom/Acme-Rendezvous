
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.AcmeServiceRepository;
import domain.AcmeService;

@Component
@Transactional
public class StringToAcmeServiceConverter implements Converter<String, AcmeService> {

	@Autowired
	AcmeServiceRepository	acmeServiceRepository;


	@Override
	public AcmeService convert(final String text) {
		AcmeService result;
		int id;
		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.acmeServiceRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
