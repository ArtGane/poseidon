package com.poseidon.api.controller;

import com.poseidon.api.custom.constantes.BidConstantes;
import com.poseidon.api.custom.exceptions.bid.BidAlreadyExistsException;
import com.poseidon.api.custom.exceptions.bid.BidCreationException;
import com.poseidon.api.custom.exceptions.bid.BidNotFoundException;
import com.poseidon.api.model.Bid;
import com.poseidon.api.repository.BidRepository;
import com.poseidon.api.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;


@Controller
public class BidController {

    @Autowired
    BidService bidService;

    @Autowired
    BidRepository bidRepository;

    @RequestMapping("/bidList/list")
    public String home(Model model) {
        model.addAttribute("bids", bidRepository.findAll());

        return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(Bid bid) {
        return "bidList/add";
    }

    @PostMapping("/bidList/validate")
    public String createBid(@Valid Bid bid, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws BidAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            return "bidList/add";
        }
        try {
            bidService.createBid(bid);
            redirectAttributes.addFlashAttribute("message", String.format(BidConstantes.CREATED_BID_MESSAGE_TEMPLATE, bid.getId()));
            return "redirect:/bidList/list";
        } catch (BidCreationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bidList/add";
        }
    }

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) throws ChangeSetPersister.NotFoundException {
        try {
            Optional<Bid> bidToUpdate = bidRepository.findById(id);
            model.addAttribute("bid", bidToUpdate);
            return "bidList/update";
        } catch (BidNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bidList/list";
        }
    }

    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Long id, @Valid Bid bid,
                            BindingResult result, RedirectAttributes redirectAttributes) throws ChangeSetPersister.NotFoundException {

        if (result.hasErrors()) {
            return "bidList/update";
        }

        try {
            if (bidService.updateBid(id, bid)) {
                redirectAttributes.addFlashAttribute("message", String.format("Bid with id " + id + " was successfully updated"));
                return "redirect:/bidList/list";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", String.format("Failed to update bid with id " + id));
                return "redirect:/bidList/update/" + id;
            }
        } catch (BidNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bidList/list";
        }
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            bidService.deleteBid(id);
            redirectAttributes.addFlashAttribute("message", String.format("Bid with id " + id + " was successfully deleted"));
            model.addAttribute("bids", bidRepository.findAll());
        } catch (BidNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bidList/list";
        }

        return "redirect:/bidList/list";
    }
}
