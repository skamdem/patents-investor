package dev.skamdem.patentsinvestor.controllers;

import dev.skamdem.patentsinvestor.data.TagRepository;
import dev.skamdem.patentsinvestor.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by kamdem
 */
@Controller
@ControllerAdvice
@RequestMapping("tags")
//@SessionAttributes("portfolio")
public class TagController {

    //General messages
    private final String INFO_MESSAGE_KEY = "message";

    //About deleted tags from stocks
    private final String ACTION_MESSAGE_KEY = "secondMessage";

    static final int numberOfItemsPerPage = 10;
    static final String baseColor = "white";
    static final String selectedColor = "green";

    String tagSortCriteria = "";

    @Autowired
    AuthenticationController authenticationController;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PaginatedListingService<Tag> paginatedListingService;

    /**
     * @param model
     * @param page
     * @param size
     * @param sortIcon
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String displayAllTags(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("sortIcon") Optional<String> sortIcon
    ) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(numberOfItemsPerPage);
        tagSortCriteria = sortIcon.orElse(tagSortCriteria);
        String iconsDestinationUrl = "/tags/?sortIcon=";
        String paginationDestinationUrl = "/tags/?size=";

        List<Tag> listOfTagsFound = ((User) model.getAttribute("user")).getPortfolio().getTags();

        if (listOfTagsFound.size() == 0) {
            model.addAttribute(INFO_MESSAGE_KEY, "info|No investment fields were found! Click the 'create new field' button below to create one");
        }

        switch (tagSortCriteria) {
            case "investmentFieldUp":
                listOfTagsFound.sort(Comparators.tagComparator);
                break;
            case "investmentFieldDown":
                listOfTagsFound.sort(Comparators.tagComparator.reversed());
                break;
        }

        paginatedListingService.setListOfItems(listOfTagsFound);
        Page<Tag> tagPage = paginatedListingService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("title", "All investment fields of " +
                ((User) model.getAttribute("user")).getUsername());

        //named stockPage because using the same fragment as that of stocks
        model.addAttribute("stockPage", tagPage);
        model.addAttribute("sortIcon", tagSortCriteria);
        model.addAttribute("baseColor", baseColor);
        model.addAttribute("selectedColor", selectedColor);
        model.addAttribute("iconsDestinationUrl", iconsDestinationUrl);
        model.addAttribute("paginationDestinationUrl", paginationDestinationUrl);

        int totalPages = tagPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            List<String> reducedPagination = paginatedListingService.paginating(currentPage, pageNumbers.size());
            model.addAttribute("pageNumbers", reducedPagination);//pageNumbers);
        }
        return "tags/index";
    }

    /**
     * A Logged in User adds a tag to his own portfolio
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String renderCreateTagForm(
            Model model) {

        model.addAttribute("title", "Create investment field");
        model.addAttribute(INFO_MESSAGE_KEY, "info|'Investments fields' help categorize" +
                " stocks. Keep the 'Field name' short, ideally a single word of less than 10 characters." +
                " You may be more descriptive in the property 'Description of the investment field'.");

        //Filter tags pertaining to this User's portfolio ONLY
        //User loggedInUser = authenticationController.getUserFromSession(request.getSession());
        model.addAttribute("isLoggedIn", true);
        Portfolio portfolio = ((User) model.getAttribute("user")).getPortfolio();
        //System.out.println("A: "+portfolio);
        model.addAttribute(new Tag(portfolio)); //model.addAttribute(new Tag());
        return "tags/create";
    }

    /**
     * A Logged in User adds a tag to his own portfolio
     *
     * @param tag
     * @param errors
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String processCreateTagForm(
            @Valid @ModelAttribute Tag tag,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (errors.hasFieldErrors("name") || errors.hasFieldErrors("description")) {
            model.addAttribute("title", "Create investment field");
            model.addAttribute("isLoggedIn", true);
            model.addAttribute(tag);
            model.addAttribute(ACTION_MESSAGE_KEY, "danger|Failed to create a new investment field");
            return "tags/create";
        }

        Tag newTag = new Tag(
                tag.getName(),
                tag.getDescription(),
                ((User) model.getAttribute("user")).getPortfolio());
        //System.out.println("D: "+ newTag.getPortfolio());
        tagRepository.save(newTag);
        //tagRepository.save(tag);

        redirectAttributes.addFlashAttribute(ACTION_MESSAGE_KEY, "success|New investment field " + tag.getDisplayName() + " created");
        return "redirect:/tags";
    }

    /**
     * A User should ONLY delete his own tags
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public String displayDeleteTagForm(
            Model model) {
        model.addAttribute("title", "Delete investment field");
        List<Tag> deletableTags = new ArrayList<>();

        //Filter tags pertaining to this User's portfolio ONLY
        List<Tag> allTags = ((User) model.getAttribute("user")).getPortfolio().getTags();
        //Iterable<Tag> allTags = tagRepository.findAll();

        for (Tag tag : allTags) {
            if (tag.getStocks().size() == 0) {
                deletableTags.add(tag);
            }
        }
        model.addAttribute("tags", deletableTags);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute(INFO_MESSAGE_KEY, "info|Only investment fields currently not set to any stock are listed. You may not delete any investment field currently tied to some stock");
        return "tags/delete";
    }

    /**
     * @param tagIds
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("delete")
    public String processDeleteTagForm(
            @RequestParam(required = false) int[] tagIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (tagIds != null) {
            for (int id : tagIds) {
                Tag tag = tagRepository.findById(id).get();
                tagRepository.deleteById(id);
            }
            redirectAttributes.addFlashAttribute(ACTION_MESSAGE_KEY, "success|" + tagIds.length + " investment field(s) deleted");
            return "redirect:/tags";
        }
        redirectAttributes.addFlashAttribute(ACTION_MESSAGE_KEY, "info|No investment field deleted");
        return "redirect:/tags";
    }

    /**
     * @param model
     * @param tagId
     * @return
     */
    @RequestMapping(value = "edit/{tagId}", method = RequestMethod.GET)
    public String displayEditForm(
            Model model,
            @PathVariable int tagId) {
        Tag tag = tagRepository.findById(tagId).get();
        String tagName = tag.getDisplayName();
        model.addAttribute("title",
                "Edit investment field " + tagName);
        model.addAttribute("tag", tag);
        if (tag.getStocks().size() > 0) {
            model.addAttribute(INFO_MESSAGE_KEY, "warning|" +
                    tag.getDisplayName() +
                    " is currently set to " +
                    tag.getStocks().size() + " stock(s) that would be affected");
        }
        //System.out.println("A: "+tag.getId());
        model.addAttribute("tagId", tag.getId());
        return "tags/edit";
    }

