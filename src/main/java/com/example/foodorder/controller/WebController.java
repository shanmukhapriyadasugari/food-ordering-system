package com.example.foodorder.controller;

import com.example.foodorder.model.MenuItem;
import com.example.foodorder.model.MenuItemRepository;
import com.example.foodorder.model.CartItem;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("cart")
public class WebController {

    private final MenuItemRepository menuItemRepository;

    public WebController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @ModelAttribute("cart")
    public List<CartItem> initializeCart() {
        return new ArrayList<>();
    }

    // Auto-populate default seed items on initial launch if database is empty
    @PostConstruct
    public void initDefaultMenu() {
        if (menuItemRepository.count() == 0) {
            menuItemRepository.save(new MenuItem("Gourmet Cheese Pizza", 12.99, "pizza", "/images/pizza.jpg"));
            menuItemRepository.save(new MenuItem("Classic Crunch Burger", 8.49, "burger", "/images/burger.jpg"));
            menuItemRepository.save(new MenuItem("Velvet Chocolate Cake", 5.99, "dessert", "/images/cake.jpg"));
        }
    }

    @GetMapping("/")
    public String viewHomePage(Model model, @ModelAttribute("cart") List<CartItem> cart) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        
        int totalQuantity = cart.stream().mapToInt(CartItem::getQuantity).sum();
        double totalBill = cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
        
        model.addAttribute("cartCount", totalQuantity);
        model.addAttribute("cartTotal", String.format("%.2f", totalBill));
        return "index";
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("itemName") String itemName, @ModelAttribute("cart") List<CartItem> cart) {
        MenuItem targetItem = menuItemRepository.findAll().stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .findFirst().orElse(null);

        if (targetItem != null) {
            CartItem existingCartItem = cart.stream()
                    .filter(item -> item.getMenuItem().getName().equalsIgnoreCase(itemName))
                    .findFirst().orElse(null);

            if (existingCartItem != null) {
                existingCartItem.incrementQuantity();
            } else {
                cart.add(new CartItem(targetItem, 1));
            }
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String viewCartDetails(Model model, @ModelAttribute("cart") List<CartItem> cart) {
        double subTotal = cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double deliveryFee = subTotal > 0 ? 2.50 : 0.00;
        double grandTotal = subTotal + deliveryFee;

        model.addAttribute("cartItems", cart);
        model.addAttribute("subTotal", String.format("%.2f", subTotal));
        model.addAttribute("deliveryFee", String.format("%.2f", deliveryFee));
        model.addAttribute("grandTotal", String.format("%.2f", grandTotal));
        return "cart";
    }

    // FEATURE 1: Secure Order Finalization & Cart Clearing
    @PostMapping("/place-order")
    public String confirmCheckoutOrder(@ModelAttribute("cart") List<CartItem> cart) {
        cart.clear(); // Wipe active session data out upon placement success
        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String showSuccessView() {
        return "order-success";
    }

    // FEATURE 2: Render Admin Portal UI
    @GetMapping("/admin")
    public String showAdminPanel() {
        return "admin";
    }

    //  FEATURE 3: Process New Item Form Additions
    @PostMapping("/admin/add-item")
    public String createNewFoodItem(@RequestParam("name") String name,
                                    @RequestParam("price") double price,
                                    @RequestParam("category") String category) {
        // Fall back onto standard placeholder image paths for convenience if no image file upload exists
        String imagePlaceholder = "/images/burger.jpg"; 
        if(category.equalsIgnoreCase("pizza")) imagePlaceholder = "/images/pizza.jpg";
        if(category.equalsIgnoreCase("dessert")) imagePlaceholder = "/images/cake.jpg";

        menuItemRepository.save(new MenuItem(name, price, category, imagePlaceholder));
        return "redirect:/";
    }
    //  Deletes an item from the database by its unique ID
    @PostMapping("/admin/delete-item")
    public String deleteFoodItem(@RequestParam("itemId") Long itemId) {
        menuItemRepository.deleteById(itemId);
        return "redirect:/";
    }
}