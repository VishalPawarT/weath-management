package com.sivalabs.jcart.admin.web.controllers;


import java.util.List;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sivalabs.jcart.admin.security.AuthenticatedUser;
import com.sivalabs.jcart.admin.security.SecurityUtil;
import com.sivalabs.jcart.admin.web.dao.CreditDetailsHome;
import com.sivalabs.jcart.admin.web.dao.DebitDetailsHome;
import com.sivalabs.jcart.admin.web.dao.UserDefinedSourcesHome;
import com.sivalabs.jcart.admin.web.models.CreditDetails;
import com.sivalabs.jcart.admin.web.models.DebitDetails;
import com.sivalabs.jcart.admin.web.models.UserDefinedSources;
import com.sivalabs.jcart.entities.Role;
import com.sivalabs.jcart.entities.User;

/**
 * @author Siva
 *
 */
@Controller
@Secured(SecurityUtil.MANAGE_CATEGORIES)
public class DebitManagementController extends JCartAdminBaseController
{
	private static final String viewPrefix = "debit_management/";
	
	
	@Autowired DebitDetailsHome debitDetailsHome;
	
	@Autowired UserDefinedSourcesHome userDefinedSourcesHome;
	
	@Override
	protected String getHeaderTitle()
	{
		return "Manage Credit Transaction";
	}
	
	@RequestMapping(value="/debit_management", method=RequestMethod.GET)
	public String listCategories(Model model) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		Role role = currentUser.getRoles().get(0);
		if (role.getId() == 1) {
		List<DebitDetails> list = debitDetailsHome.getAllDebitTransactionDetails(currentUser.getAccountId());
		logger.info("List got for debit details {} ", list);
		model.addAttribute("debitList",list);
		}
		return viewPrefix+"debit_management";
	}
	
	@RequestMapping(value="/debit_management/new", method=RequestMethod.GET)
	public String createCategoryForm(Model model) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		DebitDetails category = new DebitDetails();
		List<UserDefinedSources> sources = userDefinedSourcesHome.getAllSourcesByAccountId(currentUser.getAccountId());
		model.addAttribute("sourceList", sources);
		model.addAttribute("debitDetails",category);
		return viewPrefix+"create_debit_transaction";
	}

	@RequestMapping(value="/debit_management", method=RequestMethod.POST)
	public String createCategory(@Valid @ModelAttribute("debitDetails") DebitDetails debitDetails, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		
		if(result.hasErrors()){
			return viewPrefix+"create_debit_transaction";
		}
		debitDetails.setDebTransactionUserId(currentUser.getId());
		debitDetails.setDebTransactionAccountId(currentUser.getAccountId());
		logger.info("credit transaction information got {} ", debitDetails);
		debitDetailsHome.persist(debitDetails);
		redirectAttributes.addFlashAttribute("info", "Category created successfully");
		return "redirect:/debit_management/new";
	}
	
	@RequestMapping(value="/debit_management/{id}", method=RequestMethod.GET)
	public String editCategoryForm(@PathVariable Integer id, Model model) {
		/*Category category = catalogService.getCategoryById(id);
		model.addAttribute("category",category);*/
		return viewPrefix+"edit_debit_transaction";
	}
	
/*	@RequestMapping(value="/debit_management/{id}", method=RequestMethod.POST)
	public String updateCategory(Category category, Model model, RedirectAttributes redirectAttributes) {
	`	Category persistedCategory = catalogService.updateCategory(category);
		logger.debug("Updated category with id : {} and name : {}", persistedCategory.getId(), persistedCategory.getName());
		redirectAttributes.addFlashAttribute("info", "Category updated successfully");
		return "redirect:/debit_management";
	}*/

}