    /**
     * @param tag
     * @param errors
     * @param model
     * @param tagId
     * @param redirectAttributes
     * @return
     */
    @PostMapping("edit")
    public String processEditForm(
            @Valid @ModelAttribute Tag tag,
            Errors errors,
            Model model,
            int tagId,
            RedirectAttributes redirectAttributes) {
        if (errors.hasFieldErrors("name") || errors.hasFieldErrors("description")) {
            //System.out.println("B: "+tagId);//tag.getId());
            String tagName = tagRepository.findById(tagId).get().getDisplayName();
            model.addAttribute("title",
                    "Edit investment field " + tagName);
            model.addAttribute("tag", tag);
            model.addAttribute("tagId", tagId);
            model.addAttribute("isLoggedIn", true);
            model.addAttribute(tag);
            model.addAttribute(ACTION_MESSAGE_KEY, "danger|Failed to edit the investment field " + tagName);
            return "/tags/edit";
        }

        Optional<Tag> result = tagRepository.findById(tagId);
        Tag editedTag = result.get();
        editedTag.setDescription(tag.getDescription());
        editedTag.setName(tag.getName());
        tagRepository.save(editedTag);
        redirectAttributes.addFlashAttribute(ACTION_MESSAGE_KEY, "success|" + tag.getDisplayName() + " successfully edited");
        return "redirect:/tags";
    }
}
