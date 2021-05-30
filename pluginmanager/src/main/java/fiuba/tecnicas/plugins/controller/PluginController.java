package fiuba.tecnicas.plugins.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/plugins")
public class PluginController {
	@GetMapping("/test")
	public String test() {
		return "Funciona";
	}
}
