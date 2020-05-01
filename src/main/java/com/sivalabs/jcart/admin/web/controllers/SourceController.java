package com.sivalabs.jcart.admin.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sivalabs.jcart.JCartException;
import com.sivalabs.jcart.admin.security.AuthenticatedUser;
import com.sivalabs.jcart.admin.security.SecurityUtil;
import com.sivalabs.jcart.admin.web.dao.UserDefinedSourcesHome;
import com.sivalabs.jcart.admin.web.models.CreditDetails;
import com.sivalabs.jcart.admin.web.models.UserDefinedSources;
import com.sivalabs.jcart.common.services.EmailService;
import com.sivalabs.jcart.entities.User;

/**
 * @author Siva
 *
 */
@Controller
@Secured(SecurityUtil.MANAGE_ORDERS)
public class SourceController extends JCartAdminBaseController
{
	private static final String viewPrefix = "sources/";

	@Autowired protected EmailService emailService;
	@Autowired private TemplateEngine templateEngine;
	
	@Autowired private UserDefinedSourcesHome userSourceHome;
	
	@Override
	protected String getHeaderTitle()
	{
		return "Manage Orders";
	}
	
	
	@RequestMapping(value="/sources", method=RequestMethod.GET)
	public String listOrders(Model model) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		List<UserDefinedSources> sourceList = userSourceHome.getAllSourcesByAccountId(currentUser.getAccountId());	
		model.addAttribute("sourceList", sourceList);
		return viewPrefix+"sources";
	}
	
	@RequestMapping(value="/sources/new", method=RequestMethod.GET)
	public String createCategoryForm(Model model) {
		UserDefinedSources source = new UserDefinedSources();
		model.addAttribute("source", source);
		return viewPrefix+"create_new_source";
	}
	
	@RequestMapping(value="/sources", method=RequestMethod.POST)
	public String createCategory(@Valid @ModelAttribute("source") UserDefinedSources source, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		if(result.hasErrors()){
			return viewPrefix+"create_source";
		}
		source.setAccountId(currentUser.getAccountId());
		source.setUserId(currentUser.getId());
		logger.info("new source information got {} ", source);
		userSourceHome.persist(source);
		redirectAttributes.addFlashAttribute("info", "Category created successfully");
		return "redirect:/sources";
	}
	
}