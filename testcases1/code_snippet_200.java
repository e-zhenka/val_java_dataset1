@RequestMapping("/session")
    public String session(Model model, @RequestParam String clientId, @RequestParam String messageOrigin) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("messageOrigin", messageOrigin);
        return "session";
    }