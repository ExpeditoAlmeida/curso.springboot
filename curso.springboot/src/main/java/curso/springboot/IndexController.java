package curso.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/") // barra para procurra pelo index semelhate a servlet
	public String index() {
		return "index";
	}
}
