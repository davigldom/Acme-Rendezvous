/*
 * WelcomeController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/welcome")
public class WelcomeController extends AbstractController {

	// Constructors -----------------------------------------------------------

	public WelcomeController() {
		super();
	}

	// Index ------------------------------------------------------------------		

	@RequestMapping(value = "/index")
	public ModelAndView index() {
		ModelAndView result;
		SimpleDateFormat formatter;
		String moment;

		final String businessName = this.systemConfigService.findConfig().getBusinessName();
		final Locale locale = LocaleContextHolder.getLocale();
		final String language = locale.getLanguage();
		String phrase = "";
		String businessNameWelcome = "";

		if (language == "es") {
			phrase = this.systemConfigService.findConfig().getWelcomeMessageEs();
			businessNameWelcome += "Bienvenido/a a ";
		} else if (language == "en") {
			phrase = this.systemConfigService.findConfig().getWelcomeMessage();
			businessNameWelcome += "Welcome to ";
		}

		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		moment = formatter.format(new Date());

		result = new ModelAndView("welcome/index");
		result.addObject("businessNameWelcome", businessNameWelcome + businessName);
		result.addObject("moment", moment);
		result.addObject("phrase", phrase);

		return result;
	}
}
