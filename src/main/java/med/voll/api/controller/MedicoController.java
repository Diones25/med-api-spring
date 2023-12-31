package med.voll.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domian.medico.DadosAtualizacaoMedico;
import med.voll.api.domian.medico.DadosCadastroMedico;
import med.voll.api.domian.medico.DadosDetalhamentoMedico;
import med.voll.api.domian.medico.DadosListagemMedico;
import med.voll.api.domian.medico.Medico;
import med.voll.api.domian.medico.MedicoRepository;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

  @Autowired
  private MedicoRepository repository;
  
  @PostMapping()
  @Transactional
  public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uBuilder) {
    var medico = new Medico(dados);
    repository.save(medico);

    var uri = uBuilder.path("/medico/{id}").buildAndExpand(medico.getId()).toUri();
    
    return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
  }

  @GetMapping()
  public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 5, page = 0, sort = {"nome"}) Pageable paginacao) {
    /*
     * Listagem apenas de nome, email, crm, especialidade
     * return repository.findAll().stream().map(DadosListagemMedico::new).toList();
    */
    var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);

    return ResponseEntity.ok(page);
  }

  @GetMapping("/{id}")
  public ResponseEntity detalhar(@PathVariable Long id) {
    var medico = repository.getReferenceById(id);

    return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
  }

  @PutMapping()
  @Transactional
  public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
    var medico = repository.getReferenceById(dados.id());
    medico.atualizarInformacoes(dados);

    return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity excluir(@PathVariable Long id) {
    var medico = repository.getReferenceById(id);
    medico.excluir();

    return ResponseEntity.noContent().build();
  }
}  