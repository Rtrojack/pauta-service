package br.com.trojack.pauta.api.v1;

import br.com.trojack.pauta.api.v1.request.AbrirVotacaoPautaRequest;
import br.com.trojack.pauta.api.v1.request.PautaRequest;
import br.com.trojack.pauta.api.v1.response.PautaResponse;
import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.exception.PautaJaVotadaException;
import br.com.trojack.pauta.exception.PautaNaoExistenteException;
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

    private PautaService pautaService;
    private ObjectMapper objectMapper;

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

        try {
            pautaService.abrirVotacaoPauta(id, abrirVotacaoPautaRequest.getMinutosVotacao());
        } catch (PautaNaoExistenteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PautaJaVotadaException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
