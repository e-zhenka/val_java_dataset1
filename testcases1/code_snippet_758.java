@RequestMapping("/session")
    public String session(Model model, @RequestParam String clientId, @RequestParam String messageOrigin) {
        model.addAttribute("clientId", escape(clientId));
        model.addAttribute("messageOrigin", escape(messageOrigin));
        return "session";
    }