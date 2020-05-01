/**
 * 
 */
package com.sivalabs.jcart.admin.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.jcart.admin.security.AuthenticatedUser;
import com.sivalabs.jcart.admin.security.SecurityUtil;
import com.sivalabs.jcart.admin.web.dao.CreditDetailsHome;
import com.sivalabs.jcart.admin.web.dao.DebitDetailsHome;
import com.sivalabs.jcart.admin.web.dao.UserDefinedClientsHome;
import com.sivalabs.jcart.admin.web.dao.UserDefinedSourcesHome;
import com.sivalabs.jcart.admin.web.models.CreditDetails;
import com.sivalabs.jcart.admin.web.models.UserDefinedClients;
import com.sivalabs.jcart.admin.web.models.UserDefinedSources;
import com.sivalabs.jcart.entities.Role;
import com.sivalabs.jcart.entities.User;

/**
 * @author Siva
 *
 */
@Controller
@Secured(SecurityUtil.MANAGE_CATEGORIES)
public class CreditManagementController extends JCartAdminBaseController
{
	private static final String viewPrefix = "credit_management/";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired CreditDetailsHome creditDetailsHome;
	
	@Autowired UserDefinedSourcesHome userDefinedSourcesHome;
	
	@Autowired UserDefinedClientsHome userDefinedClilentsHome;
	
	
	@Override
	protected String getHeaderTitle()
	{
		return "Manage Credit Transaction";
	}
	
	@RequestMapping(value="/credit_management", method=RequestMethod.GET)
	public String listCategories(Model model) throws JsonProcessingException {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		Role role = currentUser.getRoles().get(0);
		List<UserDefinedClients> userDefinedClientList = userDefinedClilentsHome.getAllClientsByAccountIdAndType(currentUser.getAccountId(),1,3);
		List<CreditDetails> list = null;
		if (role.getId() == 3) {
			list = creditDetailsHome.getAllCreditTransactionDetailsByUserId(currentUser.getId());
			model.addAttribute("creditList",list);
		} else {
			list = creditDetailsHome.getAllCreditTransactionDetails(currentUser.getAccountId());
			model.addAttribute("creditList",list);
		}
		String creditJson = null;
		if(list!= null) {
			Map<Integer, List<CreditDetails>> map = list.stream().collect(Collectors.groupingBy(CreditDetails::getCrTransactionId));
			creditJson = mapper.writeValueAsString(map);
		}
		model.addAttribute("creditJson", creditJson);
		logger.info("List got for credit details {} with clients {}  ", list, userDefinedClientList);
		return viewPrefix+"credit_management";
	}
	
	@RequestMapping(value="/credit_management/new", method=RequestMethod.GET)
	public String createCategoryForm(Model model) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		List<UserDefinedSources> sources = userDefinedSourcesHome.getAllSourcesByAccountId(currentUser.getAccountId());
		List<UserDefinedClients> userDefinedClientList = userDefinedClilentsHome.getAllClientsByAccountIdAndType(currentUser.getAccountId(),1,3);
		CreditDetails category = new CreditDetails();
		model.addAttribute("udClientList",userDefinedClientList);
		model.addAttribute("sourceList", sources);
		model.addAttribute("category",category);
		return viewPrefix+"create_credit_transaction";
	}

	@RequestMapping(value="/credit_management", method=RequestMethod.POST)
	public String createCategory(@Valid @ModelAttribute("credit_transaction") CreditDetails creditDetails, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		
		if(result.hasErrors()){
			return viewPrefix+"create_credit_transaction";
		}
		creditDetails.setCrTransactionUserId(currentUser.getId());
		creditDetails.setCrTransactionAccountId(currentUser.getAccountId());
		logger.info("credit transaction information got {} ", creditDetails);
		creditDetailsHome.persist(creditDetails);
		redirectAttributes.addFlashAttribute("info", "Category created successfully");
		return "redirect:/credit_management/new";
	}
	
	@RequestMapping(value="/credit_management/{id}", method=RequestMethod.GET)
	public String editCategoryForm(@PathVariable Integer id, Model model) {
		/*Category category = catalogService.getCategoryById(id);
		model.addAttribute("category",category);*/
		return viewPrefix+"edit_credit_transaction";
	}
	
/*	@RequestMapping(value="/credit_management/{id}", method=RequestMethod.POST)
	public String updateCategory(Category category, Model model, RedirectAttributes redirectAttributes) {
	`	Category persistedCategory = catalogService.updateCategory(category);
		logger.debug("Updated category with id : {} and name : {}", persistedCategory.getId(), persistedCategory.getName());
		redirectAttributes.addFlashAttribute("info", "Category updated successfully");
		return "redirect:/credit_management";
	}*/
	
	@RequestMapping(value="/passCreditTransaction/{id}", method=RequestMethod.GET)
	public String passCreditTransaction(@PathVariable Integer id, Model model) {
		User currentUser = ((AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		creditDetailsHome.changeCreditTransactionStatus(currentUser.getAccountId(), id , true);
		return "redirect:/credit_management";
	}
	
	
}
