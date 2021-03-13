package br.com.trojack.pauta.api.v1;

import br.com.trojack.pauta.api.v1.request.AbrirVotacaoPautaRequest;
import br.com.trojack.pauta.api.v1.request.PautaRequest;
import br.com.trojack.pauta.api.v1.request.VotarPautaRequest;
import br.com.trojack.pauta.api.v1.response.PautaResponse;
import br.com.trojack.pauta.api.v1.response.ResultadoPautaResponse;
import br.com.trojack.pauta.api.v1.response.ErroSimplesResponse;
import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.exception.*;
import br.com.trojack.pauta.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/pauta")
public class PautaApi {

    private final PautaService pautaService;
    private final ObjectMapper objectMapper;

    private static final String MENSAGEM_PAUTA_INEXISTENTE = "Pauta inexistente";
    private static final String MENSAGEM_PAUTA_JA_VOTADA = "Esta pauta já foi votada";
    private static final String MENSAGEM_PAUTA_FECHADA = "Esta pauta não está aberta a votação";
    private static final String MENSAGEM_CPF_INAPTO = "CPF inapto para votar";
    private static final String MENSAGEM_CPF_INVALIDO = "CPF inválido";
    private static final String MENSAGEM_VOTO_JA_COMPUTADO = "Voto já computado para este cpf";

    @GetMapping
    ResponseEntity<List<PautaResponse>> obterPautas() {
        List<PautaDto> pautasDto = pautaService.obterPautas();

        return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(objectMapper.convertValue(pautasDto, PautaResponse[].class)));
    }

    @PostMapping
    ResponseEntity<PautaResponse> inserirPauta(@Valid @RequestBody PautaRequest pautaRequest) {
        PautaDto pautaDto = pautaService.inserirPauta(objectMapper.convertValue(pautaRequest, PautaDto.class));

        return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(pautaDto, PautaResponse.class));
    }

    @PutMapping("/abrir-votacao/{id}")
    ResponseEntity abrirVotacaoPauta(@PathVariable String id, @RequestBody AbrirVotacaoPautaRequest abrirVotacaoPautaRequest) {

        pautaService.abrirVotacaoPauta(id, abrirVotacaoPautaRequest.getMinutosVotacao());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/votar/{id}")
    ResponseEntity votarPauta(@PathVariable String id, @Valid @RequestBody VotarPautaRequest votarPautaRequest) {

        try {
            pautaService.votarPauta(id, votarPautaRequest.getCpf(), votarPautaRequest.getEscolhaVoto());
        } catch (VotoJaComputadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroSimplesResponse(MENSAGEM_VOTO_JA_COMPUTADO));
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/resultado/{id}")
    ResponseEntity<ResultadoPautaResponse> obterResultadoPauta(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.convertValue(pautaService.obterResultadoPauta(id), ResultadoPautaResponse.class));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private Map<String, String> handleValidacaoException(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String erro = error.getDefaultMessage();
            erros.put(campo, erro);
        });

        return erros;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PautaInexistenteException.class)
    private ErroSimplesResponse handlePautaInexistenteException() {
        return new ErroSimplesResponse(MENSAGEM_PAUTA_INEXISTENTE);
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(PautaJaVotadaException.class)
    private ErroSimplesResponse handlePautaJaVotadaException() {
        return new ErroSimplesResponse(MENSAGEM_PAUTA_JA_VOTADA);
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(PautaVotacaoFechadaException.class)
    private ErroSimplesResponse handlePautaFechadaException() {
        return new ErroSimplesResponse(MENSAGEM_PAUTA_FECHADA);
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(CpfInaptoAVotarException.class)
    private ErroSimplesResponse handleCpfInaptoAVotarException() {
        return new ErroSimplesResponse(MENSAGEM_CPF_INAPTO);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CpfInvalidoException.class)
    private ErroSimplesResponse handleCpfInvalidoException() {
        return new ErroSimplesResponse(MENSAGEM_CPF_INVALIDO);
    }
}
