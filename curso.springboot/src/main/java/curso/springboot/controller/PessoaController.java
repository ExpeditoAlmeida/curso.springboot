package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {

		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		// BindingResult objeto usado para retorna a mensagem de validação da respectiva
		// classe...
		if (bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			andView.addObject("pessoas", pessoasIt);
			andView.addObject("pessoaobj", pessoa); // pessoa (retorna o objeto vindo da view)

			List<String> msg = new ArrayList<String>();
			for (ObjectError error : bindingResult.getAllErrors()) {
				msg.add(error.getDefaultMessage()); // getDefaultMessage() retorna as mensagem de validação da
													// respectiva classe.
			}

			andView.addObject("msg", msg);
			return andView;
		}

		pessoaRepository.save(pessoa);
		//////////////// LIstar//////////////////////
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);

		////////////////////////////////////
		// objeto vazio para o form trabalhar
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		// objeto vazio para o form trabalhar
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}

	// "/editarpessoa/{idpessoa} url é paramentro da tela" //anotação @GetMapping
	// substitui @RequestMapping(method = RequestMethod.GET
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {

		// carrega o objeto do banco
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		// prepara o retorno para a tela designada
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		andView.addObject("pessoaobj", pessoa.get());

		return andView;
	}

	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {

		pessoaRepository.deleteById(idpessoa);
		// prepara o retorno para a tela designada
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		andView.addObject("pessoas", pessoaRepository.findAll());
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}

	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findPessoaBynome(nomepesquisa));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}

	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {

		// carrega o objeto do banco
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		// prepara o retorno para a tela designada
		ModelAndView andView = new ModelAndView("cadastro/telefones");

		andView.addObject("pessoaobj", pessoa.get());
		// ação para carregar a lista de telefone da respectiva pessoa
		andView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return andView;
	}

	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();

		// Validação para telefone na camada do backend
		if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {

			ModelAndView andView = new ModelAndView("cadastro/telefones");
			andView.addObject("pessoaobj", pessoa);
			andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

			List<String> msg = new ArrayList<String>();
			if (telefone.getNumero().isEmpty()) {
				msg.add("Numero deve ser informado");
			}

			if (telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado");
			}

			andView.addObject("msg", msg);
			return andView;

		}

		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);

		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return andView;

	}

	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {

		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();

		telefoneRepository.deleteById(idtelefone);

		ModelAndView andView = new ModelAndView("cadastro/telefones");

		andView.addObject("pessoas", pessoaRepository.findAll());
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return andView;
	}
}
