package com.labanta.servidorlocal.controller;

import com.labanta.servidorlocal.dto.ServicoResponseDTO;
import com.labanta.servidorlocal.model.Servico;
import com.labanta.servidorlocal.service.EmailService;
import com.labanta.servidorlocal.service.ExchangeService;
import com.labanta.servidorlocal.service.FileStorageService;
import com.labanta.servidorlocal.service.ServicoService;
import com.sun.jdi.PrimitiveValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.Multipart;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ServicoController {
    private ServicoService servicoService;
    private final ExchangeService exchangeService;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;


    public ServicoController(
            ServicoService servicoService,
            ExchangeService exchangeService,
            EmailService emailService,
            FileStorageService fileStorageService
    ) {
        this.servicoService = servicoService;
        this.exchangeService = exchangeService;
        this.emailService = emailService;
        this.fileStorageService = fileStorageService;

    }

    @Operation(
            summary = "Listar todos os servicos",
            description = "Rota para listar todos os servicos existentes na plataforma"
    )

    @GetMapping("/servicos")
    public Page<Servico> listarServicos(
            @ParameterObject
            @PageableDefault(page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC) Pageable pageable)
 {
        return  servicoService.servicoFindAll( pageable);
    }

    @Operation(
            summary = "criar um novo servico",
            description = "Rota para criar um novo aervico"
    )
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/servicos")
    public Servico criarServico(@RequestBody Servico servico){
        return servicoService.saveServico(servico);
    }

    @GetMapping("/{id}")
    public Servico obterServicoPorID(@PathVariable Long id){
        return servicoService.buscarServicoPorID(id);
    }

    @PostMapping("/aplicar-desconto")
    public List<ServicoResponseDTO> aplicarDesconto(@RequestBody double desconto){
        List<Servico> lista = servicoService.aplicarDescontoEmAtivos(desconto);
        List<ServicoResponseDTO> listaComPrecoFinal = new ArrayList<>();

        for (Servico s: lista){
            ServicoResponseDTO servicoResponseDTO = new ServicoResponseDTO(s.getTitulo(), s.getPrecoComDesconto());
            listaComPrecoFinal.add(servicoResponseDTO);
        }
        return listaComPrecoFinal;

    }
    @PostMapping("/{id}/orcamento")
    public  String pedirOrcamento(@PathVariable Long id,
                                  @RequestParam String emailDestino,
                                  @RequestParam(defaultValue = "CVE") String moeda
    ){
        //1. Ir a BASE de DADOS buscar o Servico
        Servico servico = servicoService.buscarServicoPorID(id);

        //2. Ir a Internet converter o preco (Aula 16)
        Double precoConvertido = exchangeService.converterPreco(servico.getPreco(),moeda);

        //3. Enviar o resultado para o Gmail do cliente (Aula 15)
        emailService.enviarOrcamentoPorEmail(emailDestino, servico.getTitulo(), precoConvertido, moeda);
        return "Orcamento calculado e enviado com sucesso para " + emailDestino + "!";
    }

    @GetMapping("/pesquisa")
    public List<Servico> buscarServico(@RequestParam String termo){
        return servicoService.buscarServicoPeloTitulo(termo);
    }


    @Operation(
            summary = "Carregar capa de servico",
            description = "rota para carregar capas de servico com base no ID"
    )
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/{id}/upload-capa",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestParam ("file")MultipartFile file,
            @PathVariable Long id

    ){
     Servico servico = servicoService.buscarServicoPorID(id);

     String fileUploaded =fileStorageService.storeImage(file);

     servico.setImageCapa(fileUploaded);
     servicoService.saveServico(servico);


     return ResponseEntity.ok("Ficheiro Carregado com sucesso" + fileUploaded);
    }

}
